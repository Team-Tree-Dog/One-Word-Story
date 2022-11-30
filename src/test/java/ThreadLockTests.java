import entities.*;
import entities.games.Game;
import entities.games.GameFactory;

import exceptions.*;

import org.junit.jupiter.api.*;

import usecases.disconnecting.*;
import usecases.join_public_lobby.*;
import usecases.pull_data.PdInputBoundary;
import usecases.pull_game_ended.PgeInputBoundary;
import usecases.run_game.RgInteractor;
import usecases.sort_players.SpInteractor;
import usecases.submit_word.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;


public class ThreadLockTests {

    private static final int REPEAT_TIMES = 50;

    private static class GameTest extends Game {
        public static final int REGULAR_GAME_SECONDS_PER_TURN = 15;
        private final Queue<Player> players;

        /**
         * Constructor for a Game
         * @param initialPlayers The players that will be included into the new GameTest
         * @param v              The validity checker (to check if a word is valid)
         */
        public GameTest(Queue<Player> initialPlayers, ValidityChecker v) {
            super(REGULAR_GAME_SECONDS_PER_TURN, v);
            players = new LinkedList<>(initialPlayers);
        }

        /**
         * @return Returns all the present players in the game
         */
        @Override
        public Collection<Player> getPlayers() {return this.players;}

        /**
         * @return Returns whether the game is over
         */
        @Override
        public boolean isGameOver() {return players.size() < 2;}

        /**
         * Additional actions that can be done by the game every time the timer is updated
         */
        @Override
        public void onTimerUpdate() {

        }

        /**
         * Returns the player by its id
         * @param playerId The player's ID
         */
        @Override
        public Player getPlayerById(String playerId) {
            return players.stream().filter(p -> p.getPlayerId().equals(playerId)).findAny().orElse(null);
        }

        /**
         * Removes the player specified from this GameRegular instance
         * @param playerToRemove The Player to be removed
         * @return if the player was successfully removed
         */
        @Override
        public boolean removePlayer(Player playerToRemove)  {return players.remove(playerToRemove);}

        /**
         * Adds new player to the game
         * @param playerToAdd The new player.
         */
        @Override
        public boolean addPlayer(Player playerToAdd) {return players.add(playerToAdd);}

        /**
         * Switches this game's turn and resets the timer
         */
        @Override
        public boolean switchTurn() {
            setSecondsLeftInCurrentTurn(getSecondsPerTurn());
            return players.add(players.remove());
        }

        /**
         * Returns the player whose turn it is
         */
        @Override
        public Player getCurrentTurnPlayer() {return players.peek();}
    }

    private static class LocalDisplayName implements DisplayNameChecker {
        @Override
        public boolean checkValid(String displayName) {
            return true;
        }
    }

    private static class LocalValidityChecker implements ValidityChecker{

        /**
         * Checks whether the word is valid
         * @param word the word we need to check
         * @return true, since the valid presenter code block will be triggered if the test doesn't go as planned.
         */
        @Override
        public boolean isValid(String word) {
            return true;
        }
    }

