package usecases.submit_word;

import entities.*;
import entities.games.Game;
import entities.games.GameFactory;
import exceptions.GameRunningException;
import exceptions.IdInUseException;
import exceptions.InvalidDisplayNameException;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static usecases.Response.ResCode.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SwInteractorTests {

    private static Lock playerPoolLock;

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
    @Before
    public void setUp() {
        playerPoolLock = new ReentrantLock();
    }

    @After
    public void tearDown() {
    
    }

    /**
     * Tests creating Game, no timer, but the player submits word out of turn.
     */
    @Test(timeout = 10000)
    public void testOutOfTurn() throws IdInUseException, GameRunningException, InvalidDisplayNameException {

        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class LocalValidityChecker implements ValidityChecker{

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

        class GameFactoryTest implements GameFactory {
            /**
            * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
            */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new LocalValidityChecker());
            }
        }

        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac, playerPoolLock);
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

        assertTrue("Player 1 is not in the Game", currGame.getPlayers().contains(player1));
        assertTrue("Player 2 is not in the Game", currGame.getPlayers().contains(player2));

        assertEquals("It should be Player 1's turn, but it isn't.", player1, currGame.getCurrentTurnPlayer());

        SwOutputBoundary pres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord){
                assertEquals("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???", 1,2);
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                assertEquals("Response message is not correct",
                        "It is not player " + "2" + "'s turn.",
                        outputDataFailure.getResponse().getMessage());

                assertEquals("Response code is not correct", OUT_OF_TURN,
                        outputDataFailure.getResponse().getCode());

                assertEquals("Offending Player ID is not correct.", "2",
                        outputDataFailure.getPlayerId());

                System.out.println("The invalid presenter code block was called successfully :)");
            }
        };

        SwInputData swinput = new SwInputData(word, player2.getPlayerId());
        SwInteractor swint = new SwInteractor(pres, lobman);
        swint.submitWord(swinput);

        System.out.println("bloop");

        }

    /**
     * Tests creating Game, no timer, but the player submits word when they don't exist.
     */
    @Test(timeout = 10000)
    public void testPlayerNotFound() throws IdInUseException, GameRunningException, InvalidDisplayNameException {

        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class LocalValidityChecker implements ValidityChecker{

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

        class GameFactoryTest implements GameFactory {
            /**
             * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
             */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new LocalValidityChecker());
            }
        }

        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac, playerPoolLock);

        Player player1 = lobman.createNewPlayer("player1", "1");
        String word = "word";

        Game currGame = lobman.newGameFromPool(new HashMap<>());
        lobman.setGame(currGame);

        assertFalse("Player 1 shouldn't be in the Game", currGame.getPlayers().contains(player1));

        SwOutputBoundary pres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord){
                assertEquals("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???", 1,2);
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                assertEquals("Response message is not correct",
                        "Player with ID " + "1" + " does not exist or is not in the Game.",
                        outputDataFailure.getResponse().getMessage());

                assertEquals("Response code is not correct", PLAYER_NOT_FOUND,
                        outputDataFailure.getResponse().getCode());

                assertEquals("Offending Player ID is not correct.", "1",
                        outputDataFailure.getPlayerId());

                System.out.println("The invalid presenter code block was called successfully! :)");
            }
        };

        SwInputData swinput = new SwInputData(word, player1.getPlayerId());

        SwInteractor swint = new SwInteractor(pres, lobman);
        swint.submitWord(swinput);

        System.out.println("Test ran to end successfully! :)");
    }

    /**
     * Tests submitting a word to a non-existent Game.
     */
    @Test(timeout = 10000)
    public void testGameDoesntExist() throws IdInUseException, InvalidDisplayNameException {

        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class LocalValidityChecker implements ValidityChecker{

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

        class GameFactoryTest implements GameFactory {
            /**
             * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
             */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new LocalValidityChecker());
            }
        }

        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac, playerPoolLock);
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
                assertEquals("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???", 1,2);
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                assertEquals("Response message is not correct",
                        "The Game you are trying to submit a word to doesn't exist",
                        outputDataFailure.getResponse().getMessage());

                assertEquals("Response code is not correct", GAME_DOESNT_EXIST,
                        outputDataFailure.getResponse().getCode());

                assertEquals("Offending Player ID is not correct.", "1",
                        outputDataFailure.getPlayerId());

                System.out.println("The invalid presenter code block was called successfully! :)");
            }
        };

        SwInputData swinput = new SwInputData(word, player1.getPlayerId());

        SwInteractor swint = new SwInteractor(pres, lobman);
        swint.submitWord(swinput);

        System.out.println("Test ran to end successfully! :)");
    }

    /**
     * Tests submitting a word that is invalid, with a DisplayNameChecker that always returns false.
     */
    @Test(timeout = 10000)
    public void testInvalidWord() throws IdInUseException, GameRunningException, InvalidDisplayNameException {
        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class LocalValidityChecker implements ValidityChecker{

            /**
             * Checks whether the word is valid
             * @param word the word we need to check
             * @return false, since we are locally defining that.
             */
            @Override
            public boolean isValid(String word) {
                return false;
            }
        }

        class GameFactoryTest implements GameFactory {
            /**
             * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
             */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new LocalValidityChecker());
            }
        }

        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac, playerPoolLock);
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

        assertTrue("Player 1 is not in the Game", currGame.getPlayers().contains(player1));

        assertEquals("It should be Player 1's turn, but it isn't.", player1, currGame.getCurrentTurnPlayer());

        SwOutputBoundary pres = new SwOutputBoundary() {
            @Override
            public void valid(SwOutputDataValidWord outputDataValidWord){
                assertEquals("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL VALID???", 1,2);
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure) {
                assertEquals("Response message is not correct",
                        "The word 'bloop' is not valid, please try another word.",
                        outputDataFailure.getResponse().getMessage());

                assertEquals("Response code is not correct", INVALID_WORD,
                        outputDataFailure.getResponse().getCode());

                assertEquals("Offending Player ID is not correct.", "1",
                        outputDataFailure.getPlayerId());

                System.out.println("The invalid presenter code block was called successfully! :)");
            }
        };

        SwInputData swinput = new SwInputData(word, player1.getPlayerId());
        SwInteractor swint = new SwInteractor(pres, lobman);
        swint.submitWord(swinput);

        System.out.println("Test ran to end successfully! :)");

    }


    /**
     * Tests submitting a word that is valid.
     */
    @Test(timeout = 10000)
    public void testValidWord() throws IdInUseException, GameRunningException, InvalidDisplayNameException {
        class LocalDisplayName implements DisplayNameChecker {
            @Override
            public boolean checkValid(String displayName) {
                return true;
            }
        }

        class LocalValidityChecker implements ValidityChecker{

            /**
             * Checks whether the word is valid
             * @param word the word we need to check
             * @return true, since we are locally defining that.
             */
            @Override
            public boolean isValid(String word) {
                return true;
            }
        }

        class GameFactoryTest implements GameFactory {
            /**
             * An anonymous GameFactoryTest which has a ValidityChecker that can be customizable.
             */
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                Queue<Player> queueOfInitialPlayers = new LinkedList<>(initialPlayers);
                return new GameTest(queueOfInitialPlayers, new LocalValidityChecker());
            }
        }

        PlayerFactory playerFac = new PlayerFactory(new LocalDisplayName());
        GameFactory gameFac = new GameFactoryTest();
        LobbyManager lobman = new LobbyManager(playerFac, gameFac, playerPoolLock);
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

        assertTrue("Player 1 is not in the Game", currGame.getPlayers().contains(player1));

        assertEquals("It should be Player 1's turn, but it isn't.", player1, currGame.getCurrentTurnPlayer());

        SwOutputBoundary pres = new SwOutputBoundary() {

            @Override
            public void valid(SwOutputDataValidWord swOutputDataValidWord) {
                assertEquals("Response message is not correct",
                        "Word 'bloop' has been added!",
                        swOutputDataValidWord.getResponse().getMessage());

                assertEquals("Response code is not correct", SUCCESS,
                        swOutputDataValidWord.getResponse().getCode());

                assertEquals("Player ID is not correct.", "1",
                        swOutputDataValidWord.getPlayerId());

                System.out.println("The valid presenter code block was called successfully! :)");
            }

            @Override
            public void invalid(SwOutputDataFailure outputDataFailure){
                assertEquals("THIS SHOULD NOT HAPPEN, WHY DOES IT CALL INVALID???", 1,2);
            }
        };

        SwInputData swinput = new SwInputData(word, player1.getPlayerId());
        SwInteractor swint = new SwInteractor(pres, lobman);
        swint.submitWord(swinput);

        System.out.println("Test ran to end successfully! :)");
    }
}
