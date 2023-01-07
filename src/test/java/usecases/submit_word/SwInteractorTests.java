package usecases.submit_word;

import entities.LobbyManager;
import entities.Player;
import entities.PlayerFactory;
import entities.PlayerPoolListener;
import entities.display_name_checkers.DisplayNameChecker;
import entities.games.Game;
import entities.games.GameFactory;
import entities.validity_checkers.ValidityCheckerFacade;
import exceptions.GameRunningException;
import exceptions.IdInUseException;
import exceptions.InvalidDisplayNameException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.ThreadRegister;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static usecases.Response.ResCode.*;

public class SwInteractorTests {

    private static final ThreadRegister register = new ThreadRegister();

    private static class GameTest extends Game {
        public static final int REGULAR_GAME_SECONDS_PER_TURN = 15;

        private final Queue<Player> players;

        /**
         * Constructor for a Game
         * @param initialPlayers The players that will be included into the new GameTest
         * @param v              The validity checker (to check if a word is valid)
         */
        public GameTest(Queue<Player> initialPlayers, ValidityCheckerFacade v) {
            super(REGULAR_GAME_SECONDS_PER_TURN, v);
            players = new LinkedList<>(initialPlayers);
        }

        /**
         * @return Returns all the present players in the game
         */
        @Override
        public @NotNull Collection<Player> getPlayers() {return this.players;}

        /**
         * @return Returns whether the game is over
         */
        @Override
        public boolean isGameOver() {return players.size() < 2;}

        /**
         * Additional actions that can be done by the game every time the timer is updated
         */
        @Override
        public void onTimerUpdateLogic() {

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
        public boolean switchTurnLogic() {
            setSecondsLeftInCurrentTurn(getSecondsPerTurn());
            return players.add(players.remove());
        }

        /**
         * Returns the player whose turn it is
         */
        @Override
        public @NotNull Player getCurrentTurnPlayer() {return players.peek();}
    }

    /**
     * Validates everything, does not modify input
     */
    static class TestValidityCheckerTrue extends ValidityCheckerFacade {

        public TestValidityCheckerTrue() {
            super((p) -> p, (w) -> w);
        }

        /**
         * Checks whether the word is valid
         * @param word the word we need to check
         * @return true
         */
        @Override
        public String[] isValid(String word) {
            return new String[]{word};
        }
    }

    /**
     * Rejects everything
     */
    static class TestValidityCheckerFalse extends ValidityCheckerFacade {

        public TestValidityCheckerFalse() {
            super((p) -> p, (w) -> w);
        }

        /**
         * Checks whether the word is valid
         * @param word the word we need to check
         * @return false
         */
        @Override
        public String[] isValid(String word) {
            return null;
        }
    }

    @BeforeEach
    public void setUp() {}

    @AfterEach
    public void tearDown() {
    
    }

    /**
     * Tests creating Game, no timer, but the player submits word out of turn.
     */
    @Test
    @Timeout(10000)
    public void testOutOfTurn() throws IdInUseException, GameRunningException, InvalidDisplayNameException {

        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class GameFactoryTest extends GameFactory {
            /**
            * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
            */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new TestValidityCheckerTrue());
            }
        }

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
        String word = "word";

        lobman.addPlayerToPool(player1, ppl);
        lobman.addPlayerToPool(player2, ppl);

        Game currGame = lobman.newGameFromPool(new HashMap<>());
        lobman.setGame(currGame);

        assertTrue(currGame.getPlayers().contains(player1), "Player 1 is not in the Game");
        assertTrue(currGame.getPlayers().contains(player2), "Player 2 is not in the Game");

        assertEquals(player1, currGame.getCurrentTurnPlayer(), "It should be Player 1's turn, but it isn't.");