    private static class GameFactoryTest implements GameFactory {
        /**
         * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
         */
        public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
            Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
            return new ThreadLockTests.GameTest(queueOfInitialPlayers, new LocalValidityChecker());
        }
    }

    @BeforeEach
    public void setUp() {}

    @AfterEach
    public void tearDown() {}

    /**
     * Tests the lock architecture, for the case in which one player joins the pool, but another disconnects.
     * Player 1 is in the pool, Player 2 joins the pool, and simultaneously Player 1 calls disconnect.
     * Threads: SP, JPL, DC
     * JPL2, DC1, SP: Only Player 2 remains in the pool, no game started.
     * JPL2, SP, DC1, SP: Game starts, but then Player 1 disconnects, so game cannot continue.
     * DC1, SP, JPL2, SP: Only Player 2 remains in the pool, no game started.
     * DC1, JPL2, SP: Only Player 2 remains in the pool, no game started.
     * We end up having only two scenarios which are predicted to happen. We test that any of these two scenarios happens.
     */
    @RepeatedTest(REPEAT_TIMES)
    @Timeout(10)
    public void testPlayerEntersPlayerDisconnects() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}

            @Override
            public Lock getLock() {
                return lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");

        lobman.addPlayerToPool(player1, ppl);

        // Everything above is already a given.
        // For adding player 2 to the pool, we use the JoinPublicLobby use case.

        AtomicBoolean jplFlagPool = new AtomicBoolean(false);
        AtomicBoolean jplFlagJoined = new AtomicBoolean(false);
        AtomicBoolean jplCancel = new AtomicBoolean(false);
        JplInputData jplInputData = new JplInputData("player2", "2");
        JplOutputBoundary jplPres = new JplOutputBoundary() {
            @Override
            public void inPool(JplOutputDataResponse dataJoinedPool) {
                jplFlagPool.set(true);
            }

            @Override
            public void inGame(JplOutputDataJoinedGame dataJoinedGame) {
                jplFlagJoined.set(true);
            }

            @Override
            public void cancelled(JplOutputDataResponse dataCancelled) {
                jplCancel.set(true);
            }
        };
        JplInteractor jplInteractor = new JplInteractor(lobman, jplPres);

        AtomicBoolean dcFlag = new AtomicBoolean(false);
        DcInputData dcInputData = new DcInputData(player1.getPlayerId());
        DcOutputBoundary dcPres = data -> dcFlag.set(true);
        DcInteractor dcInteractor = new DcInteractor(lobman, dcPres);

        PgeInputBoundary pgeInputBoundary = data -> {};
        PdInputBoundary pdInputBoundary = d -> {};
        SpInteractor spinny = new SpInteractor(lobman, pgeInputBoundary, pdInputBoundary);
        SpInteractor.SpTask spTimerTask = spinny.new SpTask();
        Timer timer = new Timer();

        int newint = new Random().nextInt(4);
        System.out.println("Case Number: " + newint);
        switch (newint) {
            case 0 :
                jplInteractor.joinPublicLobby(jplInputData);
                dcInteractor.disconnect(dcInputData);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                break;
            case 1 :
                jplInteractor.joinPublicLobby(jplInputData);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                dcInteractor.disconnect(dcInputData);
                break;
            case 2 :
                dcInteractor.disconnect(dcInputData);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                jplInteractor.joinPublicLobby(jplInputData);
                break;
            case 3 :
                dcInteractor.disconnect(dcInputData);
                jplInteractor.joinPublicLobby(jplInputData);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                break;
        }

        System.out.println("Switch over.");
        while(!dcFlag.get() | !jplFlagPool.get()) {
            Thread.onSpinWait();
        }

        lobman.getPlayerPoolLock().lock();
        lobman.getGameLock().lock();

        Player ghost = new Player("", "2"); // Ghost Player.

        // Now we detect scenarios.
        if (lobman.getPlayersFromPool().contains(ghost)) {
            // In this case, Scenario 1 is possible.
            assertTrue(lobman.isGameNull(),"A game shouldn't have started i.e. should be null.");
            assertFalse(lobman.getPlayersFromPool().contains(player1),"The pool shouldn't have Player 1.");
            System.out.println("Scenario 1 happened: Player 2 remains in the pool, Player 1 disconnected, no game exists.");

            // Cancel 2nd player from pool so JPL can end (teardown procedure)
            dcInteractor.disconnect(new DcInputData("2"));
        }
        else {
            // In this case, Scenario 2 is possible.
            lobman.getGameLock().unlock();
            lobman.getPlayerPoolLock().unlock(); // These unlock methods let SpTimer do its thing.

            while (!jplFlagJoined.get()) { // If this never happens, test timeout will make the test crash.
                Thread.onSpinWait();
            }

            while (true) { // If this never happens, test timeout will make the test crash.
                lobman.getGameLock().lock(); // Locking here will not let SpTimer do its thing.
                boolean nullbool = lobman.isGameNull();
                lobman.getGameLock().unlock(); //  See two lines before.
                if (nullbool) {break;}
            }

            lobman.getGameLock().lock();
            lobman.getPlayerPoolLock().lock(); // LOCKS BEFORE ASSERTIONS

            // So the game is over.
            assertEquals(0, lobman.getPlayersFromPool().size(), "No player should be in the pool, but someone is.");
            assertTrue(jplFlagJoined.get(), "If you can read this, no game was ever created when it should have been."); // So we know the game was created, but now vanished.

            System.out.println("Scenario 2 happened.");
        }

        lobman.getPlayerPoolLock().unlock();
        lobman.getGameLock().unlock();
        lobman.getSortPlayersTimer().cancel();
        spTimerTask.cancel();
    }

    /**
     * Tests the case in which one player submits a word, but loses connection before the word is processed.
     * In some cases, the player could also "submit a word", but in reality the request never happens because the
     * player is lagged out, so the request is received by the controller after the player disconnected.
     * We ensure in this test that there are more than two players to not immediately stop a game.
     *
     * Outcomes:
     * SW1, RG, DC1: The word will be processed, turn is switched by RG, and then the player disconnects.
     * SW1, DC1, RG: The word is processed, then player disconnects, so turn is switched to next player. RG works as normal.
     * DC1, RG, SW1: The player disconnects, so turn is switched to next player. RG works as normal, then SW1 raises PlayerNotFound.
     * DC1, SW1, RG: The player disconnects, so turn is switched to next player. SW1 raises PlayerNotFound. RG works as normal.
     * Same cases if PD goes first.
     */
    @RepeatedTest(REPEAT_TIMES)
    @Timeout(10)
    public void testPlayerSubmitsPlayerDisconnects() throws IdInUseException, InvalidDisplayNameException, GameRunningException {
        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}

            @Override
            public Lock getLock() {
                return lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");
        Player player3 = lobman.createNewPlayer("player3", "3");

        lobman.addPlayerToPool(player1, ppl);
        lobman.addPlayerToPool(player2, ppl);
        lobman.addPlayerToPool(player3, ppl);

        Game currGame = lobman.newGameFromPool(new HashMap<>());
        lobman.setGame(currGame);

        assertTrue(currGame.getPlayers().contains(player1), "Player 1 is not in the Game");
        assertTrue(currGame.getPlayers().contains(player2), "Player 2 is not in the Game");
        assertTrue(currGame.getPlayers().contains(player3), "Player 3 is not in the Game");

        assertEquals(player1, currGame.getCurrentTurnPlayer(), "It should be Player 1's turn, but it isn't.");

        // At this point, setup and sanity asssertions finish.
        // We now build the interactors:
        SwInputData swInputData = new SwInputData("bloop", player1.getPlayerId());
        AtomicBoolean swFlag = new AtomicBoolean(false);
        SwOutputBoundary swPres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord) {
                swFlag.set(true);
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                fail("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???");
            }
        };
        SwInteractor swInteractor = new SwInteractor(swPres, lobman);

        AtomicBoolean dcFlag = new AtomicBoolean(false);
        DcInputData dcInputData = new DcInputData(player1.getPlayerId());
        DcOutputBoundary dcPres = data -> dcFlag.set(true);
        DcInteractor dcInteractor = new DcInteractor(lobman, dcPres);

        PgeInputBoundary pgeInputBoundary = data -> {};
        PdInputBoundary pdInputBoundary = d -> {};
        RgInteractor rgInteractor = new RgInteractor(currGame, pgeInputBoundary, pdInputBoundary, lobman.getGameLock());
        RgInteractor.RgTask rgTimerTask = rgInteractor.new RgTask();
        Timer timer = new Timer();

        int newint = new Random().nextInt(4);
        switch (newint) {
            case 0 : // SW1, RG, DC1: Word processed, player disconnects.
                swInteractor.submitWord(swInputData);
                timer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                dcInteractor.disconnect(dcInputData);
                break;
            case 1 : // SW1, DC1, RG: Word processed, player disconnects.
                swInteractor.submitWord(swInputData);
                dcInteractor.disconnect(dcInputData);
                timer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                break;
            case 2 : // DC1, RG, SW1: PlayerNotFound, word not processed.
                dcInteractor.disconnect(dcInputData);
                timer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                assertThrows(PlayerNotFoundException.class,
                        () -> swInteractor.submitWord(swInputData));
                break;
            case 3 : // DC1, SW1, RG: PlayerNotFound, word not processed.
                dcInteractor.disconnect(dcInputData);
                assertThrows(PlayerNotFoundException.class,
                        () -> swInteractor.submitWord(swInputData));
                timer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                break;
        }
        System.out.println("Case number: " + newint);

        // We first need to make sure DC and SW have finished their respective threads.
        while(!swFlag.get() | !dcFlag.get()) {
            Thread.onSpinWait();
        }

        System.out.println("Test right now locking both...");
        lobman.getPlayerPoolLock().lock();
        lobman.getGameLock().lock();
        System.out.println("Test has locked both!");

        // We can distinguish between both cases if the word (bloop) is in the story or not.

        if (newint == 0 | newint == 1) {
            System.out.println("Scenario 1 happens. The word gets added to the story.");
            assertEquals("bloop ", currGame.getStory().toString(),
                    "The story should have the word in it, but it doesn't.");
        }
        else {
            System.out.println("Scenario 2 happens. The word shouldn't have been added to the story.");
            assertEquals("", currGame.getStory().toString(),
                    "The story should have nothing in it, but something is there.");
        }


        // In both cases, Player 2's turn shouldn't have finished because we have set the turn time at 15 seconds,
        // more than the timeout of the test, which is 10 seconds.
        assertEquals(player2, currGame.getCurrentTurnPlayer(), "It should be Player 2's turn, but it isn't.");
        // We also test that Player 1 is gone for good.
        assertFalse(currGame.getPlayers().contains(player1), "Player 1 should no longer be in the game, but it still is.");
        assertFalse(lobman.getPlayersFromPool().contains(player1), "Player 1 shouldn't be in the pool, but it still is.");

        // Cancel threads and unlock everything.
        System.out.println("Everything is unlocked now.");
        lobman.getPlayerPoolLock().unlock();
        lobman.getGameLock().unlock();
        rgTimerTask.cancel();
    }
}
