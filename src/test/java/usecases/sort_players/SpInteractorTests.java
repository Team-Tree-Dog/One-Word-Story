package usecases.sort_players;

import entities.*;
import entities.display_name_checkers.DisplayNameChecker;
import entities.games.Game;
import entities.games.GameFactory;
import entities.validity_checkers.ValidityCheckerFacade;
import exceptions.GameRunningException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.Response;
import usecases.pull_data.PdInteractor;
import usecases.pull_game_ended.PgeGatewayStory;
import usecases.pull_game_ended.PgeInteractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test the sort players timer task for proper functionality
 */
public class SpInteractorTests {

    /**
     * Test Validity Checker Facade which always validates and does not modify input
     */
    static class TestValidityCheckerTrue extends ValidityCheckerFacade {

        public TestValidityCheckerTrue() {
            super((p) -> p, (w) -> w);
        }

        @Override
        public String[] isValid(String word) {
            return new String[]{word};
        }
    }

    private static class CustomizableTestGame extends Game {

        private final ArrayList<Player> players = new ArrayList<>();
        private final boolean gameOver;
        private final boolean allowAddingPlayers;

        /**
         * @param gameOver The boolean value isGameOver returns, for testing purposes
         * @param allowAddingPlayers Does addPlayer successfully add the player and return true
         */
        public CustomizableTestGame(boolean gameOver, boolean allowAddingPlayers) {
            super(99, new TestValidityCheckerTrue());
            this.gameOver = gameOver;
            this.allowAddingPlayers = allowAddingPlayers;
        }

        @Override
        public @NotNull Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public boolean isGameOver() {
            return gameOver;
        }

        @Override
        public void onTimerUpdateLogic() {

        }

        @Override
        public Player getPlayerById(String PlayerId) {
            return null;
        }

        @Override
        public boolean removePlayer(Player playerToRemove) {
            return false;
        }

        @Override
        public boolean addPlayer(Player playerToAdd) {
            if (allowAddingPlayers) {
                players.add(playerToAdd);
                return true;
            } return false;
        }

        @Override
        public boolean switchTurnLogic() {
            return false;
        }

        @Override
        public @NotNull Player getCurrentTurnPlayer() {
            return null;
        }

    }

    private static class BlankOutputPdInteractor extends PdInteractor {
        public BlankOutputPdInteractor () {
            super(d -> {});
        }
    }

    private static class BlankOutputPgeInteractor extends PgeInteractor {
        public BlankOutputPgeInteractor () {
            super(d -> {
            }, (storyString, publishUnixTimeStamp, authorDisplayNames) -> null);
        }
    }

    private static class TestPlayerPoolListener implements PlayerPoolListener {

        public boolean joinedGameFlag = false;
        private final Lock lock = new ReentrantLock();

        @Override
        public void onJoinGamePlayer(Game game) { joinedGameFlag = true; }

        @Override
        public void onCancelPlayer() {}

        @Override
        public Lock getLock() {
            return lock;
        }

    }

    private static class NaiveDisplayNameChecker implements DisplayNameChecker {
        @Override
        public boolean checkValid(String displayName) {
            return true;
        }
    }

    /**
     * Pre-test setup, none in this case
     */
    @BeforeEach
    public void setup () {}

    /**
     * Post-test breakdown, none in this case
     */
    @AfterEach
    public void teardown () {}

    /**
     * Test the scenario where two players are in the pool and game is null. Sort players
     * should in this case empty the players from the pool into a new game
     * and start the game timer.
     */
    @Test
    @Timeout(1)
    public void testTwoPlayersInPoolStartGame () {

        CustomizableTestGame customizableTestGame = new CustomizableTestGame(false, true);

        // Game is null by default
        LobbyManager m = new LobbyManager(new PlayerFactory(new NaiveDisplayNameChecker()),
                new GameFactory() {
                    @Override
                    public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                        // Returns instance of game which always adds players successfully.
                        // We return this particular instance so we can have access to this game's methods.
                        for (Player p : initialPlayers) {
                            customizableTestGame.addPlayer(p);
                        }
                        return customizableTestGame;
                    }
                });

        TestPlayerPoolListener bobsListener = new TestPlayerPoolListener();
        TestPlayerPoolListener billysListener = new TestPlayerPoolListener();

        // Adds two players
        m.addPlayerToPool(new Player("Bob", "1"), bobsListener);
        m.addPlayerToPool(new Player("Billy", "2"), billysListener);

        // Confirm that players have no received a callback yet
        assertFalse(bobsListener.joinedGameFlag);
        assertFalse(billysListener.joinedGameFlag);

        // Execute one round of the TimerTask. This should get a new game with players from
        // the pool, set it as m.game, clear the pool, and call the PlayerPoolListeners
        SpInteractor sp = new SpInteractor(m, new BlankOutputPgeInteractor(),
                new BlankOutputPdInteractor());
        SpInteractor.SpTask spTimerTask = sp.new SpTask();
        spTimerTask.run();

        // Confirm that PlayerPoolListeners were called
        assertTrue(bobsListener.joinedGameFlag);
        assertTrue(billysListener.joinedGameFlag);

