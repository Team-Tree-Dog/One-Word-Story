package usecases.get_latest_stories;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.RepoRes;
import usecases.Response;
import usecases.StoryRepoData;
import usecases.ThreadRegister;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GlsInteractorTests {

    private static final ThreadRegister register = new ThreadRegister();

    /**
     * Customizable class to imitate GlsPresenter during testing
     */
    static class CustomizableGlsOutputBoundary implements GlsOutputBoundary {

        private GlsOutputData receivedData;

        public CustomizableGlsOutputBoundary() {
            this.receivedData = null;
        }

        public void putStories(GlsOutputData d) {
            this.receivedData = d;
        }

        public GlsOutputData getReceivedData() {
            return this.receivedData;
        }

        @Override
        public void outputShutdownServer() {
            throw new RuntimeException("This method is not implemented and should not be called");
        }
    }

    /**
     * Customizable class to imitate repository with stories during testing
     */
    static class CustomizableGlsGateway implements GlsGatewayStory {

        private final List<StoryRepoData> data;

        public CustomizableGlsGateway(List<StoryRepoData> stories) {
            this.data = stories;
        }

        public @NotNull RepoRes<StoryRepoData> getAllStories() {
            return new RepoRes<StoryRepoData>(Response.getSuccessful(""), data);
        }
    }

    private static final String[] authors = {"Jeremy", "Stephen"};

    GlsOutputBoundary pres;
    GlsGatewayStory repo;

    /**
     * In the setup, we only initialize our repository with stories
     */
    @BeforeEach
    public void setup () {

        LocalDateTime dt1 = LocalDateTime.of(2001, Month.JULY, 29, 19, 30, 40);
        LocalDateTime dt2 = LocalDateTime.of(2002, Month.JULY, 29, 19, 30, 40);
        LocalDateTime dt3 = LocalDateTime.of(2003, Month.JULY, 29, 19, 30, 40);

        StoryRepoData sd1 = new StoryRepoData("text 1", authors, dt1, "title 1", 1);
        StoryRepoData sd2 = new StoryRepoData("text 2", authors, dt2, "title 2", 2);
        StoryRepoData sd3 = new StoryRepoData("text 3", authors, dt3, "title 3", 3);
        StoryRepoData[] stories = {sd1, sd2, sd3};

        repo = new CustomizableGlsGateway(Arrays.asList(stories));
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
        GlsInteractor gls = new GlsInteractor(pres, repo, register);

        GlsInputData d = new GlsInputData(2);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryRepoData[] stories = receivedData.getStories();
        assertEquals(2, stories.length, "Returned wrong number of stories");
        assertEquals("text 3", stories[0].getStory(), "Returned incorrect story");
        assertEquals("text 2", stories[1].getStory(), "Returned incorrect story");
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
        GlsInteractor gls = new GlsInteractor(pres, repo, register);

        GlsInputData d = new GlsInputData(0);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryRepoData[] stories = receivedData.getStories();
        assertEquals(0, stories.length, "Returned wrong number of stories");
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
        GlsInteractor gls = new GlsInteractor(pres, repo, register);

        // Running inner thread
        GlsInputData d = new GlsInputData(null);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryRepoData[] stories = receivedData.getStories();
        assertEquals(3, stories.length, "Returned wrong number of stories");
        assertEquals("text 3", stories[0].getStory(), "Returned incorrect story");
        assertEquals("text 2", stories[1].getStory(), "Returned incorrect story");
        assertEquals("text 1", stories[2].getStory(), "Returned incorrect story");
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
        GlsInteractor gls = new GlsInteractor(pres, repo, register);

        // Running inner thread
        GlsInputData d = new GlsInputData(10);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryRepoData[] stories = receivedData.getStories();
        assertEquals(3, stories.length, "Returned wrong number of stories");
        assertEquals("text 3", stories[0].getStory(), "Returned incorrect story");
        assertEquals("text 2", stories[1].getStory(), "Returned incorrect story");
        assertEquals("text 1", stories[2].getStory(), "Returned incorrect story");
    }

}

