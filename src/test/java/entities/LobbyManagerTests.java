package entities;

import entities.games.Game;
import entities.games.GameFactory;
import entities.statistics.PerPlayerIntStatistic;
import entities.validity_checkers.ValidityCheckerFacade;
import exceptions.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyManagerTests {

    /**
     * Test Validity Checker Facade which always validates and does not modify input
     */
    static class TestValidityCheckerFacadeTrue extends ValidityCheckerFacade {

        public TestValidityCheckerFacadeTrue() {
            super((p) -> p, (w) -> w);
        }

        @Override
        public String[] isValid(String word) {
            return new String[]{word};
        }
    }

    /**
     * Test Validity Checker Facade which always validates and does not modify input
     */
    static class TestValidityCheckerFacadeFalse extends ValidityCheckerFacade {

        public TestValidityCheckerFacadeFalse() {
            super((p) -> p, (w) -> w);
        }

        @Override
        public String[] isValid(String word) {
            return null;
        }
    }

    private static class CustomizableTestGame extends Game {

        private final Queue<Player> players;



        /**
         * Constructor for CustomizableTestGame
         * @param initialPlayers The initial players in this CustomizableTestGame
         */
        public CustomizableTestGame(Queue<Player> initialPlayers, ValidityCheckerFacade v) {
            super(15, v);
            players = new LinkedList<>(initialPlayers);
        }

        public CustomizableTestGame(Queue<Player> initialPlayers) {
            super(15, new TestValidityCheckerFacadeTrue());
            players = new LinkedList<>(initialPlayers);
        }

        @Override
        public @NotNull Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public boolean isGameOver() {
            return players.size() < 2;
        }

        @Override
        public void onTimerUpdateLogic() {

        }

        @Override
        public Player getPlayerById(String playerId) {
            return players.stream().filter(p -> p.getPlayerId().equals(playerId)).findAny().orElse(null);
        }

        @Override
        public boolean removePlayer(Player playerToRemove) {
            return players.remove(playerToRemove);
        }

        @Override
        public boolean addPlayer(Player playerToAdd) {
            return players.add(playerToAdd);
        }

        @Override
        public boolean switchTurnLogic() {
            setSecondsLeftInCurrentTurn(getSecondsPerTurn());
            return players.add(players.remove());
        }

        @Override
        public @NotNull Player getCurrentTurnPlayer() {
            return this.players.peek();
        }
    }

    private static class CustomizableTestGameFactory extends GameFactory {
        public CustomizableTestGameFactory() {
            super(new PerPlayerIntStatistic[0]);
        }

        /**
         * Accepting any settings, create the appropriate game instance of the CustomizableTestGame
         * @param settings A map of strings to integer settings
         * @param initialPlayers A list of initial players
         * @return the created game instance
         */
        public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
            Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
            return new CustomizableTestGame(queueOfInitialPlayers); // ASSUME WORD TRUE IF GOING THROUGH FACTORY.
        }
    }

    @BeforeEach
    public void setUp() {

    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test that isGameRunning returns true when the game is running.
     */
    @Test
    @Timeout(1)
    public void isGameRunningTrue() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        lobman.setGame(new CustomizableTestGame(new LinkedList<>()));
        assertTrue(lobman.isGameRunning(), "The Game should be running at this point.");
    }

    /**
     * Test that isGameRunning returns false when the game is not running.
     */
    @Test
    @Timeout(1)
    public void isGameRunningFalse() {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        assertFalse(lobman.isGameRunning(), "The Game should not be running at this point.");
    }

    /**
     * Test that isGameNull returns true when the game is null.
     */
    @Test
    @Timeout(1)
    public void isGameNullTrue() {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        assertTrue(lobman.isGameNull(), "The Game should be null at this point.");
    }

    /**
     * Test that isGameNull returns false when the game is not null.
     */
    @Test
    @Timeout(1)
    public void isGameNullFalse() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        lobman.setGame(new CustomizableTestGame(new LinkedList<>()));
        assertFalse(lobman.isGameNull(), "The Game should not be null at this point.");
    }

    /**
     * Test that isGameEnded returns true when the game has ended.
     */
    @Test
    @Timeout(1)
    public void isGameEndedTrue() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // For this bit, I will not end the game via use case, but manually end it through a Game method:
        testGame.setTimerStopped();
        // This should affect directly the following:
        assertTrue(lobman.isGameEnded(), "The Game should have ended.");
    }

    /**
     * Test that isGameEnded returns false when the game has not ended.
     */
    @Test
    @Timeout(1)
    public void isGameEndedFalse() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);
        assertFalse(lobman.isGameEnded(), "The Game should not have ended.");
    }

    /**
     * Test that turns are switched appropriately and successfully.
     */
    @Test
    @Timeout(1)
    public void switchTurn() throws IdInUseException, InvalidDisplayNameException, GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");
        lobman.addPlayerToPool(player1, ppl);
        lobman.addPlayerToPool(player2, ppl);

        Game currGame = lobman.newGameFromPool(new HashMap<>());
        lobman.setGame(currGame);

        assertTrue(currGame.getPlayers().contains(player1), "Player 1 is not in the Game");
        assertTrue(currGame.getPlayers().contains(player2), "Player 2 is not in the Game");

        assertEquals(player1, currGame.getCurrentTurnPlayer(),
                "It should be Player 1's turn, but it isn't.");

        // Here comes the bit we want to test:
        lobman.switchTurn();
        assertEquals(player2, currGame.getCurrentTurnPlayer(),
                "It should be Player 2's turn, but it isn't.");
    }

    /**
     * Test that setGameNull sets a Game as null successfully.
     */
    @Test
    @Timeout(1)
    public void setGameNullTrue() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // Here comes the bit we want to test:
        testGame.setTimerStopped(); // Makes a game null-able
        assertTrue(lobman.isGameEnded(), "The Game should have ended.");
        lobman.setGameNull();
        // This should affect directly the following:
        assertTrue(lobman.isGameNull(), "The Game should have been made null.");
    }

    // This says it is deprecated, but we are using JUnit 4, and AssertThrows was introduced in JUnit 5

    /**
     * Test that setGameNull refuses to set a Game as null when the game is running, and throws GameRunningException.
     */
    @Test
    @Timeout(1)
    public void setGameNullFalse() {

        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        try {
            lobman.setGame(testGame);
        }
        catch (GameRunningException e) {
            fail("AYO THE EXCEPTION SHOULDN'T BE RAISED HERE!!!!");
        }

        assertTrue(lobman.isGameRunning(), "The Game should be running");
        // Here comes the bit we want to test:
        assertThrows(GameRunningException.class, lobman::setGameNull);
    }

    /**
     * Test that removeFromPoolJoin removes a player from the pool and calls onJoinGamePlayer
     */
    @Test
    @Timeout(1)
    public void removeFromPoolJoinValid() throws IdInUseException, InvalidDisplayNameException, GameRunningException,
            GameDoesntExistException, PlayerNotFoundException {

        System.out.println("onJoinGamePlayer should be called in this test.");
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
                System.out.println("onJoinGamePlayer is successfully called ;)");
            }

            @Override
            public void onCancelPlayer() {
                fail("AYO onCancelPlayer SHOULDN'T BE CALLED HERE");
            }

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        lobman.addPlayerToPool(player1, ppl);

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // What we want to test:
        lobman.removeFromPoolJoin(player1);

        // Asserts:
        assertFalse(lobman.getPlayersFromPool().contains(player1), "Player 1 should not be in the pool, but it is.");
        assertEquals(0, lobman.getPlayersFromPool().size(), "There shouldn't be any players in the pool, but there are.");
    }

    /**
     * Test that removeFromPoolJoin throws GameDoesntExistException when the game doesn't exist.
     */
    @Test
    @Timeout(1)
    public void removeFromPoolJoinGameDoesntExist() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        lobman.addPlayerToPool(player1, ppl);

        // Sanity Check Assertion:
        assertTrue(lobman.getPlayersFromPool().contains(player1), "Player 1 should be in the pool, but it is not.");
        // What we want to test:
        assertThrows(GameDoesntExistException.class, () -> lobman.removeFromPoolJoin(player1));
    }

    /**
     * Test that removeFromPoolJoin throws PlayerNotFoundException when the player is not in the pool.
     */
    @Test
    @Timeout(1)
    public void removeFromPoolJoinPlayerNotFound() throws IdInUseException, InvalidDisplayNameException,
            GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);


        Player player1 = lobman.createNewPlayer("player1", "1");

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // Sanity Check Assertions:
        assertFalse(lobman.getPlayersFromPool().contains(player1), "Player 1 should not be in the pool, but it is.");
        assertFalse(testGame.getPlayers().contains(player1), "Player 1 should not be in the game");
        assertEquals(0, lobman.getPlayersFromPool().size(), "There shouldn't be any players in the pool, but there are.");
        // What we want to test:
        assertThrows(PlayerNotFoundException.class, () -> lobman.removeFromPoolJoin(player1));
    }

    /**
     * Test that removeAllFromPoolJoin works as expected, removing all players from the pool and adding them to the Game.
     */
    @Test
    @Timeout(1)
    public void removeAllFromPoolJoin() throws IdInUseException, InvalidDisplayNameException, GameRunningException {
        System.out.println("onJoinGamePlayer should be called.");
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
                System.out.println("onJoinGamePlayer was called successfully ;)");
            }

            @Override
            public void onCancelPlayer() {
                fail("AYO onCancelPlayer SHOULDN'T BE CALLED HERE");
            }

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        // As a sanity check that multiple players are taken at once, we include two players.
        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");

        lobman.addPlayerToPool(player1, ppl);
        lobman.addPlayerToPool(player2, ppl);

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // What we want to test:
        lobman.removeAllFromPoolJoin();
        // Assertions:
        assertEquals(0, lobman.getPlayersFromPool().size(),"No player should be in the pool, but someone still is there.");
    }

    /**
     * Test that removeAllFromPoolCancel works as expected.
     */
    @Test
    @Timeout(1)
    public void removeAllFromPoolCancel() throws IdInUseException, InvalidDisplayNameException {
        System.out.println("This test requires onCancelPlayer to be called twice, once for each player.");
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
                fail("AYO onJoinGamePlayer SHOULDN'T BE CALLED");
            }

            @Override
            public void onCancelPlayer() {
                System.out.println("This is what should be called! Test fully passed ;)");
            }

            @Override
            public Lock getLock() {
                return lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");

        lobman.addPlayerToPool(player1, ppl);
        lobman.addPlayerToPool(player2, ppl);

        // Sanity Checks:
        assertTrue(lobman.getPlayersFromPool().contains(player1), "Player 1 should be in the pool, but it isn't");
        assertTrue(lobman.getPlayersFromPool().contains(player2), "Player 2 should be in the pool, but it isn't");
        // What we want to test:
        lobman.removeAllFromPoolCancel();
        // Final Assertions:
        assertFalse(lobman.getPlayersFromPool().contains(player1), "Player 1 shouldn't be in the pool, but it is.");
        assertFalse(lobman.getPlayersFromPool().contains(player2), "Player 2 shouldn't be in the pool, but it is.");
        assertEquals(0, lobman.getPlayersFromPool().size(),"Nobody should be in the pool, but someone is.");

    }

    /**
     * Test that getPlayersFromPool returns all players as expected.
     */
    @Test
    @Timeout(1)
    public void getPlayersFromPool() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
            }

            @Override
            public void onCancelPlayer() {
            }

            @Override
            public Lock getLock() {
                return lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");

        lobman.addPlayerToPool(player1, ppl);
        lobman.addPlayerToPool(player2, ppl);

        // Our Assertions:
        assertTrue(lobman.getPlayersFromPool().contains(player1), "Player 1 should be in the pool, but it isn't");
        assertTrue(lobman.getPlayersFromPool().contains(player2), "Player 2 should be in the pool, but it isn't");
    }

    /**
     * Test that addWord successfully adds a valid word.
     */
    @Test
    @Timeout(1)
    public void addWordValid() throws IdInUseException, InvalidDisplayNameException, GameDoesntExistException,
            GameRunningException, InvalidWordException, OutOfTurnException, PlayerNotFoundException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
            }

            @Override
            public void onCancelPlayer() {
            }

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        lobman.addPlayerToPool(player1, ppl);

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);
        lobman.addPlayerToGame(player1);

        // What we want to test:
        lobman.addWord("bloop", "1");
        // Assertions:
        assertEquals("bloop ", testGame.getStoryString(), "testGame should just have bloop in the string.");
    }

    /**
     * Test that addWord throws GameDoesntExist when the game doesn't exist.
     */
    @Test
    @Timeout(1)
    public void addWordGameDoesntExist() throws
            IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
            }

            @Override
            public void onCancelPlayer() {
            }

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        lobman.addPlayerToPool(player1, ppl);

        // What we want to test, and fail:
        assertThrows(GameDoesntExistException.class, () -> lobman.addWord("bloop", "player1"));
    }

    /**
     * Test that addWord throws PlayerNotFound when the player is not found.
     */
    @Test
    @Timeout(1)
    public void addWordPlayerNotFound() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        assertThrows(PlayerNotFoundException.class, () -> lobman.addWord("bloop", "player1"));
    }

    /**
     * Test that addWord throws OutOfTurn when the player is out of turn.
     */
    @Test
    @Timeout(1)
    public void addWordOutOfTurn() throws GameDoesntExistException, IdInUseException, InvalidDisplayNameException, GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        lobman.addPlayerToGame(player1);
        lobman.addPlayerToGame(player2);

        // Sanity Check:
        assertTrue(testGame.getPlayers().contains(player1), "Player 1 should be in the game, but it isn't.");
        assertTrue(testGame.getPlayers().contains(player2), "Player 2 should be in the game, but it isn't.");
        assertEquals(player1, testGame.getCurrentTurnPlayer(), "It should be player 1's turn, but it isn't.");
        assertNotSame(player2, testGame.getCurrentTurnPlayer(), "It shouldn't be player 2's turn, but it is.");
        // What we want to test, and fail:
        assertThrows(OutOfTurnException.class, () -> lobman.addWord("bloop", "2"));
    }

    /**
     * Test that addWord refuses an invalid word.
     */
    @Test
    @Timeout(1)
    public void addWordInValid() throws IdInUseException, InvalidDisplayNameException, GameDoesntExistException,
            GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>(),
                new TestValidityCheckerFacadeFalse());
        lobman.setGame(testGame);

        lobman.addPlayerToGame(player1);
        lobman.addPlayerToGame(player2);

        // The Exception we want:
        assertTrue(testGame.getPlayers().contains(player1), "Player 1 should be in the game, but it isn't.");
        // What we want to test, and fail:
        assertThrows(InvalidWordException.class, () -> lobman.addWord("bloop", "1"));
    }

    /**
     * Test that newGameFromPool works as intended.
     */
    @Test
    @Timeout(1)
    public void testNewGameFromPool() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");

        lobman.addPlayerToPool(player1, ppl);
        lobman.addPlayerToPool(player2, ppl);

        // What we want to test:
        Game testGame = lobman.newGameFromPool(new HashMap<>());
        // Assertions:
        assertTrue(testGame.getPlayers().contains(player1),
                "The game should have Player1, but it doesn't.");
        assertTrue(testGame.getPlayers().contains(player2),
                "The game should have Player2, but it doesn't.");
    }

    @Test
    @Timeout(1)
    public void removeFromPoolCancel() throws IdInUseException, InvalidDisplayNameException, PlayerNotFoundException {
        System.out.println("onCancelPlayer should be called in this test.");
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
                fail("AYO onJoinGamePlayer SHOULDN'T BE CALLED HERE");
            }

            @Override
            public void onCancelPlayer() {
                System.out.println("onCancelPlayer was called successfully ;)");
            }

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");
        lobman.addPlayerToPool(player1, ppl);

        // What we want to test:
        lobman.removeFromPoolCancel(player1);

        // Asserts:
        assertFalse(lobman.getPlayersFromPool().contains(player1), "Player 1 should not be in the pool, but it is.");
        assertEquals(0, lobman.getPlayersFromPool().size(),"There shouldn't be any players in the pool, but there are.");
    }

    @Test
    @Timeout(1)
    public void removePlayerFromGame() throws GameDoesntExistException, PlayerNotFoundException, GameRunningException, IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        Player player1 = lobman.createNewPlayer("player1", "1");

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        lobman.addPlayerToGame(player1);
        // Sanity Check:
        assertTrue(testGame.getPlayers().contains(player1), "Player 1 should be in the game, but it isn't.");

        // What we want to test:
        lobman.removePlayerFromGame(player1);
        // Assertion:
        assertFalse(testGame.getPlayers().contains(player1), "Player 1 should no longer be in the game, but it still is.");
    }

    @Test
    @Timeout(1)
    public void addPlayerToGame() throws GameDoesntExistException, GameRunningException,
            IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        Player player1 = lobman.createNewPlayer("player1", "1");

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // What we want to test:
        lobman.addPlayerToGame(player1);
        // Assertion:
        assertTrue(testGame.getPlayers().contains(player1), "Player 1 should be in the game, but it isn't.");
    }

    @Test
    @Timeout(1)
    public void createNewPlayer() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        // What we want to test:
        Player player1 = lobman.createNewPlayer("player1", "1");
        // Assertion:
        assertEquals("player1", player1.getDisplayName(), "The DisplayName should be player1, but it isn't.");
        assertEquals("1", player1.getPlayerId(), "The ID should be 1, but it isn't.");
    }

    @Test
    @Timeout(1)
    public void addPlayerToPool() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {}

            @Override
            public void onCancelPlayer() {}

            @Override
            public Lock getLock() {
                return this.lock;
            }
        };

        Player player1 = lobman.createNewPlayer("player1", "1");

        // What we want to test:
        lobman.addPlayerToPool(player1, ppl);
        // Assertions:
        assertTrue(lobman.getPlayersFromPool().contains(player1), "Player 1 should be in the pool, but it isn't.");
    }
}
