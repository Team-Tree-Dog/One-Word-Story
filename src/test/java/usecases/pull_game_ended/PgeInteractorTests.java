package usecases.pull_game_ended;

import entities.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class PgeInteractorTests {

    // PgeInputBoundary, PgeInputData, TestOutputBoundary used to test PgeInteractor
    private PgeInputBoundary pgeib;
    private PgeInputData pgeid;
    private final TestOutputBoundary testOutputBoundary = new TestOutputBoundary();

    // Implementation of output boundary for testing built data
    private static class TestOutputBoundary implements PgeOutputBoundary {

        ArrayList<String> builtData = new ArrayList<>();

        @Override
        public void notifyGameEnded(PgeOutputData data) {
            builtData.add(data.getPlayerIds()[0]);
            builtData.add(data.getPlayerIds()[1]);
        }
    }

    @Before
    public void setUp() {
        // Add players for a list of players
        Player p1 = new Player("p1", "1");
        Player p2 = new Player("p2", "2");

        // Instantiate pgeInputBoundary
        pgeib = new PgeInteractor(testOutputBoundary);

        // Instantiate pgeInputData with list of players
        pgeid = new PgeInputData(new ArrayList<>(2));
        pgeid.getPlayers().add(0, p1);
        pgeid.getPlayers().add(1, p2);
    }

    @After
    public void tearDown() {}

    /**
     * Test that the data is built correctly (the proper player ids were extracted)
     */
    @Test(timeout = 1000)
    public void testBuiltData() {
        ArrayList<String> expectedPlayerIds = new ArrayList<>();
        expectedPlayerIds.add("1");
        expectedPlayerIds.add("2");
        pgeib.onGameEnded(pgeid);
        assertEquals(expectedPlayerIds, testOutputBoundary.builtData);
    }
}
