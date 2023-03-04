package usecases.get_latest_stories;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GlsInteractorTests {
    private static final ThreadRegister register = new ThreadRegister();

    /**
    * Customizable class to imitate GlsPresenter during testing SDJAKAJSDKDJSKKDJAJKSA
    */
    static class CustomizableGlsOutputBoundary implements GlsOutputBoundary {

        private List<FullStoryDTO> story_dtos;

        public CustomizableGlsOutputBoundary() {
            story_dtos = null;
        }

        public List<FullStoryDTO> getStoryDTOs(){
            return story_dtos;
        }

        @Override
        public void outputShutdownServer() {
            throw new RuntimeException("This method is not implemented and should not be called");
        }

        @Override
        public void putStories(@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res) {
            story_dtos = storyDTOS;
        }
    }

    /**
     * Customizable class to imitate repository with stories during testing
     */
    static class CustomizableGlsGatewayStory implements GlsGatewayStory {

        private final List<StoryRepoData> data;

        public CustomizableGlsGatewayStory(List<StoryRepoData> stories) {
            this.data = stories;
        }

        public @NotNull RepoRes<StoryRepoData> getAllStories() {
            return new RepoRes<>(Response.getSuccessful(""), data);
        }
    }

    /**
     * Customizable class to imitate repository with titles during testing
     */
    static class CustomizableGlsGatewayTitle implements GlsGatewayTitles {

        private final List<TitleRepoData> data;

        public CustomizableGlsGatewayTitle(List<TitleRepoData> titles) {
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

    private static final String[] authors = {"Jeremy", "Stephen"};

    GlsOutputBoundary pres;
    GlsGatewayStory story_repo;
    GlsGatewayTitles title_repo;

    /**
     * In the setup, we only initialize our repository with stories
     */
    @BeforeEach
    public void setup () {

        LocalDateTime dt1 = LocalDateTime.of(2001, Month.JULY, 29, 19, 30, 40);
        LocalDateTime dt2 = LocalDateTime.of(2002, Month.JULY, 29, 19, 30, 40);
        LocalDateTime dt3 = LocalDateTime.of(2003, Month.JULY, 29, 19, 30, 40);

        StoryRepoData sd1 = new StoryRepoData(0, "text 1", authors, dt1, 1);
        StoryRepoData sd2 = new StoryRepoData(1, "text 2", authors, dt2, 2);
        StoryRepoData sd3 = new StoryRepoData(2, "text 3", authors, dt3, 3);
        StoryRepoData[] stories = {sd1, sd2, sd3};

        TitleRepoData td1 = new TitleRepoData(0, 0,"text 1", 1);
        TitleRepoData td2 = new TitleRepoData(1, 1,"text 2", 2);
        TitleRepoData td3 = new TitleRepoData(2, 2,"text 3", 3);
        TitleRepoData[] titles = {td1, td2, td3};

        story_repo = new CustomizableGlsGatewayStory(Arrays.asList(stories));
        title_repo = new CustomizableGlsGatewayTitle(Arrays.asList(titles));
    }

    @AfterEach
    public void teardown () {

    }

    /**
     * Testing the case when numToGet is within the size of the repository.
     * Expect to receive numToGet stories starting from the latest one
     */
    @Test
    @Timeout(1)

    public void testSimpleTest() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(story_repo, title_repo, register);

        GlsInputData d = new GlsInputData(2);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGlsOutputBoundary) pres).getStoryDTOs();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        assertEquals(2, receivedStories.size(), "Returned wrong number of stories");
        assertEquals("text 3", receivedStories.get(0).storyData().getStory(), "Returned incorrect story");
        assertEquals("text 2", receivedStories.get(1).storyData().getStory(), "Returned incorrect story");
    }

    /**
     * Testing the case when numToGet is zero.
     * Expect to receive empty DataStory[], but not null
     */
    @Test
    @Timeout(1)
    public void testZeroTest() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(story_repo, title_repo, register);

        GlsInputData d = new GlsInputData(0);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGlsOutputBoundary) pres).getStoryDTOs();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        assertEquals(0, receivedStories.size(), "Returned wrong number of stories");
    }

    /**
     * Testing  the case when numToGet is null.
     * Expect to receive all available stories from latest to earliest
     */
    @Test
    @Timeout(1)
    public void testNull() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(story_repo, title_repo, register);

        GlsInputData d = new GlsInputData(null);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGlsOutputBoundary) pres).getStoryDTOs();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        assertEquals(3, receivedStories.size(), "Returned wrong number of stories");
        assertEquals("text 3", receivedStories.get(0).storyData().getStory(), "Returned incorrect story");
        assertEquals("text 2", receivedStories.get(1).storyData().getStory(), "Returned incorrect story");
        assertEquals("text 1", receivedStories.get(2).storyData().getStory(), "Returned incorrect story");
    }

    /**
     * Testing the case when numToGet exceeds repository size.
     * Expect to receive all available stories from latest to earliest
     */
    @Test
    @Timeout(1)
    public void testOutOfBound() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(story_repo, title_repo, register);

        // Running inner thread
        GlsInputData d = new GlsInputData(10);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d, pres);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        List<FullStoryDTO> receivedStories = ((CustomizableGlsOutputBoundary) pres).getStoryDTOs();
        assertNotNull(receivedStories, "Presenter was not accessed");

        // Verify received data is correct
        assertEquals(3, receivedStories.size(), "Returned wrong number of stories");
        assertEquals("text 3", receivedStories.get(0).storyData().getStory(), "Returned incorrect story");
        assertEquals("text 2", receivedStories.get(1).storyData().getStory(), "Returned incorrect story");
        assertEquals("text 1", receivedStories.get(2).storyData().getStory(), "Returned incorrect story");
    }
}
