package usecases.sort_players;

import entities.*;
import entities.games.Game;
import entities.games.GameFactory;
import exceptions.GameRunningException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import usecases.pull_data.PdInteractor;
import usecases.pull_data.PdOutputBoundary;
import usecases.pull_data.PdOutputData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// TODO: For players being added to the game, check that the Pool Listener methods are called

/**
 *
 */
public class SpInteractorTests {

    private static class CustomizableTestGame extends Game {

        private final ArrayList<Player> players = new ArrayList<>();
        private final boolean gameOver;

        public CustomizableTestGame(boolean gameOver) {
            super(99, new ValidityChecker() {
                @Override
                public boolean isValid(String word) {
                    return true;
                }
            });
            this.gameOver = gameOver;
        }

        @Override
        public Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public boolean isGameOver() {
            return gameOver;
        }

        @Override
        public void onTimerUpdate() {

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
            players.add(playerToAdd);
            return true;
        }

        @Override
        public boolean switchTurn() {
            return false;
        }

        @Override
        public Player getCurrentTurnPlayer() {
            return null;
        }
    }

    private static class BlankOutputPdInteractor extends PdInteractor {
        public BlankOutputPdInteractor () {
            super(new PdOutputBoundary() {
                @Override
                public void updateGameInfo(PdOutputData d) {}
            });
        }
    }

    private static class BlankOutputPgeInteractor extends PgeInteractor {
        public BlankOutputPgeInteractor () {
            super(new PgeOutputBoundary() {
                @Override
                public void notifyGameEnded (PgeOutputData d) {}
            });
        }
    }

    private static class NaivePlayerPoolListener implements PlayerPoolListener {
        @Override
        public void onJoinGamePlayer(Game game) {}

        @Override
        public void onCancelPlayer() {}
    }

    private static class NaiveDisplayNameChecker implements DisplayNameChecker {
        @Override
        public boolean checkValid(String displayName) {
            return true;
        }
    }

    /**
     *
     */
    @Before
    public void setup () {

    }

    /**
     *
     */
    @After
    public void teardown () {

    }

    /**
     * Test the scenario where two players are in the pool and game is null. Sort players
     * should in this case empty the players from the pool into a new game
     * and start the game timer.
     */
    @Test(timeout=1000)
    public void testTwoPlayersInPoolStartGame () {

        CustomizableTestGame customizableTestGame = new CustomizableTestGame(false);

        // Game is null by default
        LobbyManager m = new LobbyManager(new PlayerFactory(new NaiveDisplayNameChecker()), new GameFactory() {
            @Override
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                // Returns instance of game which always adds players successfully.
                // We return this particular instance so we can have access to this game's methods.
                return customizableTestGame;
            }
        });

        // Adds two players
        m.addPlayerToPool(new Player("Bob", "1"), new NaivePlayerPoolListener());
        m.addPlayerToPool(new Player("Billy", "2"), new NaivePlayerPoolListener());

        SpInteractor sp = new SpInteractor(m, new BlankOutputPgeInteractor(), new BlankOutputPdInteractor());

        SpInteractor.SpTask spTimerTask = sp.new SpTask();
        spTimerTask.run();

        assertFalse(m.isGameNull());
        assertEquals(0, m.getPool().size());
        assertFalse(m.isGameEnded());
        assertEquals(customizableTestGame.getPlayers().size(), 2);

        // Terminates timer
        customizableTestGame.getGameTimer().cancel();
    }

    /**
     * Test the scenario where the game is not null but is over. Sort players
     * should set the game to null
     */
    @Test(timeout=1000)
    public void testGameNotNullIsOverSetNull () {
        CustomizableTestGame g = new CustomizableTestGame(true);

        LobbyManager m = new LobbyManager(new PlayerFactory(new NaiveDisplayNameChecker()), new GameFactory() {
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

        SpInteractor sp = new SpInteractor(m, new BlankOutputPgeInteractor(), new BlankOutputPdInteractor());

        SpInteractor.SpTask spTimerTask = sp.new SpTask();
        spTimerTask.run();

        assertTrue(m.isGameNull());
    }

    /**
     * Test the scenario where the game is not null and not over and players
     * are in the pool. Sort players should try to add the players to the game
     * and remove them from the pool. The players will be successfully added due
     * to the game implementation used for this test
     */
    @Test(timeout=1000)
    public void testGameRunningPlayersInPool () {
        CustomizableTestGame g = new CustomizableTestGame(false);

        LobbyManager m = new LobbyManager(new PlayerFactory(new NaiveDisplayNameChecker()), new GameFactory() {
            @Override
            public Game createGame(Map<String, Integer> settings, Collection<Player> initialPlayers) {
                return null;
            }
        });

        g.addPlayer(new Player("Lilly", "3"));
        g.addPlayer(new Player("Anna", "4"));

        try {
            m.setGame(g);
        } catch (GameRunningException ignored) {}

        m.addPlayerToPool(new Player("Bob", "1"), new NaivePlayerPoolListener());
        m.addPlayerToPool(new Player("Billy", "2"), new NaivePlayerPoolListener());

        SpInteractor sp = new SpInteractor(m, new BlankOutputPgeInteractor(), new BlankOutputPdInteractor());
        SpInteractor.SpTask spTimerTask = sp.new SpTask();
        spTimerTask.run();

        assertEquals(m.getPool().size(), 0);
        assertEquals(g.getPlayers().size(), 4);
    }
}
