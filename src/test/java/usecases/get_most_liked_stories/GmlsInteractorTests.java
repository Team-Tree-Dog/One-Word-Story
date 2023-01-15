package usecases.get_most_liked_stories;

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

public class GmlsInteractorTests {

// TODO: Fix these tests, major changes caused these tests to break

//    private static final ThreadRegister register = new ThreadRegister();
//
//    /**
//     * Customizable class to imitate GmlsPresenter during testing
//     */
//    class CustomizableGmlsOutputBoundary implements GmlsOutputBoundary {
//
//        private GmlsOutputData receivedData;
//
//        public CustomizableGmlsOutputBoundary() {
//            this.receivedData = null;
//        }
//
//        public void putStories(GmlsOutputData d) {
//            this.receivedData = d;
//        }
//
//        public GmlsOutputData getReceivedData() {
//            return this.receivedData;
//        }
//
//        @Override
//        public void outputShutdownServer() {
//            throw new RuntimeException("This method is not implemented and should not be called");
//        }
//    }
//
//    /**
//     * Customizable class to imitate repository with stories during testing
//     */
//    class CustomizableGmlsGateway implements GmlsGatewayStory {
//
//        private final List<StoryRepoData> data;
//
//        public CustomizableGmlsGateway(List<StoryRepoData> stories) {
//            this.data = stories;
//        }
//
//        public @NotNull RepoRes<StoryRepoData> getAllStories() {
//            return new RepoRes<StoryRepoData>(Response.getSuccessful(""), data);
//        }
//    }
//
//    private static final LocalDateTime dt = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
//    private static final String[] authors = {"Jeremy", "Stephen"};
//
//    GmlsOutputBoundary pres;
//    GmlsGatewayStory repo;
//
//    /**
//     * In the setup, we only initialize our repository with stories
//     */
//    @BeforeEach
//    public void setup () {
//
//        // Adding three stories for testing purposes
//        StoryRepoData sd1 = new StoryRepoData(0, "text 1", authors, dt, 1);
//        StoryRepoData sd2 = new StoryRepoData(1, "text 2", authors, dt, 2);
//        StoryRepoData sd3 = new StoryRepoData(2,"text 3", authors, dt, 3);
//
//        StoryRepoData[] stories = {sd1, sd2, sd3};
//        repo = new CustomizableGmlsGateway(Arrays.asList(stories));
//    }
//
//    @AfterEach
//    public void teardown () {
//
//    }
//
//    /**
//     * Testing the case where endpoints are within boundaries.
//     * Expect to receive output in accordance with specifications,
//     * similar to [l:r] slice in Python
//     */
//    @Test
//    @Timeout(1000)
//    public void testSimpleTest() {
//
//        // Instantiating interactor
//        pres = new CustomizableGmlsOutputBoundary();
//        GmlsInteractor gmls = new GmlsInteractor(repo, register);
//
//        // Running inner thread
//        GmlsInputData d = new GmlsInputData(0, 2);
//        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
//        innerThreadInstance.run();
//
//        // Check presenter receives non-null data
//        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
//        assertNotNull(receivedData, "Presenter was not accessed");
//
//        // Verify received data is correct
//        StoryRepoData[] stories = receivedData.stories().toArray(new StoryRepoData[0]);
//        assertEquals(2, stories.length, "Returned wrong number of stories");
//        assertEquals("text 3", stories[0].getStory(), "Returned incorrect story");
//        assertEquals("text 2", stories[1].getStory(), "Returned incorrect story");
//    }
//
//    /**
//     * Testing the case where left endpoint is null.
//     * Expect to receive output in accordance with specifications,
//     * similar to [:r] slice in Python
//     */
//    @Test
//    @Timeout(1000)
//    public void testLeftNull() {
//
//        // Instantiating interactor
//        pres = new CustomizableGmlsOutputBoundary();
//        GmlsInteractor gmls = new GmlsInteractor(repo, register);
//
//        // Running inner thread
//        GmlsInputData d = new GmlsInputData(null, 2);
//        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
//        innerThreadInstance.run();
//
//        // Check presenter receives non-null data
//        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
//        assertNotNull(receivedData, "Presenter was not accessed");
//
//        // Verify received data is correct
//        StoryRepoData[] stories = receivedData.stories().toArray(new StoryRepoData[0]);
//        assertEquals(2, stories.length, "Returned wrong number of stories");
//        assertEquals( "text 3", stories[0].getStory(), "Returned incorrect story");
//        assertEquals("text 2", stories[1].getStory(), "Returned incorrect story");
//    }
//
//    /**
//     * Testing the case where right endpoint is null.
//     * Expect to receive output in accordance with specifications,
//     * similar to [l:] slice in Python
//     */
//    @Test
//    @Timeout(1000)
//    public void testRightNull() {
//
//        // Instantiating interactor
//        pres = new CustomizableGmlsOutputBoundary();
//        GmlsInteractor gmls = new GmlsInteractor(repo, register);
//
//        // Running inner thread
//        GmlsInputData d = new GmlsInputData(1, null);
//        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
//        innerThreadInstance.run();
//
//        // Check presenter receives non-null data
//        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
//        assertNotNull(receivedData, "Presenter was not accessed");
//
//        // Verify received data is correct
//        StoryRepoData[] stories = receivedData.stories().toArray(new StoryRepoData[0]);
//        assertEquals(2, stories.length, "Returned wrong number of stories");
//        assertEquals("text 2", stories[0].getStory(), "Returned incorrect story");
//        assertEquals("text 1", stories[1].getStory(), "Returned incorrect story");
//    }
//
//    /**
//     * Testing the case where both endpoints are null.
//     * Expect to receive all stories in the repository,
//     * similar to [:] slice in Python
//     */
//    @Test
//    @Timeout(1000)
//    public void testBothNull() {
//
//        // Instantiating interactor
//        pres = new CustomizableGmlsOutputBoundary();
//        GmlsInteractor gmls = new GmlsInteractor(repo, register);
//
//        // Running inner thread
//        GmlsInputData d = new GmlsInputData(null, null);
//        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
//        innerThreadInstance.run();
//
//        // Check presenter receives non-null data
//        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
//        assertNotNull(receivedData, "Presenter was not accessed");
//
//        // Verify received data is correct
//        StoryRepoData[] stories = receivedData.stories().toArray(new StoryRepoData[0]);
//        assertEquals(3, stories.length, "Returned wrong number of stories");
//        assertEquals( "text 3", stories[0].getStory(), "Returned incorrect story");
//        assertEquals( "text 2", stories[1].getStory(), "Returned incorrect story");
//        assertEquals( "text 1", stories[2].getStory(), "Returned incorrect story");
//    }
//
//    /**
//     * Testing the case where endpoints are not in increasing order, i.e. left >= right.
//     * Expect to receive empty data, but not null
//     */
//    @Test
//    @Timeout(1000)
//    public void testInvalidInput() {
//
//        // Instantiating interactor
//        pres = new CustomizableGmlsOutputBoundary();
//        GmlsInteractor gmls = new GmlsInteractor(repo, register);
//
//        // Running inner thread
//        GmlsInputData d = new GmlsInputData(2, 1);
//        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d, pres);
//        innerThreadInstance.run();
//
//        // Check presenter receives non-null data
//        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
//        assertNotNull(receivedData, "Presenter was not accessed");
//
//        // Verify received data is correct
//        StoryRepoData[] stories = receivedData.stories().toArray(new StoryRepoData[0]);
//        assertEquals(0, stories.length, "Returned wrong number of stories");
//    }
//
//    /**
//     * Testing the case where endpoints are out of bound.
//     * Expect to receive output in accordance with specifications,
//     * full dataset in this case
//     */
//    @Test
//    @Timeout(1000)
//    public void testOutOfBounds() {
//
//        // Instantiating interactor
//        pres = new CustomizableGmlsOutputBoundary();
//        GmlsInteractor gmls = new GmlsInteractor(repo, register);
//
//        // Running inner thread
//        GmlsInputData d = new GmlsInputData(-10, 10);
//        GmlsInteractor.GmlsThread innerThreadInstance = gmls.new GmlsThread(d ,pres);
//        innerThreadInstance.run();
//
//        // Check presenter receives non-null data
//        GmlsOutputData receivedData = ((CustomizableGmlsOutputBoundary) pres).getReceivedData();
//        assertNotNull(receivedData, "Presenter was not accessed");
//
//        // Verify received data is correct
//        StoryRepoData[] stories = receivedData.stories().toArray(new StoryRepoData[0]);
//        assertEquals(3, stories.length, "Returned wrong number of stories");
//        assertEquals( "text 3", stories[0].getStory(), "Returned incorrect story");
//        assertEquals( "text 2", stories[1].getStory(), "Returned incorrect story");
//        assertEquals( "text 1", stories[2].getStory(), "Returned incorrect story");
//    }
}
