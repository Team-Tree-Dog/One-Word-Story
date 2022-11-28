package usecases.get_latest_stories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.StoryData;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;


public class GlsInteractorTests {

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
    }

    /**
     * Customizable class to imitate repository with stories during testing
     */
    static class CustomizableGlsGateway implements GlsGateway {

        private final GlsGatewayOutputData data;

        public CustomizableGlsGateway(StoryData[] stories) {
            this.data = new GlsGatewayOutputData(stories);
        }

        public GlsGatewayOutputData getAllStories() {
            return data;
        }
    }

    private static final String[] authors = {"Jeremy", "Stephen"};

    GlsOutputBoundary pres;
    GlsGateway repo;

    /**
     * In the setup, we only initialize our repository with stories
     */
    @BeforeEach
    public void setup () {

        LocalDateTime dt1 = LocalDateTime.of(2001, Month.JULY, 29, 19, 30, 40);
        LocalDateTime dt2 = LocalDateTime.of(2002, Month.JULY, 29, 19, 30, 40);
        LocalDateTime dt3 = LocalDateTime.of(2003, Month.JULY, 29, 19, 30, 40);

        StoryData sd1 = new StoryData("text 1", authors, dt1, "title 1", 1);
        StoryData sd2 = new StoryData("text 2", authors, dt2, "title 2", 2);
        StoryData sd3 = new StoryData("text 3", authors, dt3, "title 3", 3);
        StoryData[] stories = {sd1, sd2, sd3};

        repo = new CustomizableGlsGateway(stories);
    }

    @AfterEach
    public void teardown () {

    }

    /**
     * Testing the case when numToGet is within the size of the repository.
     * Expect to receive numToGet stories starting from the latest one
     */
    @Test
    @Timeout(1000)
    public void testSimpleTest() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(pres, repo);

        GlsInputData d = new GlsInputData(2);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals(2, stories.length, "Returned wrong number of stories");
        assertEquals("text 3", stories[0].getStory(), "Returned incorrect story");
        assertEquals("text 2", stories[1].getStory(), "Returned incorrect story");
    }

    /**
     * Testing the case when numToGet is zero.
     * Expect to receive empty DataStory[], but not null
     */
    @Test
    @Timeout(1000)
    public void testZeroTest() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(pres, repo);

        GlsInputData d = new GlsInputData(0);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals(0, stories.length, "Returned wrong number of stories");
    }

    /**
     * Testing  the case when numToGet is null.
     * Expect to receive all available stories from latest to earliest
     */
    @Test
    @Timeout(1000)
    public void testNull() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(pres, repo);

        // Running inner thread
        GlsInputData d = new GlsInputData(null);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
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
    @Timeout(1000)
    public void testOutOfBound() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(pres, repo);

        // Running inner thread
        GlsInputData d = new GlsInputData(10);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals(3, stories.length, "Returned wrong number of stories");
        assertEquals("text 3", stories[0].getStory(), "Returned incorrect story");
        assertEquals("text 2", stories[1].getStory(), "Returned incorrect story");
        assertEquals("text 1", stories[2].getStory(), "Returned incorrect story");
    }

}

