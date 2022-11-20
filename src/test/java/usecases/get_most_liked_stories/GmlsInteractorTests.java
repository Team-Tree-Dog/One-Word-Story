package usecases.get_most_liked_stories;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.*;

public class GmlsInteractorTests {

    /**
     * Customizable class to imitate GmlsPresenter during testing
     */
    class CustomizableGmlsOutputBoundary implements GmlsOutputBoundary {

        private GmlsOutputData receivedData;

        public CustomizableGmlsOutputBoundary() {
            this.receivedData = null;
        }

        public void putStories(GmlsOutputData d) {
            this.receivedData = d;
        }

        public GmlsOutputData getReceivedData() {
            return this.receivedData;
        }
    }

    /**
     * Customizable class to imitate repository with stories during testing
     */
    class CustomizableGmlsGateway implements GmlsGateway {

        private final GmlsGatewayOutputData data;

        public CustomizableGmlsGateway(StoryData[] stories) {
            this.data = new GmlsGatewayOutputData(stories);
        }

        public GmlsGatewayOutputData getAllStories() {
            return data;
        }
    }

    private static LocalDateTime dt = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
    private static String[] authors = {"Jeremy", "Stephen"};

    GmlsOutputBoundary pres;
    GmlsGateway repo;

    /**
     * In the setup, we only initialize our repository with stories
     */
    @Before
    public void setup () {

        StoryData sd1 = new StoryData("text 1", authors, dt, "title 1", 1);
        StoryData sd2 = new StoryData("text 2", authors, dt, "title 2", 2);
        StoryData sd3 = new StoryData("text 3", authors, dt, "title 3", 3);
        StoryData[] stories = {sd1, sd2, sd3};
        repo = new CustomizableGmlsGateway(stories);
    }

    @After
    public void teardown () {

    }

    /**
     * Simple test without any curveballs
     */
    @Test(timeout = 1000)
    public void testSimpleTest() {

        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo);

        GmlsInputData d = new GmlsInputData(0, 2);
        gmls.getLatestStories(d);

        StoryData[] stories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData().getStories();
        assertNotNull("Presenter was not accessed", stories);
        assertEquals("Returned wrong number of stories", 2, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[2].getStory());
    }

    /**
     * Testing left null
     */
    @Test(timeout = 1000)
    public void testLeftNull() {

        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo);

        GmlsInputData d = new GmlsInputData(null, 2);
        gmls.getLatestStories(d);

        StoryData[] stories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData().getStories();
        assertNotNull("Presenter was not accessed", stories);
        assertEquals("Returned wrong number of stories", 2, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
    }

    /**
     * Testing right null
     */
    @Test(timeout = 1000)
    public void testRightNull() {

        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo);

        GmlsInputData d = new GmlsInputData(1, null);
        gmls.getLatestStories(d);

        StoryData[] stories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData().getStories();
        assertNotNull("Presenter was not accessed", stories);
        assertEquals("Returned wrong number of stories", 2, stories.length);
        assertEquals("Returned incorrect story", "text 2", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[1].getStory());
    }

    /**
     * Testing both null
     */
    @Test(timeout = 1000)
    public void testBothNull() {

        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo);

        GmlsInputData d = new GmlsInputData(null, null);
        gmls.getLatestStories(d);

        StoryData[] stories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData().getStories();
        assertNotNull("Presenter was not accessed", stories);
        assertEquals("Returned wrong number of stories", 3, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[2].getStory());
    }

    /**
     * Testing right bound <= left bound
     */
    @Test(timeout = 1000)
    public void testInvalidInput() {

        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo);

        GmlsInputData d = new GmlsInputData(2, 0);
        gmls.getLatestStories(d);

        StoryData[] stories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData().getStories();
        assertNotNull("Presenter was not accessed", stories);
        assertEquals("Returned wrong number of stories", 0, stories.length);
    }

    /**
     * Testing left and right endpoint out of bounds
     */
    @Test(timeout = 1000)
    public void testOutOfBounds() {

        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo);

        GmlsInputData d = new GmlsInputData(-10, 10);
        gmls.getLatestStories(d);

        StoryData[] stories = ((CustomizableGmlsOutputBoundary) pres).getReceivedData().getStories();
        assertNotNull("Presenter was not accessed", stories);
        assertEquals("Returned wrong number of stories", 3, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[2].getStory());
    }
}
