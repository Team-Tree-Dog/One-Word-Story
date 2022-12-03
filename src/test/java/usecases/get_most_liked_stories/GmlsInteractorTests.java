package usecases.get_most_liked_stories;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import usecases.StoryData;
import usecases.ThreadRegister;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class GmlsInteractorTests {


    private static final ThreadRegister register = new ThreadRegister();

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

        @Override
        public void outputShutdownServer() {
            throw new RuntimeException("This method is not implemented and should not be called");
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

    private static final LocalDateTime dt = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
    private static final String[] authors = {"Jeremy", "Stephen"};

    GmlsOutputBoundary pres;
    GmlsGateway repo;

    /**
     * In the setup, we only initialize our repository with stories
     */
    @Before
    public void setup () {

        // Adding three stories for testing purposes
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
     * Testing the case where endpoints are within boundaries.
     * Expect to receive output in accordance with specifications,
     * similar to [l:r] slice in Python
     */
    @Test(timeout = 1000)
    public void testSimpleTest() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(0, 2);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 2, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
    }

    /**
     * Testing the case where left endpoint is null.
     * Expect to receive output in accordance with specifications,
     * similar to [:r] slice in Python
     */
    @Test(timeout = 1000)
    public void testLeftNull() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(null, 2);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 2, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
    }

    /**
     * Testing the case where right endpoint is null.
     * Expect to receive output in accordance with specifications,
     * similar to [l:] slice in Python
     */
    @Test(timeout = 1000)
    public void testRightNull() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(1, null);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 2, stories.length);
        assertEquals("Returned incorrect story", "text 2", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[1].getStory());
    }

    /**
     * Testing the case where both endpoints are null.
     * Expect to receive all stories in the repository,
     * similar to [:] slice in Python
     */
    @Test(timeout = 1000)
    public void testBothNull() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(null, null);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 3, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[2].getStory());
    }

    /**
     * Testing the case where endpoints are not in increasing order, i.e. left >= right.
     * Expect to receive empty data, but not null
     */
    @Test(timeout = 1000)
    public void testInvalidInput() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(2, 1);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 0, stories.length);
    }

    /**
     * Testing the case where endpoints are out of bound.
     * Expect to receive output in accordance with specifications,
     * full dataset in this case
     */
    @Test(timeout = 1000)
    public void testOutOfBounds() {

        // Instantiating interactor
        pres = new CustomizableGmlsOutputBoundary();
        GmlsInteractor gmls = new GmlsInteractor(pres, repo, register);

        // Running inner thread
        GmlsInputData d = new GmlsInputData(-10, 10);
        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
        assertNotNull("Presenter was not accessed", receivedData);

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals("Returned wrong number of stories", 3, stories.length);
        assertEquals("Returned incorrect story", "text 3", stories[0].getStory());
        assertEquals("Returned incorrect story", "text 2", stories[1].getStory());
        assertEquals("Returned incorrect story", "text 1", stories[2].getStory());
    }
}
