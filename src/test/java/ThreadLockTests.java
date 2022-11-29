import entities.*;
import entities.games.Game;
import entities.games.GameFactory;

import exceptions.IdInUseException;
import exceptions.InvalidDisplayNameException;

import org.junit.jupiter.api.*;

import usecases.disconnecting.*;
import usecases.join_public_lobby.*;
import usecases.pull_data.PdInputBoundary;
import usecases.pull_game_ended.PgeInputBoundary;
import usecases.sort_players.SpInteractor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class ThreadLockTests {

    private static final int REPEAT_TIMES = 1;

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

        // int newint = new Random().nextInt(4);
        int newint = 1;
        switch (newint) {
            case 0 :
                jplInteractor.joinPublicLobby(jplInputData);
                dcInteractor.disconnect(dcInputData);
                timer.scheduleAtFixedRate(spTimerTask, 0, 100);
                break;
            case 1 :
                System.out.println("STEP 1: BEFORE JPL");
                System.out.println("GAME LOCK: " + lobman.getGameLock().toString());
                System.out.println("PLAYER POOL LOCK: " + lobman.getPlayerPoolLock().toString());

                jplInteractor.joinPublicLobby(jplInputData);
                System.out.println("STEP 2: AFTER JPL, BEFORE TIMER STARTS");
                System.out.println("GAME LOCK: " + lobman.getGameLock().toString());
                System.out.println("PLAYER POOL LOCK: " + lobman.getPlayerPoolLock().toString());

                timer.scheduleAtFixedRate(spTimerTask, 0, 100);
                System.out.println("STEP 3: AFTER TIMER STARTS, BEFORE DC");
                System.out.println("GAME LOCK: " + lobman.getGameLock().toString());
                System.out.println("PLAYER POOL LOCK: " + lobman.getPlayerPoolLock().toString());

                dcInteractor.disconnect(dcInputData);
                System.out.println("STEP 4: AFTER DC");
                System.out.println("GAME LOCK: " + lobman.getGameLock().toString());
                System.out.println("PLAYER POOL LOCK: " + lobman.getPlayerPoolLock().toString());
                break;
            case 2 :
                dcInteractor.disconnect(dcInputData);
                timer.scheduleAtFixedRate(spTimerTask, 0, 100);
                jplInteractor.joinPublicLobby(jplInputData);
                break;
            case 3 :
                dcInteractor.disconnect(dcInputData);
                jplInteractor.joinPublicLobby(jplInputData);
                timer.scheduleAtFixedRate(spTimerTask, 0, 100);
                break;
        }

        System.out.println("STEP 5: BEFORE FIRST WHILE LOOP");
        System.out.println("GAME LOCK: " + lobman.getGameLock().toString());
        System.out.println("PLAYER POOL LOCK: " + lobman.getPlayerPoolLock().toString());

        // spTimerTask.run();
        while(!dcFlag.get() | !jplFlagPool.get()) {
            Thread.onSpinWait();
        }

        System.out.println("STEP 6: AFTER FIRST WHILE LOOP");
        System.out.println("GAME LOCK: " + lobman.getGameLock().toString());
        System.out.println("PLAYER POOL LOCK: " + lobman.getPlayerPoolLock().toString());

        lobman.getPlayerPoolLock().lock();
        lobman.getGameLock().lock();

        System.out.println("STEP 7: AFTER BOTH LOCKS HAPPEN");
        System.out.println("GAME LOCK: " + lobman.getGameLock().toString());
        System.out.println("PLAYER POOL LOCK: " + lobman.getPlayerPoolLock().toString());

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
            // spTimerTask.run();
            while (!jplFlagJoined.get()) { // If this never happens, test timeout will make the test crash.
                // spTimerTask.run();
            }

            while (true) { // If this never happens, test timeout will make the test crash.
                lobman.getGameLock().lock();
                boolean nullbool = lobman.isGameNull();
                lobman.getGameLock().unlock();
                if (nullbool) {break;}
                // spTimerTask.run();
            }
            lobman.getGameLock().lock();
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

}
