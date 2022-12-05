import entities.*;
import entities.games.Game;
import entities.games.GameFactory;

import exceptions.*;

import org.junit.jupiter.api.*;

import usecases.Response;
import usecases.disconnecting.*;
import usecases.join_public_lobby.*;
import usecases.pull_data.PdInputBoundary;
import usecases.pull_game_ended.*;
import usecases.run_game.RgInteractor;
import usecases.sort_players.SpInteractor;
import usecases.submit_word.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static usecases.Response.ResCode.*;


public class ThreadLockTests {

    private static final int REPEAT_TIMES = 100;

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

        public GameTest(List<Player> initialPlayers, ValidityChecker v) {
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
    public void testPlayerEntersPlayerDisconnects() throws IdInUseException, InvalidDisplayNameException, InterruptedException {
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
        int randTime1 = new Random().nextInt(20);
        int randTime2 = new Random().nextInt(20);
        System.out.println("Case Number: " + newint);
        switch (newint) {
            case 0 :
                jplInteractor.joinPublicLobby(jplInputData);
                Thread.sleep(randTime1);
                dcInteractor.disconnect(dcInputData);
                Thread.sleep(randTime2);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                break;
            case 1 :
                jplInteractor.joinPublicLobby(jplInputData);
                Thread.sleep(randTime1);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                Thread.sleep(randTime2);
                dcInteractor.disconnect(dcInputData);
                break;
            case 2 :
                dcInteractor.disconnect(dcInputData);
                Thread.sleep(randTime1);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                Thread.sleep(randTime2);
                jplInteractor.joinPublicLobby(jplInputData);
                break;
            case 3 :
                dcInteractor.disconnect(dcInputData);
                Thread.sleep(randTime1);
                jplInteractor.joinPublicLobby(jplInputData);
                Thread.sleep(randTime2);
                System.out.println("SpTimer should now start");
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                System.out.println("SpTimer should have just started.");
                break;
        }

        System.out.println("Switch over.");
        while(!dcFlag.get() | !jplFlagPool.get()) {
            System.out.println("Inside while loop.");
            Thread.onSpinWait();
        }

        Player ghost = new Player("", "2"); // Ghost Player.

        System.out.println("Test wants to lock PlayerPool.");
        lobman.getPlayerPoolLock().lock();
        System.out.println("Test locked PlayerPool!");

        // Now we detect scenarios.
        if (lobman.getPlayersFromPool().contains(ghost)) {
            System.out.println("Enter Scenario 1.");
            // In this case, Scenario 1 is possible.
            System.out.println("Test wants to lock Game.");
            lobman.getGameLock().lock();
            System.out.println("Test locked Game!");
            assertTrue(lobman.isGameNull(),"A game shouldn't have started i.e. should be null.");
            assertFalse(lobman.getPlayersFromPool().contains(player1),"The pool shouldn't have Player 1.");
            System.out.println("Scenario 1 happened: Player 2 remains in the pool, Player 1 disconnected, no game exists.");

            // Cancel 2nd player from pool so JPL can end (teardown procedure)
            dcInteractor.disconnect(new DcInputData("2"));
            // If the above line would be commented out, Java would still force-terminate the JPL thread.
        }
        else {
            System.out.println("Enter Scenario 2.");
            // In this case, Scenario 2 is possible.
            lobman.getPlayerPoolLock().unlock(); // This unlock method lets SpTimer do its thing.
            System.out.println("Test unlocked PlayerPool!");

            while (!jplFlagJoined.get()) { // If this never happens, test timeout will make the test crash.
                Thread.onSpinWait();
            }

            while (true) { // If this never happens, test timeout will make the test crash.
                lobman.getGameLock().lock();
                boolean nullbool = lobman.isGameNull();
                lobman.getGameLock().unlock();
                if (nullbool) {break;}
            }
            System.out.println("Test wants to lock Game.");
            lobman.getGameLock().lock();
            System.out.println("Test locked Game!");
            System.out.println("Test wants to lock PlayerPool.");
            lobman.getPlayerPoolLock().lock();
            System.out.println("Test locked PlayerPool!"); // LOCKS BEFORE ASSERTIONS

            // So the game is over.
            assertEquals(0, lobman.getPlayersFromPool().size(), "No player should be in the pool, but someone is.");
            assertTrue(jplFlagJoined.get(), "If you can read this, no game was ever created when it should have been."); // So we know the game was created, but now vanished.

            System.out.println("Scenario 2 happened.");
        }

        System.out.println("Unlock Everything.");
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
     * Outcomes:
     * SW1, RG, DC1: The word will be processed, turn is switched by RG, and then the player disconnects.
     * SW1, DC1, RG: The word is processed, then player disconnects, so turn is switched to next player. RG works as normal.
     * DC1, RG, SW1: The player disconnects, so turn is switched to next player. RG works as normal, then SW1 raises PlayerNotFound.
     * DC1, SW1, RG: The player disconnects, so turn is switched to next player. SW1 raises PlayerNotFound. RG works as normal.
     * Same cases if PD goes first.
     */
    @RepeatedTest(REPEAT_TIMES)
    @Timeout(10)
    public void testPlayerSubmitsPlayerDisconnects() throws IdInUseException, InvalidDisplayNameException, GameRunningException, GameDoesntExistException {
        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac);

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");
        Player player3 = lobman.createNewPlayer("player3", "3");

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);

        Game currGame = new GameTest(players, new LocalValidityChecker());
        lobman.setGame(currGame);

        assertTrue(lobman.getPlayersFromGame().contains(player1), "Player 1 is not in the Game");
        assertTrue(lobman.getPlayersFromGame().contains(player2), "Player 2 is not in the Game");
        assertTrue(lobman.getPlayersFromGame().contains(player3), "Player 3 is not in the Game");

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
                System.out.println("VALID SHOULD BE CALLED IF THIS IS CASE 0 OR 1");
                swFlag.set(true);
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                // This should only happen with a PlayerNotFound exception.
                System.out.println("PLAYER_NOT_FOUND SHOULD BE CALLED IF THIS IS CASE 2 OR 3");
                Response newResp = outputDataFailure.getResponse();
                assertEquals(PLAYER_NOT_FOUND, newResp.getCode(),
                        "The response should be PLAYER_NOT_FOUND for any case, but it isn't.");
                assertEquals("Player with ID 1 does not exist or is not in the Game.", newResp.getMessage(),
                        "The response message is not correct.");
                swFlag.set(true);
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
        System.out.println("Case number: " + newint);
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
                swInteractor.submitWord(swInputData);
                break;
            case 3 : // DC1, SW1, RG: PlayerNotFound, word not processed.
                dcInteractor.disconnect(dcInputData);
                swInteractor.submitWord(swInputData);
                timer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                break;
        }

