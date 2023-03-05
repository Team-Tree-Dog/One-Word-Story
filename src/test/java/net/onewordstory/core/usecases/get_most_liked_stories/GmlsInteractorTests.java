package net.onewordstory.core.usecases.get_most_liked_stories;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import net.onewordstory.core.usecases.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GmlsInteractorTests {


    private static final ThreadRegister register = new ThreadRegister();

    /**
     * Customizable class to imitate GmlsPresenter during testing
     */
    static class CustomizableGmlsOutputBoundary implements GmlsOutputBoundary {

        private List<FullStoryDTO> receivedData;

        public CustomizableGmlsOutputBoundary() {
            this.receivedData = null;
        }

        public List<FullStoryDTO> getReceivedData() {
            return this.receivedData;
        }

        @Override
        public void outputShutdownServer() {
            throw new RuntimeException("This method is not implemented and should not be called");
        }

        @Override
        public void putStories(@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res) {
            this.receivedData = storyDTOS;
        }
    }

    /**
     * Customizable class to imitate repository with stories during testing
     */
    static class CustomizableGmlsGatewayStory implements GmlsGatewayStory {

        private final List<StoryRepoData> data;

        public CustomizableGmlsGatewayStory(List<StoryRepoData> stories) {
            this.data = stories;
        }

        public @NotNull RepoRes<StoryRepoData> getAllStories() {
            return new RepoRes<>(Response.getSuccessful(""), data);
        }
    }

    /**
     * Customizable class to imitate repository with titles during testing
     */
    static class CustomizableGmlsGatewayTitle implements GmlsGatewayTitles {

        private final List<TitleRepoData> data;

        public CustomizableGmlsGatewayTitle(List<TitleRepoData> titles) {
            this.data = titles;
        }

        @Override
        public @NotNull RepoRes<String> getMostUpvotedStoryTitle(int storyId) {

            TitleRepoData currWinnerTRD = new TitleRepoData(-1, -1, null, -1);
            int currUpvoteWinner = 0;

            for (TitleRepoData trd : data) {
                if (trd.getStoryId() == storyId) {
                    if (trd.getUpvotes() > currUpvoteWinner) {
                        currWinnerTRD = trd;
                        currUpvoteWinner = trd.getUpvotes();
                    }
                }
            }

            return new RepoRes<>(Response.getSuccessful(""),
                    new ArrayList<>(Collections.singleton(currWinnerTRD.getTitle())));
        }
    }
    private static final LocalDateTime dt = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
    private static final String[] authors = {"Jeremy", "Stephen"};

    GmlsOutputBoundary pres;
    GmlsGatewayStory story_repo;
    GmlsGatewayTitles title_repo;

    /**
     * In the setup, we only initialize our repository with stories
     */
    @BeforeEach
    public void setup () {

        StoryRepoData sd1 = new StoryRepoData(0, "text 1", authors, dt, 1);
        StoryRepoData sd2 = new StoryRepoData(1, "text 2", authors, dt, 2);
        StoryRepoData sd3 = new StoryRepoData(2,"text 3", authors, dt, 3);
        StoryRepoData[] stories = {sd1, sd2, sd3};

        TitleRepoData td1 = new TitleRepoData(0, 0,"text 1", 1);
        TitleRepoData td2 = new TitleRepoData(1, 1,"text 2", 2);
        TitleRepoData td3 = new TitleRepoData(2, 2,"text 3", 3);
        TitleRepoData[] titles = {td1, td2, td3};

        story_repo = new CustomizableGmlsGatewayStory(Arrays.asList(stories));
        title_repo = new CustomizableGmlsGatewayTitle(Arrays.asList(titles));
    }

    @AfterEach
    public void teardown () {

    }

    /**
     * Testing the case where endpoints are within boundaries.
     * Expect to receive output in accordance with specifications,
     * similar to [l:r] slice in Python
     */
    @Test
    @Timeout(1000)
    public void testSimpleTest() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(story_repo, title_repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(0, 2);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        FullStoryDTO[] stories = receivedStories.toArray(new FullStoryDTO[0]);
        assertEquals(2, stories.length, "Returned wrong number of stories");
        assertEquals("text 3", stories[0].storyData().getStory(), "Returned incorrect story");
        assertEquals("text 2", stories[1].storyData().getStory(), "Returned incorrect story");
    }

    /**
     * Testing the case where left endpoint is null.
     * Expect to receive output in accordance with specifications,
     * similar to [:r] slice in Python
     */
    @Test
    @Timeout(1000)
    public void testLeftNull() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(story_repo, title_repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(null, 2);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        FullStoryDTO[] stories = receivedStories.toArray(new FullStoryDTO[0]);
        assertEquals(2, stories.length, "Returned wrong number of stories");
        assertEquals( "text 3", stories[0].storyData().getStory(), "Returned incorrect story");
        assertEquals("text 2", stories[1].storyData().getStory(), "Returned incorrect story");
    }

    /**
     * Testing the case where right endpoint is null.
     * Expect to receive output in accordance with specifications,
     * similar to [l:] slice in Python
     */
    @Test
    @Timeout(1000)
    public void testRightNull() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(story_repo, title_repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(1, null);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        FullStoryDTO[] stories = receivedStories.toArray(new FullStoryDTO[0]);
        assertEquals(2, stories.length, "Returned wrong number of stories");
        assertEquals("text 2", stories[0].storyData().getStory(), "Returned incorrect story");
        assertEquals("text 1", stories[1].storyData().getStory(), "Returned incorrect story");
    }

    /**
     * Testing the case where both endpoints are null.
     * Expect to receive all stories in the repository,
     * similar to [:] slice in Python
     */
    @Test
    @Timeout(1000)
    public void testBothNull() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(story_repo, title_repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(null, null);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        FullStoryDTO[] stories = receivedStories.toArray(new FullStoryDTO[0]);
        assertEquals(3, stories.length, "Returned wrong number of stories");
        assertEquals( "text 3", stories[0].storyData().getStory(), "Returned incorrect story");
        assertEquals( "text 2", stories[1].storyData().getStory(), "Returned incorrect story");
        assertEquals( "text 1", stories[2].storyData().getStory(), "Returned incorrect story");
    }

    /**
     * Testing the case where endpoints are not in increasing order, i.e. left >= right.
     * Expect to receive empty data, but not null
     */
    @Test
    @Timeout(1000)
    public void testInvalidInput() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(story_repo, title_repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(2, 1);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        FullStoryDTO[] stories = receivedStories.toArray(new FullStoryDTO[0]);
        assertEquals(0, stories.length, "Returned wrong number of stories");
    }

    /**
     * Testing the case where endpoints are out of bound.
     * Expect to receive output in accordance with specifications,
     * full dataset in this case
     */
    @Test
    @Timeout(1000)
    public void testOutOfBounds() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(story_repo, title_repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(-10, 10);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d ,pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        FullStoryDTO[] stories = receivedStories.toArray(new FullStoryDTO[0]);
        assertEquals(3, stories.length, "Returned wrong number of stories");
        assertEquals( "text 3", stories[0].storyData().getStory(), "Returned incorrect story");
        assertEquals( "text 2", stories[1].storyData().getStory(), "Returned incorrect story");
        assertEquals( "text 1", stories[2].storyData().getStory(), "Returned incorrect story");
    }
}