        // Confirm game is no longer null (has been set)
        assertFalse(m.isGameNull());
        // Confirm pool is empty
        assertEquals(0, m.getPool().size());
        // Confirm that the game has two players (Bob and Billy)
        assertEquals(customizableTestGame.getPlayers().size(), 2);

        // Terminate timer
        customizableTestGame.getGameTimer().cancel();
    }

    /**
     * Test the scenario where the game is not null but is over. Sort players
     * should set the game to null
     */
    @Test
    @Timeout(1)
    public void testGameNotNullIsOverSetNull () {
        CustomizableTestGame g = new CustomizableTestGame(true, true);

        LobbyManager m = new LobbyManager(new PlayerFactory(new NaiveDisplayNameChecker()),
                new GameFactory() {
                    @Override
                    public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                        return null;
                    }
                });

        // Set a game and set isGameEnded to true via setTimerStopped
        try {
            m.setGame(g);
        } catch (GameRunningException ignored) {}
        g.setTimerStopped();

        // Execute one round of the TimerTask. This should set the game to null
        SpInteractor sp = new SpInteractor(m, new BlankOutputPgeInteractor(),
                new BlankOutputPdInteractor());
        SpInteractor.SpTask spTimerTask = sp.new SpTask();
        spTimerTask.run();

        // Confirm game has been set to null
        assertTrue(m.isGameNull());
    }

    /**
     * Test the scenario where the game is not null and not over and players
     * are in the pool. Sort players should try to add the players to the game
     * and remove them from the pool. The players will be successfully added due
     * to the game implementation used for this test
     */
    @Test
    @Timeout(1)
    public void testGameRunningPlayersInPoolAdded () {
        CustomizableTestGame g = new CustomizableTestGame(false, true);

        LobbyManager m = new LobbyManager(new PlayerFactory(new NaiveDisplayNameChecker()),
                new GameFactory() {
                    @Override
                    public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                        return null;
                    }
                });

        // Add two players to game
        g.addPlayer(new Player("Lilly", "3"));
        g.addPlayer(new Player("Anna", "4"));

        // Set up scenario where a game is running (not null not ended)
        try {
            m.setGame(g);
        } catch (GameRunningException ignored) {}

        TestPlayerPoolListener bobsListener = new TestPlayerPoolListener();
        TestPlayerPoolListener billysListener = new TestPlayerPoolListener();

        // Add two players to pool
        m.addPlayerToPool(new Player("Bob", "1"), bobsListener);
        m.addPlayerToPool(new Player("Billy", "2"), billysListener);

        // Execute one round of the TimerTask. Based on the Game instance used, Bob and Billy
        // should both be successfully added to the currently running game
        SpInteractor sp = new SpInteractor(m, new BlankOutputPgeInteractor(),
                new BlankOutputPdInteractor());
        SpInteractor.SpTask spTimerTask = sp.new SpTask();
        spTimerTask.run();

        // Confirm that pool has been emptied and the game has 4 players (Bob, Billy, Lilly, Anna)
        assertEquals(m.getPool().size(), 0);
        assertEquals(g.getPlayers().size(), 4);
        // Confirm that both pool listeners have been notified
        assertTrue(bobsListener.joinedGameFlag);
        assertTrue(billysListener.joinedGameFlag);
    }

    /**
     * Test the scenario where the game is not null and not over and players
     * are in the pool. Sort players should try to add the players to the game
     * and remove them from the pool. However, the game will refuse to add them.
     * In this case, the pool listeners should not be called and the players
     * should remain in the pool
     */
    @Test
    @Timeout(1)
    public void testGameRunningPlayersInPoolRefused () {
        CustomizableTestGame g = new CustomizableTestGame(false, false);

        LobbyManager m = new LobbyManager(new PlayerFactory(new NaiveDisplayNameChecker()),
                new GameFactory() {
                    @Override
                    public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                        return null;
                    }
                });

        // Set up scenario where a game is running (not null not ended)
        try {
            m.setGame(g);
        } catch (GameRunningException ignored) {}

        TestPlayerPoolListener bobsListener = new TestPlayerPoolListener();
        TestPlayerPoolListener billysListener = new TestPlayerPoolListener();

        // Add two players to pool
        m.addPlayerToPool(new Player("Bob", "1"), bobsListener);
        m.addPlayerToPool(new Player("Billy", "2"), billysListener);

        // Confirm that the listeners have not been called before execution
        assertFalse(bobsListener.joinedGameFlag);
        assertFalse(billysListener.joinedGameFlag);
        assertEquals(0, g.getPlayers().size());
        assertEquals(2, m.getPool().size());

        // Execute one round of the TimerTask. Based on the Game instance used, Bob and Billy
        // should both be successfully added to the currently running game
        SpInteractor sp = new SpInteractor(m, new BlankOutputPgeInteractor(),
                new BlankOutputPdInteractor());
        SpInteractor.SpTask spTimerTask = sp.new SpTask();
        spTimerTask.run();

        // Confirm that the listeners have not been called after execution
        assertFalse(bobsListener.joinedGameFlag);
        assertFalse(billysListener.joinedGameFlag);
        // Confirm that both the game and the pool still have their two players each (no changes)
        assertEquals(0, g.getPlayers().size());
        assertEquals(2, m.getPool().size());
    }
}
