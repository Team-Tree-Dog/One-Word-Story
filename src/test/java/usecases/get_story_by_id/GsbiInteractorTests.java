package usecases.get_story_by_id;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import usecases.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GsbiInteractorTests {

    private GsbiOutputBoundary output;
    private GsbiInteractor.GsbiThread innerThread;

    public void setup(RepoRes<StoryRepoData> storyRepoRes, RepoRes<String> titleRepoRes,
                      GsbiOutputBoundary pres) {
        ThreadRegister register = new ThreadRegister();

        GsbiGatewayStories storyRepo = storyId -> storyRepoRes;
        GsbiGatewayTitles titlesRepo = storyId -> titleRepoRes;

        GsbiInteractor interactor = new GsbiInteractor(storyRepo, titlesRepo, register);
        innerThread = interactor.new GsbiThread(3, pres);
    }

    /**
     * Test the scenario where a story by the provided ID doesn't exist
     */
    @Test
    @Timeout(3)
    public void testStoryDoesntExist() throws InterruptedException {
        setup(new RepoRes<>(Response.getFailure("No Story")),
                new RepoRes<>(Response.getFailure("No Title")),
                new GsbiOutputBoundary() {
                    @Override
                    public void putStories(@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res) {
                        assertNull(storyDTOS);
                        assertEquals(res.getCode(), Response.ResCode.FAIL);
                    }

                    @Override
                    public void outputShutdownServer() {}
                });
        innerThread.threadLogic();
    }

    /**
     * Test the scenario where a story by the provided ID exists and has a title
     */
    @Test
    @Timeout(3)
    public void testStoryExistsWithTitle() throws InterruptedException {
        setup(new RepoRes<>(Response.getSuccessful("Story Found"),
                        List.of(new StoryRepoData(
                                3, "Hey", new String[]{"Bob"}, LocalDateTime.now(), 10
                        ))),
                new RepoRes<>(Response.getSuccessful("Title Found"),
                        List.of("Awesome Title")),
                new GsbiOutputBoundary() {
                    @Override
                    public void putStories(@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res) {
                        assertNotNull(storyDTOS);
                        assertEquals(res.getCode(), Response.ResCode.SUCCESS);
                        assertEquals(storyDTOS.get(0).title(), "Awesome Title");
                        assertEquals(storyDTOS.get(0).storyData().getStoryId(), 3);
                        assertEquals(storyDTOS.get(0).storyData().getStory(), "Hey");
                    }

                    @Override
                    public void outputShutdownServer() {}
                });
        innerThread.threadLogic();
    }

    /**
     * Test the scenario where a story by the provided ID exists and has no title
     */
    @Test
    @Timeout(3)
    public void testStoryExistsWithoutTitle() throws InterruptedException {
        setup(new RepoRes<>(Response.getSuccessful("Story Found"),
                        List.of(new StoryRepoData(
                                3, "Hey", new String[]{"Bob"}, LocalDateTime.now(), 10
                        ))),
                new RepoRes<>(Response.getFailure("No Title")),
                new GsbiOutputBoundary() {
                    @Override
                    public void putStories(@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res) {
                        assertNotNull(storyDTOS);
                        assertEquals(res.getCode(), Response.ResCode.SUCCESS);
                        assertNull(storyDTOS.get(0).title());
                        assertEquals(storyDTOS.get(0).storyData().getStoryId(), 3);
                        assertEquals(storyDTOS.get(0).storyData().getStory(), "Hey");
                    }

                    @Override
                    public void outputShutdownServer() {}
                });
        innerThread.threadLogic();
    }
}
