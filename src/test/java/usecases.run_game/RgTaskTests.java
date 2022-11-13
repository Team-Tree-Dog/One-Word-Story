package usecases.run_game;

import entities.Player;
import entities.games.Game;
import entities.games.GameRegular;
import exceptions.InvalidWordException;
import org.junit.After;
import org.junit.Before;
import org.junit.*;
import usecases.GameDTO;
import usecases.PlayerDTO;
import usecases.pull_data.*;
import usecases.pull_game_ended.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RgTaskTests {

    /**
     * Game, PgeInputBoundary, PdInputBoundary used to test RgTask in RunGame use-case
     */
    private Game g;
    private PgeInputBoundary pge;
    private PdInputBoundary pd;

    @Before
    public void setUp() {

        // Instantiate Game g
        g = new GameRegular(new LinkedList<>());

        // Instantiate empty PgeInputBoundary (i.e. PgeInteractor) pge
        PgeOutputBoundary pgeob = new PgeOutputBoundary() {
            @Override
            public void notifyGameEnded (PgeOutputData d) {
            }
        };
        pge = new PgeInteractor(pgeob);

        // Instantiate empty PdInputBoundary (i.e. PdInteractor) pd
        PdOutputBoundary pob = new PdOutputBoundary() {
            @Override
            public void updateGameInfo (PdOutputData d) {}
        };
        pd = new PdInteractor(pob);
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

        // Instantiate Game g
        g = new GameRegular(new LinkedList<>());
        g.addPlayer(new Player("p1", "1"));

        // Instantiate PgeInputBoundary (i.e. PgeInteractor) pge with necessary checks
        PgeOutputBoundary pgeob = new PgeOutputBoundary() {
            @Override
            public void notifyGameEnded (PgeOutputData d) {
                assertTrue("Could not access PGE boundary", d.getPlayerIds.length == 1);
                assertTrue("Could not access PGE boundary", d.getPlayerIds[0] == "1");
            }
        };
        pge = new PgeInteractor(pgeob);

        // There is only one player, so isGameOver should be triggered
        assertTrue("GameOver scenario is not accessed", g.isGameOver());

        RgInteractor rg = new RgInteractor(g, pge, pd);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        innerTaskInstance.run();
        assertTrue("TimerStopped is not updated in GameOver scenario", g.isTimerStopped());

    }

    /**
     * Test regular decrement scenario.
     * We expect secondsLeftCurrentTurn to decrease appropriately,
     * without change of turn
     */
    @Test(timeout = 1000)
    public void testTimerDecrementScenario() {

        // Instantiate Game g
        g = new GameRegular(new LinkedList<>());
        g.addPlayer(new Player("p1", "1"));
        g.addPlayer(new Player("p2", "2"));

        // Instantiate PdInputBoundary (i.e. PdInteractor) pd with necessary checks
        PdOutputBoundary pob = new PdOutputBoundary() {
            @Override
            public void updateGameInfo (PdOutputData d) {
                GameDTO obj1 = d.getGameInfo();

                List<String> IdList = new ArrayList<>();
                for (PlayerDTO p : obj1.getPlayers()) {
                    IdList.add(p.getPlayerId());
                }
                for (Player p : g.getPlayers()) {
                    assertTrue("Mistake in PD boundary", IdList.contains(p.getPlayerId()));
                }

                assertEquals("Mistake in PD boundary", obj1.getCurrentTurnPlayerId(),
                        g.getCurrentTurnPlayer().getPlayerId());

                assertEquals("Mistake in PD boundary", obj1.getSecondsLeftCurrentTurn(),
                        g.getSecondsLeftInCurrentTurn());
            }
        };
        pd = new PdInteractor(pob);


        // There are two players, so isGameOver should not be triggered
        assertFalse("GameOver scenario incorrectly triggerred for Timer Decrement Scenario", g.isGameOver());

        RgInteractor rg = new RgInteractor(g, pge, pd);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        String curPlayerId = g.getCurrentTurnPlayer().getPlayerId();
        g.setSecondsLeftInCurrentTurn(10);

        innerTaskInstance.run();
        innerTaskInstance.run();
        innerTaskInstance.run();

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

        // Instantiate Game g
        g = new GameRegular(new LinkedList<>());
        g.addPlayer(new Player("p1", "1"));
        g.addPlayer(new Player("p2", "2"));
        g.addPlayer(new Player("p3", "3"));

        // Instantiate PdInputBoundary (i.e. PdInteractor) pd with necessary checks
        PdOutputBoundary pob = new PdOutputBoundary() {
            @Override
            public void updateGameInfo (PdOutputData d) {
                GameDTO obj1 = d.getGameInfo();

                List<String> IdList = new ArrayList<>();
                for (PlayerDTO p : obj1.getPlayers()) {
                    IdList.add(p.getPlayerId());
                }
                for (Player p : g.getPlayers()) {
                    assertTrue("Mistake in PD boundary", IdList.contains(p.getPlayerId()));
                }

                assertEquals("Mistake in PD boundary", obj1.getCurrentTurnPlayerId(),
                        g.getCurrentTurnPlayer().getPlayerId());

                assertEquals("Mistake in PD boundary", obj1.getSecondsLeftCurrentTurn(),
                        g.getSecondsLeftInCurrentTurn());
            }
        };
        pd = new PdInteractor(pob);


        // There are two players, so isGameOver should not be triggered
        assertFalse("GameOver scenario incorrectly triggerred for SwitchTurn Scenario", g.isGameOver());
        assertEquals("Expected different first player", g.getCurrentTurnPlayer().getPlayerId(), "1");

        RgInteractor rg = new RgInteractor(g, pge, pd);
        RgInteractor.RgTask innerTaskInstance = rg.new RgTask();

        String curPlayerId = g.getCurrentTurnPlayer().getPlayerId();
        g.setSecondsLeftInCurrentTurn(3);

        innerTaskInstance.run();
        innerTaskInstance.run();
        innerTaskInstance.run();

        // Verify correct switch of turn
        assertEquals("Invalid switch of turn", g.getCurrentTurnPlayer().getPlayerId(), "2");

        // Verify secondsLeftInCurrentTurn is correctly updated
        assertEquals("Invalid secondsLeftInCurrentTurn after switch of turn", g.getSecondsLeftInCurrentTurn(),
                g.getSecondsPerTurn());

    }

}
