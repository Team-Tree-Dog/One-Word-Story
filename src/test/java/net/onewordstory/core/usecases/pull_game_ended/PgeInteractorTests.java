package net.onewordstory.core.usecases.pull_game_ended;

import net.onewordstory.core.entities.Player;
import net.onewordstory.core.entities.statistics.AllPlayerNamesStatistic;
import net.onewordstory.core.usecases.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            builtData.add(data.getPlayerStatDTOs()[0].getPlayerId());
            builtData.add(data.getPlayerStatDTOs()[1].getPlayerId());
        }
    }

    @BeforeEach
    public void setUp() {
        // Add players for a list of players
        Player p1 = new Player("p1", "1");
        Player p2 = new Player("p2", "2");

        // Instantiate pgeInputBoundary
        pgeib = new PgeInteractor(testOutputBoundary, new PgeGatewayStory() {
            @Override
            public @NotNull Response saveStory(String storyString, double publishUnixTimeStamp, @Nullable Set<String> authorDisplayNames) {
                return new Response(Response.ResCode.SUCCESS, "Response has been returned successfully");
            }
        });

        // Instantiate pgeInputData with list of players
        pgeid = new PgeInputData(new ArrayList<>(2), "",
                new ArrayList<>(1), new AllPlayerNamesStatistic());
        pgeid.getPlayers().add(0, p1);
        pgeid.getPlayers().add(1, p2);
    }

    @AfterEach
    public void tearDown() {}

    /**
     * Test that the data is built correctly (the proper player ids were extracted)
     */
    @Test
    @Timeout(1000)
    public void testBuiltData() {
        ArrayList<String> expectedPlayerIds = new ArrayList<>();
        expectedPlayerIds.add("1");
        expectedPlayerIds.add("2");
        pgeib.onGameEnded(pgeid);

        assertEquals(expectedPlayerIds, testOutputBoundary.builtData);
    }
}