        SwOutputBoundary pres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord){
                fail("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???");
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                assertEquals("It is not player " + "2" + "'s turn.",
                        outputDataFailure.getResponse().getMessage(),
                        "Response message is not correct");

                assertEquals(OUT_OF_TURN, outputDataFailure.getResponse().getCode(),
                        "Response code is not correct");

                assertEquals("2", outputDataFailure.getPlayerId(),
                        "Offending Player ID is not correct.");

                System.out.println("The invalid presenter code block was called successfully :)");
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };

        SwInputData swinput = new SwInputData(word, player2.getPlayerId());
        SwInteractor swint = new SwInteractor(lobman, register);
        swint.submitWord(swinput, pres);

        System.out.println("bloop");

        }

    /**
     * Tests creating Game, no timer, but the player submits word when they don't exist.
     */
    @Test
    @Timeout(10000)
    public void testPlayerNotFound() throws IdInUseException, GameRunningException, InvalidDisplayNameException {

        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class GameFactoryTest extends GameFactory {
            /**
             * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
             */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new TestValidityCheckerTrue());
            }
        }

        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac);

        Player player1 = lobman.createNewPlayer("player1", "1");
        String word = "word";

        Game currGame = lobman.newGameFromPool(new HashMap<>());
        lobman.setGame(currGame);

        assertFalse(currGame.getPlayers().contains(player1), "Player 1 shouldn't be in the Game");

        SwOutputBoundary pres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord){
                fail("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???");
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                assertEquals("Player with ID " + "1" + " does not exist or is not in the Game.",
                        outputDataFailure.getResponse().getMessage(),
                        "Response message is not correct");

                assertEquals(PLAYER_NOT_FOUND, outputDataFailure.getResponse().getCode(),
                        "Response code is not correct");

                assertEquals("1", outputDataFailure.getPlayerId(),
                        "Offending Player ID is not correct.");

                System.out.println("The invalid presenter code block was called successfully! :)");
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };

        SwInputData swinput = new SwInputData(word, player1.getPlayerId());

        SwInteractor swint = new SwInteractor(lobman, register);
        swint.submitWord(swinput, pres);

        System.out.println("Test ran to end successfully! :)");
    }

    /**
     * Tests submitting a word to a non-existent Game.
     */
    @Test
    @Timeout(10000)
    public void testGameDoesntExist() throws IdInUseException, InvalidDisplayNameException {

        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class GameFactoryTest extends GameFactory {
            /**
             * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
             */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new TestValidityCheckerTrue());
            }
        }

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
        String word = "word";

        lobman.addPlayerToPool(player1, ppl);

        SwOutputBoundary pres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord){
                fail("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???");
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                assertEquals("The Game you are trying to submit a word to doesn't exist",
                        outputDataFailure.getResponse().getMessage(),
                        "Response message is not correct");

                assertEquals(GAME_DOESNT_EXIST, outputDataFailure.getResponse().getCode(),
                        "Response code is not correct");

                assertEquals("1", outputDataFailure.getPlayerId(),
                        "Offending Player ID is not correct.");

                System.out.println("The invalid presenter code block was called successfully! :)");
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };

        SwInputData swinput = new SwInputData(word, player1.getPlayerId());

        SwInteractor swint = new SwInteractor(lobman, register);
        swint.submitWord(swinput, pres);

        System.out.println("Test ran to end successfully! :)");
    }

    /**
     * Tests submitting a word that is invalid, with a DisplayNameChecker that always returns false.
     */
    @Test
    @Timeout(10000)
    public void testInvalidWord() throws IdInUseException, GameRunningException, InvalidDisplayNameException {
        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class GameFactoryTest extends GameFactory {
            /**
             * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
             */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new TestValidityCheckerFalse());
            }
        }

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
        String word = "bloop";

        lobman.addPlayerToPool(player1, ppl);

        Game currGame = lobman.newGameFromPool(new HashMap<>());
        lobman.setGame(currGame);

        assertTrue(currGame.getPlayers().contains(player1), "Player 1 is not in the Game");

        assertEquals(player1, currGame.getCurrentTurnPlayer(), "It should be Player 1's turn, but it isn't.");

        SwOutputBoundary pres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord){
                fail("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???");
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                assertEquals("The word 'bloop' is not valid, please try another word.",
                        outputDataFailure.getResponse().getMessage(),
                        "Response message is not correct");

                assertEquals(INVALID_WORD, outputDataFailure.getResponse().getCode(),
                        "Response code is not correct");

                assertEquals("1", outputDataFailure.getPlayerId(),
                        "Offending Player ID is not correct.");

                System.out.println("The invalid presenter code block was called successfully! :)");
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };

        SwInputData swinput = new SwInputData(word, player1.getPlayerId());
        SwInteractor swint = new SwInteractor(lobman, register);
        swint.submitWord(swinput, pres);

        System.out.println("Test ran to end successfully! :)");

    }


    /**
     * Tests submitting a word that is valid.
     */
    @Test
    @Timeout(10000)
    public void testValidWord() throws IdInUseException, GameRunningException, InvalidDisplayNameException {
        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class GameFactoryTest extends GameFactory {
            /**
             * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
             */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new TestValidityCheckerTrue());
            }
        }

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
        String word = "bloop";

        lobman.addPlayerToPool(player1, ppl);

        Game currGame = lobman.newGameFromPool(new HashMap<>());
        lobman.setGame(currGame);

        assertTrue(currGame.getPlayers().contains(player1), "Player 1 is not in the Game");

        assertEquals(player1, currGame.getCurrentTurnPlayer(), "It should be Player 1's turn, but it isn't.");

        SwOutputBoundary pres = new SwOutputBoundary() {

            @Override
            public void valid(SwOutputDataValidWord swOutputDataValidWord) {
                assertEquals("Word 'bloop' has been added!",
                        swOutputDataValidWord.getResponse().getMessage(),
                        "Response message is not correct");

                assertEquals(SUCCESS, swOutputDataValidWord.getResponse().getCode(),
                        "Response code is not correct");

                assertEquals("1", swOutputDataValidWord.getPlayerId(),
                        "Player ID is not correct.");

                System.out.println("The valid presenter code block was called successfully! :)");
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure){
                fail("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL INVALID???");
            }

            @Override
            public void outputShutdownServer() {
                throw new RuntimeException("This method is not implemented and should not be called");
            }
        };

        SwInputData swinput = new SwInputData(word, player1.getPlayerId());
        SwInteractor swint = new SwInteractor(lobman, register);
        swint.submitWord(swinput, pres);

        System.out.println("Test ran to end successfully! :)");
    }
}
