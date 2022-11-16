package usecases.run_game;

import entities.Player;
import entities.games.Game;
import org.junit.After;
import org.junit.Before;
import org.junit.*;
import usecases.pull_data.*;
import usecases.pull_game_ended.*;

import static org.junit.Assert.*;

import java.util.*;


public class RgTaskTests {

    /**
     * We use custom implementations of Game, PgeInputBoundary, PdInputBoundary used to test RgTask in RunGame use-case
     */

    private static class CustomizableTestGame extends Game {

        public static int REGULAR_GAME_SECONDS_PER_TURN = 15;
        private final Queue<Player> players;

        private final boolean gameOverValue;

        /**
         * Constructor of CustomizableTestGame for our tests
         * @param gameOverValue fixed isGameOver return value
         */
        public CustomizableTestGame(boolean gameOverValue) {
            super(REGULAR_GAME_SECONDS_PER_TURN, word -> {
                // Currently accepting all the words
                return true;
            });
            players = new LinkedList<>();
            this.gameOverValue = gameOverValue;
        }

        @Override
        public Collection<Player> getPlayers() {
            return players;
        }

        @Override
        public void onTimerUpdate() {

        }

        @Override
        public Player getPlayerById(String playerId) {
            return null;
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
            return players.peek();
        }

        @Override
        public boolean isGameOver() {
            return this.gameOverValue;
        }
    }

    private static class CustomizablePgeInputBoundary implements PgeInputBoundary {

        /**
         * Value passed to onGameEnded,
         * or null if it was never accessed.
         */
        private PgeInputData passedData;

        /**
         * Constructor of CustomizablePgeInputBoundary for our tests
         */
        public CustomizablePgeInputBoundary() {
            this.passedData = null;
        }

        public PgeInputData getPassedData() {
            return this.passedData;
        }

        /**
         * Simulates onGameEnded call. Records received data
         * @param d PgeInputData received as an argument
         */
        @Override
        public void onGameEnded(PgeInputData d) {
            this.passedData = d;
        }
    }

    private static class CustomizablePdInputBoundary implements PdInputBoundary {

        /**
         * Value passed to onTimerUpdate,
         * or null if it was never accessed.
         */
        private PdInputData passedData;

        /**
         * Constructor of CustomizablePdInputBoundary for our tests
         */
        public CustomizablePdInputBoundary() {
            this.passedData = null;
        }

        public PdInputData getPassedData() {
            return this.passedData;
        }

        /**
         * Simulates onTimerUpdate call. Records received data
         * @param d PdInputData received as an argument
         */
        @Override
        public void onTimerUpdate(PdInputData d) {
            this.passedData = d;
        }
    }

    private Game g;
    private PgeInputBoundary pge;
    private PdInputBoundary pd;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    /**
     * Tests GameOver Scenario, when game timer is cancelled
     * and Pull-Game-Ended use-case is called
     */
    @Test(timeout = 1000)
    public void testGameOverScenario() {

        g = new CustomizableTestGame(true);
        g.addPlayer(new Player("p1", "1"));

        pge = new CustomizablePgeInputBoundary();
        pd = new CustomizablePdInputBoundary();

        RgInteractor rg = new RgInteractor(g, pge, pd);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        innerTaskInstance.run();

        // Verify that PGE is called in onGameEnded
        assertNotNull("PGE use-case is not accessed", ((CustomizablePgeInputBoundary) pge).getPassedData());

        // Verify that onGameEnded receives correct input
        ArrayList<Player> pgeList = new ArrayList<>(((CustomizablePgeInputBoundary) pge).getPassedData().getPlayers());
        ArrayList<Player> ourList = new ArrayList<>(g.getPlayers());
        assertEquals("PGE received input of incorrect length", pgeList.size(), ourList.size());
        for (int i = 0; i < pgeList.size(); i++) {
            assertEquals("PGE received incorrect input", pgeList.get(i), ourList.get(i));
        }
    }

    /**
     * Test regular decrement scenario.
     * We expect secondsLeftCurrentTurn to decrease appropriately,
     * without change of turn
     */
    @Test(timeout = 1000)
    public void testTimerDecrementScenario() {

        g = new CustomizableTestGame(false);
        g.addPlayer(new Player("p1", "1"));
        g.addPlayer(new Player("p2", "2"));
        g.addPlayer(new Player("p3", "3"));

        pge = new CustomizablePgeInputBoundary();
        pd = new CustomizablePdInputBoundary();

        RgInteractor rg = new RgInteractor(g, pge, pd);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        String curPlayerId = g.getCurrentTurnPlayer().getPlayerId();
        g.setSecondsLeftInCurrentTurn(10);

        innerTaskInstance.run();
        innerTaskInstance.run();
        innerTaskInstance.run();

        // Verify pd use-case is accessed
        assertNotNull("PD use-case is not accessed", ((CustomizablePdInputBoundary) pd).getPassedData());

        // Verify that onTimerUpdate receives correct input
        assertEquals("PD received incorrect input",
                     ((CustomizablePdInputBoundary) pd).getPassedData().getGame(), g);

        // Verify decrement
        assertEquals("Invalid decrement of secondsLeftInCurrentTurn", g.getSecondsLeftInCurrentTurn(), 7);

        // Verify no switch of turn
        assertEquals("Unplanned switch of turn", g.getCurrentTurnPlayer().getPlayerId(), curPlayerId);

    }


    /**
     * Test switch of turn scenario.
     * We expect secondsLeftCurrentTurn to be reinstated appropriately,
     * with a valid switch of turn performed
     */
    @Test(timeout = 1000)
    public void testTimerSwitchTurnScenario() {

        g = new CustomizableTestGame(false);
        g.addPlayer(new Player("p1", "1"));
        g.addPlayer(new Player("p2", "2"));
        g.addPlayer(new Player("p3", "3"));

        pge = new CustomizablePgeInputBoundary();
        pd = new CustomizablePdInputBoundary();

        RgInteractor rg = new RgInteractor(g, pge, pd);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        g.setSecondsLeftInCurrentTurn(3);

        innerTaskInstance.run();
        innerTaskInstance.run();
        innerTaskInstance.run();

        // Verify pd use-case is accessed
        assertNotNull("PD use-case is not accessed", ((CustomizablePdInputBoundary) pd).getPassedData());

        // Verify that onTimerUpdate receives correct input
        assertEquals("PD received incorrect input",
                ((CustomizablePdInputBoundary) pd).getPassedData().getGame(), g);

        // Verify correct switch of turn
        assertEquals("Invalid switch of turn", g.getCurrentTurnPlayer().getPlayerId(), "2");

        // Verify secondsLeftInCurrentTurn is correctly updated
        assertEquals("Invalid secondsLeftInCurrentTurn after switch of turn", g.getSecondsLeftInCurrentTurn(),
                g.getSecondsPerTurn());

    }

}
