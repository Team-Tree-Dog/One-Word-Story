package usecases.run_game;

import entities.Player;
import entities.games.Game;
import entities.validity_checkers.ValidityCheckerFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.pull_data.*;
import usecases.pull_game_ended.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;


public class RgTaskTests {

    private Lock gameLock;

    /**
     * We use custom implementations of Game, PgeInputBoundary, PdInputBoundary used to test RgTask in RunGame use-case
     */

    private static class CustomizableTestGame extends Game {

        public static int REGULAR_GAME_SECONDS_PER_TURN = 15;
        private final Queue<Player> players;
        private final boolean gameOverValue;

        public static final ValidityCheckerFacade v = new ValidityCheckerFacade(
                puncValidityChecker -> "",
                wordValidityChecker -> ""
        );

        /**
         * Constructor of CustomizableTestGame for our tests
         * @param gameOverValue fixed isGameOver return value
         */
        public CustomizableTestGame(boolean gameOverValue) {
            super(REGULAR_GAME_SECONDS_PER_TURN, v);
            players = new LinkedList<>();
            this.gameOverValue = gameOverValue;
        }

        @Override
        public Collection<Player> getPlayers() {
            return players;
        }

        /**
         * Copied from GameRegular
         * Currently implemented as no-operation
         */
        @Override
        public void onTimerUpdate() {}

        /**
         * Unused custom getPlayerById
         * @param playerId Id of searched player
         * @return null
         */
        @Override
        public Player getPlayerById(String playerId) { return null; }

        /**
         * Unused custom removePlayer
         * @param playerToRemove The Player to be removed
         * @return true
         */
        @Override
        public boolean removePlayer(Player playerToRemove) { return true; }

        /**
         * Copied from GameRegular
         * Adds the player specified to this game instance
         * @param playerToAdd The Player to be added
         * @return if the player was successfully added
         */
        @Override
        public boolean addPlayer(Player playerToAdd) { return players.add(playerToAdd); }

        /**
         * Copied from GameRegular
         * Moves the player whose turn it currently is from the front of the list of players to the back
         * It is now the new player in the front's turn
         * @return if the turn switch was successful
         */
        @Override
        public boolean switchTurn() {
            setSecondsLeftInCurrentTurn(getSecondsPerTurn());
            return players.add(players.remove());
        }

        /**
         * Copied from GameRegular
         * @return the first player in the player list, whose turn is currently taking place
         */
        @Override
        public Player getCurrentTurnPlayer() { return players.peek(); }

        /**
         * Custom isGameOver, returns fixed value
         * @return specified value
         */
        @Override
        public boolean isGameOver() { return this.gameOverValue; }

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

    @BeforeEach
    public void setUp() {
        gameLock = new ReentrantLock();
    }

    @AfterEach
    public void tearDown() {

    }

    /**
     * Tests GameOver Scenario, when game timer is cancelled
     * and Pull-Game-Ended use-case is called
     */
    @Test
    @Timeout(1000)
    public void testGameOverScenario() {

        g = new CustomizableTestGame(true);
        g.addPlayer(new Player("p1", "1"));

        pge = new CustomizablePgeInputBoundary();
        pd = new CustomizablePdInputBoundary();

        RgInteractor rg = new RgInteractor(g, pge, pd, gameLock);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        innerTaskInstance.run();

        // Verify that PGE is called in onGameEnded
        assertNotNull(((CustomizablePgeInputBoundary) pge).getPassedData(), "PGE use-case is not accessed");

        // Verify that onGameEnded receives correct input
        ArrayList<Player> pgeList = new ArrayList<>(((CustomizablePgeInputBoundary) pge).getPassedData().getPlayers());
        ArrayList<Player> ourList = new ArrayList<>(g.getPlayers());
        assertEquals(pgeList.size(), ourList.size(), "PGE received input of incorrect length");
        for (int i = 0; i < pgeList.size(); i++) {
            assertEquals(pgeList.get(i), ourList.get(i), "PGE received incorrect input");
        }
    }

    /**
     * Test regular decrement scenario.
     * We expect secondsLeftCurrentTurn to decrease appropriately,
     * without change of turn
     */
    @Test
    @Timeout(1000)
    public void testTimerDecrementScenario() {

        g = new CustomizableTestGame(false);
        g.addPlayer(new Player("p1", "1"));
        g.addPlayer(new Player("p2", "2"));
        g.addPlayer(new Player("p3", "3"));

        pge = new CustomizablePgeInputBoundary();
        pd = new CustomizablePdInputBoundary();

        RgInteractor rg = new RgInteractor(g, pge, pd, gameLock);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        String curPlayerId = g.getCurrentTurnPlayer().getPlayerId();
        g.setSecondsLeftInCurrentTurn(10);

        innerTaskInstance.run();
        innerTaskInstance.run();
        innerTaskInstance.run();

        // Verify pd use-case is accessed
        assertNotNull(((CustomizablePdInputBoundary) pd).getPassedData(),
                "PD use-case is not accessed");

        // Verify that onTimerUpdate receives correct input
        assertEquals(((CustomizablePdInputBoundary) pd).getPassedData().getGame(), g,
                "PD received incorrect input");

        // Verify decrement
        assertEquals(g.getSecondsLeftInCurrentTurn(), 7,
                "Invalid decrement of secondsLeftInCurrentTurn");

        // Verify no switch of turn
        assertEquals(g.getCurrentTurnPlayer().getPlayerId(), curPlayerId,
                "Unplanned switch of turn");

    }


    /**
     * Test switch of turn scenario.
     * We expect secondsLeftCurrentTurn to be reinstated appropriately,
     * with a valid switch of turn performed
     */
    @Test
    @Timeout(1000)
    public void testTimerSwitchTurnScenario() {

        g = new CustomizableTestGame(false);
        g.addPlayer(new Player("p1", "1"));
        g.addPlayer(new Player("p2", "2"));
        g.addPlayer(new Player("p3", "3"));

        pge = new CustomizablePgeInputBoundary();
        pd = new CustomizablePdInputBoundary();

        RgInteractor rg = new RgInteractor(g, pge, pd, gameLock);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        g.setSecondsLeftInCurrentTurn(3);

        innerTaskInstance.run();
        innerTaskInstance.run();
        innerTaskInstance.run();

        // Verify pd use-case is accessed
        assertNotNull(((CustomizablePdInputBoundary) pd).getPassedData(), "PD use-case is not accessed");

        // Verify that onTimerUpdate receives correct input
        assertEquals(((CustomizablePdInputBoundary) pd).getPassedData().getGame(), g,
                "PD received incorrect input");

        // Verify correct switch of turn
        assertEquals(g.getCurrentTurnPlayer().getPlayerId(), "2",
                "Invalid switch of turn");

        // Verify secondsLeftInCurrentTurn is correctly updated
        assertEquals(g.getSecondsLeftInCurrentTurn(), g.getSecondsPerTurn(),
                "Invalid secondsLeftInCurrentTurn after switch of turn");

    }

}
