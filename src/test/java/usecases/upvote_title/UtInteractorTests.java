package usecases.upvote_title;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.Response;
import usecases.ThreadRegister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class UtInteractorTests {
    private static final ThreadRegister register = new ThreadRegister();
    UtOutputBoundary pres;
    UtGatewayTitles repo;

    /**
     * Customizable class to imitate UtPresenter during testing
     */
    static class CustomizableUtOutputBoundary implements UtOutputBoundary {

        private UtOutputData receivedData;

        public CustomizableUtOutputBoundary() {
            this.receivedData = null;
        }

        public void upvoteOutput(UtOutputData d) {
            this.receivedData = d;
        }

        public UtOutputData getReceivedData() {
            return this.receivedData;
        }

        @Override
        public void outputShutdownServer() {
            throw new RuntimeException("This method is not implemented and should not be called");
        }
    }

    /**
     * Customizable class to imitate repository with titles during testing.
     * Records received data to be compared with applied requests
     */
    static class CustomizableUtGatewayTitles implements UtGatewayTitles {

        private final boolean upvoteTitleSuccess;

        /**
         * Constructor of our customizable gateway
         * @param upvoteTitleSuccess determines return value for upvoteTitle
         */
        public CustomizableUtGatewayTitles(boolean upvoteTitleSuccess) {
            this.upvoteTitleSuccess = upvoteTitleSuccess;
        }

        /**
         * Method imitating repository's upvoteTitle with custom element based on
         * upvoteTitleSuccess parameter.
         * @param storyId requested storyId
         * @param titleToUpvote requested title as String
         * @return "success" and predetermined output when getAllTitlesSuccess is true,
         * and "failure" with null otherwise
         */
        public @NotNull Response upvoteTitle(int storyId, String titleToUpvote) {
            if (upvoteTitleSuccess) {
                return Response.getSuccessful("Customizable UtGateway would accept anything");
            }
            return Response.getFailure("Customizable UtGateway would reject anything");
        }

    }

    @BeforeEach
    public void setup () {

    }

    @AfterEach
    public void teardown () {

    }

    /**
     * Testing when repo fails and returns failure response.
     * We expect to receive FAIL code
     */
    @Test
    @Timeout(1000)
    public void testRepoFails() {

        // Instantiating interactor
        repo = new CustomizableUtGatewayTitles(false);
        pres = new CustomizableUtOutputBoundary();
        UtInteractor ut = new UtInteractor(pres, repo, register);

        // Running thread
        UtInputData d = new UtInputData("request1", 1, "Lovely story");
        UtInteractor.UtThread innerThreadInstance = ut.new UtThread(d);
        innerThreadInstance.run();

        // Verifying results
        UtOutputData receivedData = ((CustomizableUtOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.FAIL, receivedData.getRes().getCode(), "Wrong code");
    }


    /**
     * Testing when repo succeeds and returns success response.
     * We expect to receive SUCCESS code
     */
    @Test
    @Timeout(1000)
    public void testRepoSucceeds() {

        // Instantiating interactor
        repo = new CustomizableUtGatewayTitles(true);
        pres = new CustomizableUtOutputBoundary();
        UtInteractor ut = new UtInteractor(pres, repo, register);

        // Running thread
        UtInputData d = new UtInputData("request1", 1, "Lovely story");
        UtInteractor.UtThread innerThreadInstance = ut.new UtThread(d);
        innerThreadInstance.run();

        // Verifying results
        UtOutputData receivedData = ((CustomizableUtOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.SUCCESS, receivedData.getRes().getCode(), "Wrong code");
    }

}
