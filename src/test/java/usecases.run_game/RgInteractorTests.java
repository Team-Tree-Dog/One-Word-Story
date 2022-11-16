package usecases.run_game;

import entities.Player;
import entities.games.Game;
import entities.games.GameRegular;
import exceptions.InvalidWordException;
import org.junit.After;
import org.junit.Before;
import org.junit.*;
import usecases.pull_data.*;
import usecases.pull_game_ended.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RgInteractorTests {

    /**
     * Game, PgeInputBoundary, PdInputBoundary used to test RgInteractor in RunGame use-case
     */
    private Game g;
    private PgeInputBoundary pge;
    private PdInputBoundary pd;

    @Before
    public void setUp() {
        // Instantiate Game g
        g = new GameRegular(new LinkedList<>());
        Player p1 = new Player("p1", "1");
        Player p2 = new Player("p2", "2");
        g.addPlayer(p1);
        g.addPlayer(p2);
        try {
            g.getStory().addWord("lol", p1);
            g.getStory().addWord("kek", p2);
            g.getStory().addWord("haha", p2);
        } catch (InvalidWordException ignored) {}

        // Instantiate empty PgeInputBoundary (i.e. PgeInteractor) pge
        PgeOutputBoundary pgeob = new PgeOutputBoundary() {
            @Override
            public void notifyGameEnded (PgeOutputData d) {}
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
     * Test that the timer starts correctly
     */
    @Test(timeout = 1000)
    public void testStartTimer() {

        RgInteractor rg = new RgInteractor(g, pge, pd);
        rg.startTimer();

        assertEquals("Timer did not start", g.getSecondsLeftInCurrentTurn(), g.getSecondsPerTurn());

    }

}
