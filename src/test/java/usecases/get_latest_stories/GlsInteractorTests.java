package usecases.get_latest_stories;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import usecases.StoryData;
import usecases.run_game.RgInteractor;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class GlsInteractorTests {

    /**
     * Customizable class to imitate GlsPresenter during testing
     */
    class CustomizableGlsOutputBoundary implements GlsOutputBoundary {

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
    class CustomizableGlsGateway implements GlsGateway {

        private final GlsGatewayOutputData data;

        public CustomizableGlsGateway(StoryData[] stories) {
            this.data = new GlsGatewayOutputData(stories);
        }

        public GlsGatewayOutputData getAllStories() {
            return data;
        }
    }

    private static String[] authors = {"Jeremy", "Stephen"};

    GlsOutputBoundary pres;
    GlsGateway repo;

    /**
     * In the setup, we only initialize our repository with stories
     */
    @Before
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

    @After
    public void teardown () {

    }

    /**
     * Testing the case when numToGet is within the size of the repository.
     * Expect to receive numToGet stories starting from the latest one
     */
    @Test(timeout = 1000)
    public void testSimpleTest() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(pres, repo);

        GlsInputData d = new GlsInputData(2);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 2, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
    }

    /**
     * Testing the case when numToGet is zero.
     * Expect to receive empty DataStory[], but not null
     */
    @Test(timeout = 1000)
    public void testZeroTest() {

        // Instantiating interactor
        pres = new CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(pres, repo);

        GlsInputData d = new GlsInputData(0);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 0, stories.length);
    }

    /**
     * Testing  the case when numToGet is null.
     * Expect to receive all available stories from latest to earliest
     */
    @Test(timeout = 1000)
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
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 3, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[2].getStory());
    }

    /**
     * Testing the case when numToGet exceeds repository size.
     * Expect to receive all available stories from latest to earliest
     */
    @Test(timeout = 1000)
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
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 3, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[2].getStory());
    }

}