        // We first need to make sure DC and SW have finished their respective threads.
        while(!swFlag.get() | !dcFlag.get()) {
            Thread.onSpinWait();
        }

        System.out.println("Test right now locking both...");
        lobman.getPlayerPoolLock().lock();
        lobman.getGameLock().lock();
        System.out.println("Test has locked both!");

        // We can distinguish between both cases if the word (bloop) is in the story or not.
        // This, however, is the only difference between the two cases.
        assertTrue("bloop ".equals(currGame.getStory().toString()) | "".equals(currGame.getStory().toString()),
                "The story should either be bloop or nothing, but it is somehow neither");

        // We can now test using the elements the two cases have in common (which is everything except the above assert).
        // In both cases, Player 2's turn shouldn't have finished because we have set the turn time at 15 seconds,
        // more than the timeout of the test, which is 10 seconds.
        assertEquals(player2, currGame.getCurrentTurnPlayer(), "It should be Player 2's turn, but it isn't.");
        // We also test that Player 1 is gone for good.
        assertFalse(currGame.getPlayers().contains(player1), "Player 1 should no longer be in the game, but it still is.");
        assertFalse(lobman.getPlayersFromPool().contains(player1), "Player 1 shouldn't be in the pool, but it still is.");

        // Cancel threads and unlock everything.
        System.out.println("Everything is unlocked now.");
        lobman.getGameLock().unlock();
        lobman.getPlayerPoolLock().unlock();
        rgTimerTask.cancel();
    }

    /**
     * Tests the case in which one player enters a lobby using JPL but immediately loses connection.
     * This can be simulated as JPL and DC happening at approximately the same time.
     * The only persistent thread that has a role is SP, but only because it engages/disengages locks.
     * SP won't actually change anything, but still needs to be included since it will be part of the thread arch.
     * Possible Outcomes:
     * JPL1, DC1, SP: The player joins, then disconnects. No net change.
     * JPL1, SP, DC1: The player joins, then disconnects. No net change.
     * DC1, SP, JPL1: The player attempts to disconnect, responding PlayerNotFound. Player then joins the game successfully.
     * DC1, JPL1, SP: The player attempts to disconnect, responding PlayerNotFound. Player then joins the game successfully.
     * We end up having only two scenarios which are predicted to happen. We test that any of these two scenarios happens.
     */
    @RepeatedTest(REPEAT_TIMES)
    @Timeout(10)
    public void testJoinsThenDisconnects() throws InterruptedException {
        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac);

        // Setup is above.
        // We now introduce all flags and use-case objects:

        AtomicBoolean jplFlagPool = new AtomicBoolean(false);
        AtomicBoolean jplFlagJoined = new AtomicBoolean(false);
        AtomicReference<Response.ResCode> codeJpl = new AtomicReference<>();
        AtomicBoolean jplCancel = new AtomicBoolean(false);
        JplInputData jplInputData = new JplInputData("player1", "1");
        JplOutputBoundary jplPres = new JplOutputBoundary() {
            @Override
            public void inPool(JplOutputDataResponse dataJoinedPool) {
                jplFlagPool.set(true);
                codeJpl.set(dataJoinedPool.getRes().getCode());
            }

            @Override
            public void inGame(JplOutputDataJoinedGame dataJoinedGame) {
                jplFlagJoined.set(true);
                codeJpl.set(dataJoinedGame.getRes().getCode());
            }

            @Override
            public void cancelled(JplOutputDataResponse dataCancelled) {
                jplCancel.set(true);
                codeJpl.set(dataCancelled.getRes().getCode());
            }
        };
        JplInteractor jplInteractor = new JplInteractor(lobman, jplPres);

        AtomicBoolean dcFlag = new AtomicBoolean(false);
        AtomicReference<Response.ResCode> codeDc = new AtomicReference<>();
        DcInputData dcInputData = new DcInputData("1");
        DcOutputBoundary dcPres = data -> {
            codeDc.set(data.getResponse().getCode());
            dcFlag.set(true);
        };
        DcInteractor dcInteractor = new DcInteractor(lobman, dcPres);

        PgeInputBoundary pgeInputBoundary = data -> {};
        PdInputBoundary pdInputBoundary = d -> {};
        SpInteractor spinny = new SpInteractor(lobman, pgeInputBoundary, pdInputBoundary);
        SpInteractor.SpTask spTimerTask = spinny.new SpTask();
        Timer timer = new Timer();

        int newint = new Random().nextInt(4);
        int randTime1 = new Random().nextInt(20);
        int randTime2 = new Random().nextInt(20);
        System.out.println("Case Number: " + newint);
        switch (newint) {
            case 0 :
                jplInteractor.joinPublicLobby(jplInputData);
                Thread.sleep(randTime1);
                dcInteractor.disconnect(dcInputData);
                Thread.sleep(randTime2);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                break;
            case 1 :
                jplInteractor.joinPublicLobby(jplInputData);
                Thread.sleep(randTime1);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                Thread.sleep(randTime2);
                dcInteractor.disconnect(dcInputData);
                break;
            case 2 :
                dcInteractor.disconnect(dcInputData);
                Thread.sleep(randTime1);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                Thread.sleep(randTime2);
                jplInteractor.joinPublicLobby(jplInputData);
                break;
            case 3 :
                dcInteractor.disconnect(dcInputData);
                Thread.sleep(randTime1);
                jplInteractor.joinPublicLobby(jplInputData);
                Thread.sleep(randTime2);
                timer.scheduleAtFixedRate(spTimerTask, 0, 1000);
                break;
        }

        System.out.println("Switch over.");
        System.out.println("Before while loop.");
        while(!dcFlag.get() | !jplFlagPool.get() | codeJpl.get() == null) {
            Thread.onSpinWait();
        }
        System.out.println("After while loop.");

        Player ghost = new Player("", "1"); // Ghost Player.

        System.out.println("Test wants to lock PlayerPool.");
        lobman.getPlayerPoolLock().lock();
        System.out.println("Test locked PlayerPool!");

        // We can distinguish between both scenarios based on if Player 1 is in the pool.
        if (!lobman.getPlayersFromPool().contains(ghost)) {
            System.out.println("Scenario 1 happens: Player 1 is not in the pool.");

            // As a sanity check, we need to make sure no one is in the pool at this time.
            assertEquals(0, lobman.getPlayersFromPool().size(),
                    "No one should be in the pool, but someone is.");
            // Now, we need to check that the player joined at some point. We can check this using response code.
            assertEquals(SUCCESS, codeJpl.get(),
                    "The code returned by JPL should be SUCCESS, but it isn't this code.");
            // We also need to check that the player disconnected. We can check this using response code.
            assertEquals(SUCCESS, codeDc.get(),
                    "The code returned by DC should be SUCCESS, but it isn't this code.");
        }
        else {
            System.out.println("Scenario 2 happens: Player 1 is in the pool.");

            // As a sanity check, we need to make sure Player 1 is the only one in the pool at this time.
            ArrayList<Player> playerArrayList = new ArrayList<>();
            playerArrayList.add(new Player("player1", "1"));
            assertEquals(playerArrayList, lobman.getPlayersFromPool(),
                    "No one should be in the pool, but someone is.");
            // Now, we need to check that the player joined at some point. We can check this using response code.
            assertEquals(SUCCESS, codeJpl.get(),
                    "The code returned by JPL should be SUCCESS, but it isn't this code.");
            // We also need to check that the player couldn't disconnect because it didn't exist in the pool.
            // DcInteractor checks in game after checking in pool, and should find the game doesn't exist.
            // We can check this using response code.
            assertEquals(GAME_DOESNT_EXIST, codeDc.get(),
                    "The code returned by DC should be GAME_DOESNT_EXIST, but it isn't this code.");
        }
        System.out.println("Unlock everything.");
        lobman.getPlayerPoolLock().unlock();
        lobman.getSortPlayersTimer().cancel();
        spTimerTask.cancel();
    }

    /**
     * Tests when a player disconnects when the game over condition is reached. Assume there is only one player in the
     * game. The test game over condition is that less than two players are in the game, so we only set the game
     * according to this condition before starting the threads.
     * Outcomes:
     * RG, DC, SP: RG detects game over, cancels game timer, notifies that game ended, flags presenter to "disconnect"
     *             player. DC disconnects player from game/pool. SP sets game to null. SCE 1.
     * RG, SP, DC: RG detects game over, cancels game timer, notifies that game ended, flags presenter to "disconnect"
     *             player. SP sets game to null. DC cannot disconnect player from game/pool. SCE 2.
     * SP, RG, DC: SP does nothing. RG detects game over, cancels game timer, notifies that game ended, flags
     *             presenter to "disconnect" player. DC disconnects player from game/pool. SCE 1.
     * SP, DC, RG: SP does nothing. DC disconnects player from game/pool. RG detects game over, cancels game timer,
     *             notifies that game ended, but detects no players to "disconnect". SCE 3.
     * DC, SP, RG: DC disconnects player from game/pool. SP does nothing. RG detects game over, cancels game timer,
     *             notifies that game ended, but detects no players to "disconnect". SCE 3.
     * DC, RG, SP: DC disconnects player from game/pool. RG detects game over, cancels game timer,
     *             notifies that game ended, but detects no players to "disconnect". SP sets game to null. SCE 3.
     */
    @RepeatedTest(REPEAT_TIMES)
    @Timeout(10)
    public void testRGAndDCGameOver() throws IdInUseException, InvalidDisplayNameException, GameRunningException, GameDoesntExistException {
        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac);

        Player player1 = lobman.createNewPlayer("player 1", "1");

        List<Player> players = new ArrayList<>();
        players.add(player1);

        Game currGame = new GameTest(players, new LocalValidityChecker());
        lobman.setGame(currGame);

        assertEquals(0, lobman.getPlayersFromPool().size(),
                "No one should be in the pool, but someone is.");
        assertTrue(lobman.getPlayersFromGame().contains(player1), "Player 1 is not in the Game");
        assertTrue(currGame.getPlayers().contains(player1), "Player 1 is not in the Game");
        assertEquals(player1, currGame.getCurrentTurnPlayer(), "It should be Player 1's turn, but it isn't.");

        // At this point, setup and sanity asssertions finish.
        // We now build the interactors:

        // RG:
        AtomicReference<String[]> playersPge = new AtomicReference<>();
        AtomicBoolean pgeFlag = new AtomicBoolean(false);
        PgeOutputBoundary pgePres = data -> {
            playersPge.set(data.getPlayerIds());
            pgeFlag.set(true);
            //if (!pgeFlag.get()) { // So that playersPge only changes if RG never ended.
              //  playersPge.set(data.getPlayerIds());
              //  pgeFlag.set(true);
            System.out.println("players PGE:" + Arrays.toString(playersPge.get()));
            // }
        };
        PgeInteractor pgeInteractor = new PgeInteractor(pgePres);
        PdInputBoundary pdInputBoundary = d -> {};
        RgInteractor rgInteractor = new RgInteractor(currGame, pgeInteractor, pdInputBoundary, lobman.getGameLock());
        RgInteractor.RgTask rgTimerTask = rgInteractor.new RgTask();
        Timer rgTimer = new Timer();

        // SP:
        SpInteractor spinny = new SpInteractor(lobman, pgeInteractor, pdInputBoundary);
        SpInteractor.SpTask spTimerTask = spinny.new SpTask();
        Timer spTimer = new Timer();

        // DC:
        AtomicBoolean dcFlag = new AtomicBoolean(false);
        AtomicReference<String> messageDc = new AtomicReference<>();
        DcInputData dcInputData = new DcInputData(player1.getPlayerId());
        DcOutputBoundary dcPres = data -> {
            messageDc.set(data.getResponse().getMessage());
            dcFlag.set(true);
            System.out.println("DC Message: " + messageDc.get());
        };
        DcInteractor dcInteractor = new DcInteractor(lobman, dcPres);

        int newint = new Random().nextInt(6);
        System.out.println("Case number: " + newint);
        switch (newint) {
            case 0 : // RG, DC, SP
                rgTimer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                dcInteractor.disconnect(dcInputData);
                spTimer.scheduleAtFixedRate(spTimerTask, 0, 50);
                break;
            case 1 : // RG, SP, DC
                rgTimer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                spTimer.scheduleAtFixedRate(spTimerTask, 0, 50);
                dcInteractor.disconnect(dcInputData);
                break;
            case 2 : // SP, RG, DC
                spTimer.scheduleAtFixedRate(spTimerTask, 0, 50);
                rgTimer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                dcInteractor.disconnect(dcInputData);
                break;
            case 3 : // SP, DC, RG
                spTimer.scheduleAtFixedRate(spTimerTask, 0, 50);
                dcInteractor.disconnect(dcInputData);
                rgTimer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                break;
            case 4 : // DC, SP, RG
                dcInteractor.disconnect(dcInputData);
                spTimer.scheduleAtFixedRate(spTimerTask, 0, 50);
                rgTimer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                break;
            case 5 : // DC, RG, SP
                dcInteractor.disconnect(dcInputData);
                rgTimer.scheduleAtFixedRate(rgTimerTask, 0, 50);
                spTimer.scheduleAtFixedRate(spTimerTask, 0, 50);
                break;
        }
        System.out.println("Switch Over");

        // We first need to make sure DC has finished its thread, as well as RG.
        while(!dcFlag.get()) {
            Thread.onSpinWait();
        }
        assertTrue(dcFlag.get(), "How did we get out of the while loop if dcFlag is still false?");

        while(!pgeFlag.get()) {
            Thread.onSpinWait();
        }
        assertTrue(pgeFlag.get(), "How did we get out of the while loop if pgeFlag is still false?");

        // Now, we need to stop both RG and SP before their next iterations. The cancel() method will still let both
        // TimerTasks finish their current iteration if there is one running.
        rgTimerTask.cancel();
        spTimerTask.cancel();
        System.out.println("All Tasks Canceled.");

        System.out.println("Test right now locking PlayerPool...");
        lobman.getPlayerPoolLock().lock();
        System.out.println("Test has locked PlayerPool!");

        System.out.println("Test right now locking Game..");
        lobman.getGameLock().lock();
        System.out.println("Test has locked Game!");

        // We can switch between either scenarios 1&2, or scenario 3, based on if RG detected a player to "disconnect".
        // Scenario 3 happens if RG didn't detect a player to disconnect.
        // Then, switch between either scenario 1 or scenario 2, based on if DC was able to disconnect.
        // If DC was not able to disconnect (scenario 2), SP should have already set the game to null.

        if (playersPge.get().length == 0) {
            System.out.println("Scenario 3: RG notified that no players were still in-game, " +
                    "so DC should have detected a player to disconnect (and disconnected the player).");
            // In this case, DC should have detected a player to disconnect.
            // We don't care if the game was set to null or not.

            // Assert that the player was removed from the game entity:
            assertFalse(currGame.getPlayers().contains(player1),
                    "Player 1 shouldn't be in the game anymore, but it still is.");
            // And that DC should have disconnected the player:
            assertEquals("Disconnecting was successful.", messageDc.get(),
                    "The message is supposed to be Disconnecting was successful, but it isn't.");
        } else if (Arrays.equals(playersPge.get(), new String[]{player1.getPlayerId()})){
            if (messageDc.get().equals("Player not found")) {
                System.out.println("Scenario 2: RG notified player 1 was still in-game, " +
                        "and DC didn't detect any players in-game, so SP should have already set the game to null.");

                // Sanity assert that DC couldn't remove players from the game entity:
                assertTrue(currGame.getPlayers().contains(player1),
                        "Player 1 should still be in the game, but it isn't.");
                // Check that the game was already set to null:
                assertTrue(lobman.isGameNull());
            }
            else if (messageDc.get().equals("Disconnecting was successful.")){
                System.out.println("Scenario 1: RG notified player 1 was still in-game, " +
                        "and DC detected a player still in the game.");
                // There isn't really much to assert here, other than the general asserts.
            }
            else {fail("Something unexpected happened with DC, based on the message.");}
        } else {fail("playersPGE should either have only player 1 or no players, but it's different.");}

        // In any case, SP should end up setting the game to null.
        // We can let SP do that by starting up a new TimerTask/Timer.

        SpInteractor.SpTask spTimerTaskNew = spinny.new SpTask();
        Timer spTimerNew = new Timer();

        // Unlock Game and PlayerPool:
        lobman.getGameLock().unlock();
        System.out.println("Test has unlocked Game!");
        lobman.getPlayerPoolLock().unlock();
        System.out.println("Test has unlocked PlayerPool!");

        System.out.println("Trying to set game to null...");
        spTimerNew.scheduleAtFixedRate(spTimerTaskNew, 0, 100);
        while (true) { // If this never happens, test timeout will make the test crash, so this functions as an assert.
            System.out.println("Inside null while loop.");
            lobman.getGameLock().lock();
            boolean nullbool = lobman.isGameNull();
            lobman.getGameLock().unlock();
            if (nullbool) {break;}
        }
        spTimerNew.cancel(); // And cancel.
        // Actually, sanity assert this just in case.
        assertTrue(lobman.isGameNull(), "Why did it break out of the while loop, but the game is still null?");
        System.out.println("Test has ended.");
    }
}
