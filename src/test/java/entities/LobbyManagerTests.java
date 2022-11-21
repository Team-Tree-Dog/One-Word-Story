package entities;

import entities.games.Game;
import entities.games.GameFactory;
import exceptions.*;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LobbyManagerTests {

    private static class CustomizableTestGame extends Game {

        private final Queue<Player> players;

        /**
         * Constructor for CustomizableTestGame
         * @param initialPlayers The initial players in this CustomizableTestGame
         */
        public CustomizableTestGame(Queue<Player> initialPlayers, ValidityChecker v) {
            super(15, v);
            players = new LinkedList<>(initialPlayers);
        }

        public CustomizableTestGame(Queue<Player> initialPlayers) {
            super(15, word -> true);
            players = new LinkedList<>(initialPlayers);
        }

        @Override
        public Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public boolean isGameOver() {
            return players.size() < 2;
        }

        @Override
        public void onTimerUpdate() {

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
        public boolean switchTurn() {
            setSecondsLeftInCurrentTurn(getSecondsPerTurn());
            return players.add(players.remove());
        }

        @Override
        public Player getCurrentTurnPlayer() {
            return this.players.peek();
        }
    }

    private static class CustomizableTestGameFactory implements GameFactory {
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

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    /**
     * Test that isGameRunning returns true when the game is running.
     */
    @Test(timeout = 1000)
    public void isGameRunningTrue() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        lobman.setGame(new CustomizableTestGame(new LinkedList<>()));
        assertTrue("The Game should be running at this point.", lobman.isGameRunning());
    }

    /**
     * Test that isGameRunning returns false when the game is not running.
     */
    @Test(timeout = 1000)
    public void isGameRunningFalse() {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        assertFalse("The Game should not be running at this point.", lobman.isGameRunning());
    }

    /**
     * Test that isGameNull returns true when the game is null.
     */
    @Test(timeout = 1000)
    public void isGameNullTrue() {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        assertTrue("The Game should be null at this point.", lobman.isGameNull());
    }

    /**
     * Test that isGameNull returns false when the game is not null.
     */
    @Test(timeout = 1000)
    public void isGameNullFalse() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        lobman.setGame(new CustomizableTestGame(new LinkedList<>()));
        assertFalse("The Game should not be null at this point.", lobman.isGameNull());
    }

    /**
     * Test that isGameEnded returns true when the game has ended.
     */
    @Test(timeout = 1000)
    public void isGameEndedTrue() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // For this bit, I will not end the game via use case, but manually end it through a Game method:
        testGame.setTimerStopped();
        // This should affect directly the following:
        assertTrue("The Game should have ended.", lobman.isGameEnded());
    }

    /**
     * Test that isGameEnded returns false when the game has not ended.
     */
    @Test(timeout = 1000)
    public void isGameEndedFalse() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);
        assertFalse("The Game should not have ended.", lobman.isGameEnded());
    }

    /**
     * Test that turns are switched appropriately and successfully.
     */
    @Test(timeout = 1000)
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

        assertTrue("Player 1 is not in the Game", currGame.getPlayers().contains(player1));
        assertTrue("Player 2 is not in the Game", currGame.getPlayers().contains(player2));

        assertEquals("It should be Player 1's turn, but it isn't.", player1, currGame.getCurrentTurnPlayer());

        // Here comes the bit we want to test:
        lobman.switchTurn();
        assertEquals("It should be Player 2's turn, but it isn't.", player2, currGame.getCurrentTurnPlayer());
    }

    /**
     * Test that setGameNull sets a Game as null successfully.
     */
    @Test(timeout = 1000)
    public void setGameNullTrue() throws GameRunningException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // Here comes the bit we want to test:
        testGame.setTimerStopped(); // Makes a game null-able
        assertTrue("The Game should have ended.", lobman.isGameEnded());
        lobman.setGameNull();
        // This should affect directly the following:
        assertTrue("The Game should have been made null.", lobman.isGameNull());
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    // This says it is deprecated, but we are using JUnit 4, and AssertThrows was introduced in JUnit 5

    /**
     * Test that setGameNull refuses to set a Game as null when the game is running, and throws GameRunningException.
     */
    @Test(timeout = 1000)
    public void setGameNullFalse() throws GameRunningException {

        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        try {
            lobman.setGame(testGame);
        }
        catch (GameRunningException e) {
            assertEquals("AYO THE EXCEPTION SHOULDN'T BE RAISED HERE!!!!", 1, 2);
        }

        assertTrue("The Game should be running", lobman.isGameRunning());
        // Here comes the bit we want to test:
        exceptionRule.expect(GameRunningException.class);
        lobman.setGameNull();
    }

    /**
     * Test that removeFromPoolJoin removes a player from the pool and calls onJoinGamePlayer
     */
    @Test(timeout = 1000)
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
                assertEquals("AYO onCancelPlayer SHOULDN'T BE CALLED HERE", 1, 2);
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
        assertFalse("Player 1 should not be in the pool, but it is.", lobman.getPlayersFromPool().contains(player1));
        assertEquals("There shouldn't be any players in the pool, but there are.",
                lobman.getPlayersFromPool(), new ArrayList<Player>());
    }

    /**
     * Test that removeFromPoolJoin throws GameDoesntExistException when the game doesn't exist.
     */
    @Test(timeout = 1000)
    public void removeFromPoolJoinGameDoesntExist() throws IdInUseException, InvalidDisplayNameException,
            GameDoesntExistException, PlayerNotFoundException {
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
        assertTrue("Player 1 should be in the pool, but it is not.", lobman.getPlayersFromPool().contains(player1));
        // Our exception:
        exceptionRule.expect(GameDoesntExistException.class);
        // What we want to test:
        lobman.removeFromPoolJoin(player1);
    }

    /**
     * Test that removeFromPoolJoin throws PlayerNotFoundException when the player is not in the pool.
     */
    @Test(timeout = 1000)
    public void removeFromPoolJoinPlayerNotFound() throws IdInUseException, InvalidDisplayNameException,
            GameRunningException, GameDoesntExistException, PlayerNotFoundException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);


        Player player1 = lobman.createNewPlayer("player1", "1");

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // Sanity Check Assertions:
        assertFalse("Player 1 should not be in the pool, but it is.", lobman.getPlayersFromPool().contains(player1));
        assertFalse("Player 1 should not be in the game", testGame.getPlayers().contains(player1));
        assertEquals("There shouldn't be any players in the pool, but there are.",
                lobman.getPlayersFromPool(), new ArrayList<Player>());
        // Our exception:
        exceptionRule.expect(PlayerNotFoundException.class);
        // What we want to test:
        lobman.removeFromPoolJoin(player1);
    }

    /**
     * Test that removeAllFromPoolJoin works as expected, removing all players from the pool and adding them to the Game.
     */
    @Test(timeout = 1000)
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
                assertEquals("AYO onCancelPlayer SHOULDN'T BE CALLED HERE", 1, 2);
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
        assertEquals("No player should be in the pool, but someone still is there.",
                lobman.getPlayersFromPool(), new ArrayList<Player>());
    }

    /**
     * Test that removeAllFromPoolCancel works as expected.
     */
    @Test(timeout = 1000)
    public void removeAllFromPoolCancel() throws IdInUseException, InvalidDisplayNameException {
        System.out.println("This test requires onCancelPlayer to be called twice, once for each player.");
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
                assertEquals("AYO onJoinGamePlayer SHOULDN'T BE CALLED", 1, 2);
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
        assertTrue("Player 1 should be in the pool, but it isn't", lobman.getPlayersFromPool().contains(player1));
        assertTrue("Player 2 should be in the pool, but it isn't", lobman.getPlayersFromPool().contains(player2));
        // What we want to test:
        lobman.removeAllFromPoolCancel();
        // Final Assertions:
        assertFalse("Player 1 shouldn't be in the pool, but it is.", lobman.getPlayersFromPool().contains(player1));
        assertFalse("Player 2 shouldn't be in the pool, but it is.", lobman.getPlayersFromPool().contains(player2));
        assertEquals("Nobody should be in the pool, but someone is.",
                new ArrayList<Player>(), lobman.getPlayersFromPool());

    }

    /**
     * Test that getPlayersFromPool returns all players as expected.
     */
    @Test(timeout = 1000)
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
        assertTrue("Player 1 should be in the pool, but it isn't", lobman.getPlayersFromPool().contains(player1));
        assertTrue("Player 2 should be in the pool, but it isn't", lobman.getPlayersFromPool().contains(player2));
    }

    /**
     * Test that addWord successfully adds a valid word.
     */
    @Test(timeout = 1000)
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
        assertEquals("testGame should just have bloop in the string.",
                "bloop ", testGame.getStory().toString());
    }

    /**
     * Test that addWord throws GameDoesntExist when the game doesn't exist.
     */
    @Test(timeout = 1000)
    public void addWordGameDoesntExist() throws GameDoesntExistException, InvalidWordException, OutOfTurnException,
            PlayerNotFoundException, IdInUseException, InvalidDisplayNameException {
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

        // The Exception we want:
        exceptionRule.expect(GameDoesntExistException.class);
        // What we want to test, and fail:
        lobman.addWord("bloop", "player1");
    }

    /**
     * Test that addWord throws PlayerNotFound when the player is not found.
     */
    @Test(timeout = 1000)
    public void addWordPlayerNotFound() throws GameRunningException,
            GameDoesntExistException, InvalidWordException, OutOfTurnException, PlayerNotFoundException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        // The Exception we want:
        exceptionRule.expect(PlayerNotFoundException.class);
        // What we want to test, and fail:
        lobman.addWord("bloop", "player1");
    }

    /**
     * Test that addWord throws OutOfTurn when the player is out of turn.
     */
    @Test(timeout = 1000)
    public void addWordOutOfTurn() throws GameDoesntExistException, IdInUseException, InvalidDisplayNameException, GameRunningException, InvalidWordException, OutOfTurnException, PlayerNotFoundException {
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
        assertTrue("Player 1 should be in the game, but it isn't.", testGame.getPlayers().contains(player1));
        assertTrue("Player 2 should be in the game, but it isn't.", testGame.getPlayers().contains(player2));
        assertEquals("It should be player 1's turn, but it isn't.", player1, testGame.getCurrentTurnPlayer());
        assertNotSame("It shouldn't be player 2's turn, but it is.", player2, testGame.getCurrentTurnPlayer());
        // The Exception we want:
        exceptionRule.expect(OutOfTurnException.class);
        // What we want to test, and fail:
        lobman.addWord("bloop", "2");
    }

    /**
     * Test that addWord refuses an invalid word.
     */
    @Test(timeout = 1000)
    public void addWordInValid() throws IdInUseException, InvalidDisplayNameException, GameDoesntExistException,
            GameRunningException, InvalidWordException, OutOfTurnException, PlayerNotFoundException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        Player player1 = lobman.createNewPlayer("player1", "1");
        Player player2 = lobman.createNewPlayer("player2", "2");

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>(), word -> false);
        lobman.setGame(testGame);

        lobman.addPlayerToGame(player1);
        lobman.addPlayerToGame(player2);

        // The Exception we want:
        assertTrue("Player 1 should be in the game, but it isn't.", testGame.getPlayers().contains(player1));
        exceptionRule.expect(InvalidWordException.class);
        // What we want to test, and fail:
        lobman.addWord("bloop", "1");
    }

    /**
     * Test that newGameFromPool works as intended.
     */
    @Test(timeout = 1000)
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
        assertTrue("The game should have Player1, but it doesn't.",
                testGame.getPlayers().contains(player1));
        assertTrue("The game should have Player2, but it doesn't.",
                testGame.getPlayers().contains(player2));
    }

    @Test(timeout = 1000)
    public void removeFromPoolCancel() throws IdInUseException, InvalidDisplayNameException, PlayerNotFoundException {
        System.out.println("onCancelPlayer should be called in this test.");
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);
        PlayerPoolListener ppl = new PlayerPoolListener() {

            private final Lock lock = new ReentrantLock();

            @Override
            public void onJoinGamePlayer(Game game) {
                assertEquals("AYO onJoinGamePlayer SHOULDN'T BE CALLED HERE", 1, 2);
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
        assertFalse("Player 1 should not be in the pool, but it is.", lobman.getPlayersFromPool().contains(player1));
        assertEquals("There shouldn't be any players in the pool, but there are.",
                lobman.getPlayersFromPool(), new ArrayList<Player>());
    }

    @Test(timeout = 1000)
    public void removePlayerFromGame() throws GameDoesntExistException, PlayerNotFoundException, GameRunningException, IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        Player player1 = lobman.createNewPlayer("player1", "1");

        CustomizableTestGame testGame = new CustomizableTestGame(new LinkedList<>());
        lobman.setGame(testGame);

        lobman.addPlayerToGame(player1);
        // Sanity Check:
        assertTrue("Player 1 should be in the game, but it isn't.", testGame.getPlayers().contains(player1));

        // What we want to test:
        lobman.removePlayerFromGame(player1);
        // Assertion:
        assertFalse("Player 1 should no longer be in the game, but it still is.", testGame.getPlayers().contains(player1));
    }

    @Test(timeout = 1000)
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
        assertTrue("Player 1 should be in the game, but it isn't.", testGame.getPlayers().contains(player1));
    }

    @Test(timeout = 1000)
    public void createNewPlayer() throws IdInUseException, InvalidDisplayNameException {
        PlayerFactory playerfac = new PlayerFactory(displayName -> true);
        GameFactory gamefac = new CustomizableTestGameFactory();
        LobbyManager lobman = new LobbyManager(playerfac, gamefac);

        // What we want to test:
        Player player1 = lobman.createNewPlayer("player1", "1");
        // Assertion:
        assertEquals("The DisplayName should be player1, but it isn't.",
                "player1", player1.getDisplayName());
        assertEquals("The ID should be 1, but it isn't.","1", player1.getPlayerId());
    }

    @Test(timeout = 1000)
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
        assertTrue("Player 1 should be in the pool, but it isn't.", lobman.getPlayersFromPool().contains(player1));
    }
}
