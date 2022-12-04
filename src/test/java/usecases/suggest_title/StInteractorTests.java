package usecases.suggest_title;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.Response;
import usecases.ThreadRegister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StInteractorTests {
    private static final ThreadRegister register = new ThreadRegister();

    /**
     * Customizable class to imitate StPresenter during testing
     */
    static class CustomizableStOutputBoundary implements StOutputBoundary {

        private StOutputData receivedData;

        public CustomizableStOutputBoundary() {
            this.receivedData = null;
        }

        public void suggestTitleOutput(StOutputData d) {
            this.receivedData = d;
        }

        public StOutputData getReceivedData() {
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
    static class CustomizableStGateway implements StGateway {

        private final RepoRes<TitleRepoData> data;

        public CustomizableStGateway(RepoRes<TitleRepoData> data) {
            this.data = data;
        }

        public Response suggestTitle(int storyId, String titleSuggestion) {
            return Response.getSuccessful("StGateWay testing. Assumes success");
        }

        public RepoRes<TitleRepoData> getAllTitles(int storyId, String titleSuggestion) {
            return data[storyId];
        }

    }

    StOutputBoundary pres;
    StGateway repo;

    /**
     * In the setup, we only initialize our repository with stories
     */
    @BeforeEach
    public void setup () {

        RepoRes<TitleRepoData>

        repo = new CustomizableStGateway(data);
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
        pres = new usecases.get_latest_stories.GlsInteractorTests.CustomizableGlsOutputBoundary();
        GlsInteractor gls = new GlsInteractor(pres, repo, register);

        GlsInputData d = new GlsInputData(2);
        GlsInteractor.GlsThread innerThreadInstance = gls.new GlsThread(d);
        innerThreadInstance.run();

        // Check presenter receives non-null data
        GlsOutputData receivedData = ((usecases.get_latest_stories.GlsInteractorTests.CustomizableGlsOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");

        // Verify received data is correct
        StoryData[] stories = receivedData.getStories();
        assertEquals(2, stories.length, "Returned wrong number of stories");
        assertEquals("text 3", stories[0].getStory(), "Returned incorrect story");
        assertEquals("text 2", stories[1].getStory(), "Returned incorrect story");
    }

}

