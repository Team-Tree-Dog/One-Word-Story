package usecases.pull_game_ended;

import entities.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class PgeInteractorTests {

    // PgeInputBoundary, PgeInputData used to test PgeInteractor
    private PgeInputBoundary pgeib;
    private PgeInputData pgeid;

    @Before
    public void setUp() {
        // Add players for a list of players
        Player p1 = new Player("p1", "1");
        Player p2 = new Player("p2", "2");

        // Instantiate pgeInputBoundary
        pgeib = new PgeInteractor(new PgeOutputBoundary() {
            @Override
            public void notifyGameEnded(PgeOutputData data) {}
        });

        // Instantiate pgeInputData with list of players
        pgeid = new PgeInputData(new ArrayList<Player>(2));
        pgeid.players.add(0, p1);
        pgeid.players.add(1, p2);
    }

    @After
    public void tearDown() {}

    /**
     * Test that the data is built correctly (the proper player ids were extracted)
     */
    @Test(timeout = 1000)
    public void testBuildData() {
        String[] expectedPlayerIds = new String[2];
        expectedPlayerIds[0] = "1";
        expectedPlayerIds[1] = "2";
        String[] actualPlayerIds = ((PgeInteractor) pgeib).getPlayerIds(pgeid.players);
        assertEquals(expectedPlayerIds, actualPlayerIds);
    }
}
