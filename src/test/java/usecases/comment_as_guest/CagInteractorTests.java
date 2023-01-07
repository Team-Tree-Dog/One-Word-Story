package usecases.comment_as_guest;

import entities.comment_checkers.CommentChecker;
import entities.display_name_checkers.DisplayNameChecker;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import usecases.Response;
import usecases.ThreadRegister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CagInteractorTests {
    private static final ThreadRegister register = new ThreadRegister();

    CagOutputBoundary pres;
    CagGatewayComments repo;
    CommentChecker commentChecker;
    DisplayNameChecker displayChecker;


    /**
     * Customizable class to imitate CagPresenter during testing
     */
    static class CustomizableCagOutputBoundary implements CagOutputBoundary {

        private CagOutputData receivedData;

        public CustomizableCagOutputBoundary() {
            this.receivedData = null;
        }

        public void commentAsGuestOutput(CagOutputData d) {
            this.receivedData = d;
        }

        public CagOutputData getReceivedData() {
            return this.receivedData;
        }

        @Override
        public void outputShutdownServer() {
            throw new RuntimeException("This method is not implemented and should not be called");
        }
    }

    /**
     * Customizable class to imitate repository with stories during testing.
     * Records received data to be compared with applied requests
     */
    static class CustomizableCagGatewayComments implements CagGatewayComments {

        private final boolean commentAsGuestSuccess;

        /**
         * Constructor of our customizable gateway
         * @param commentAsGuestSuccess determines return value for commentAsGuest
         */
        public CustomizableCagGatewayComments(boolean commentAsGuestSuccess) {
            this.commentAsGuestSuccess = commentAsGuestSuccess;
        }

        /**
         * Method imitating repository's commentAsGuest with custom element based on
         * commentAsGuestSuccess parameter.
         * @param storyId requested storyId
         * @param displayName proposed displayName of a guest
         * @param comment proposed comment
         * @return "success" when commentAsGuestSuccess is true, and "failure" otherwise
         */
        public @NotNull Response commentAsGuest(int storyId, @NotNull String displayName, @NotNull String comment) {
            if (this.commentAsGuestSuccess) {
                return Response.getSuccessful("Customizable CagGateway would accept anything");
            } else {
                return Response.getFailure("Customizable CagGateway would reject anything");
            }
        }

    }

    /**
     * Customizable class to imitate comment checker during testing.
     */
    static class CustomizableCommentChecker implements CommentChecker {

        private final boolean returnValue;

        public CustomizableCommentChecker(boolean returnValue) {
            this.returnValue = returnValue;
        }

        @Override
        public boolean checkValid(String title) {
            return this.returnValue;
        }

    }


    /**
     * Customizable class to imitate display name checker during testing.
     */
    static class CustomizableDisplayNameChecker implements DisplayNameChecker {

        private final boolean returnValue;

        public CustomizableDisplayNameChecker(boolean returnValue) {
            this.returnValue = returnValue;
        }

        @Override
        public boolean checkValid(String title) {
            return this.returnValue;
        }

    }

    /**
     * In the setup, we only initialize our repository with stories
     */
    @BeforeEach
    public void setup () {

    }

    @AfterEach
    public void teardown () {

    }

    /**
     * Testing when invalid comment is provided.
     * We expect to receive INVALID_COMMENT code
     */
    @Test
    @Timeout(1000)
    public void testInvalidComment() {

        // Instantiating interactor
        repo = new CustomizableCagGatewayComments(true);
        pres = new CustomizableCagOutputBoundary();
        commentChecker = new CustomizableCommentChecker(false);
        displayChecker = new CustomizableDisplayNameChecker(true);
        CagInteractor cag = new CagInteractor(repo, commentChecker, displayChecker, register);

        // Running thread
        CagInputData d = new CagInputData("Jim", "You call this a story", 1);
        CagInteractor.CagThread innerThreadInstance = cag.new CagThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        CagOutputData receivedData = ((CustomizableCagOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.INVALID_COMMENT, receivedData.getRes().getCode(), "Wrong code");
    }

    /**
     * Testing when invalid display name is provided.
     * We expect to receive INVALID_DISPLAY_NAME code
     */
    @Test
    @Timeout(1000)
    public void testInvalidDisplayName() {

        // Instantiating interactor
        repo = new CustomizableCagGatewayComments(true);
        pres = new CustomizableCagOutputBoundary();
        commentChecker = new CustomizableCommentChecker(true);
        displayChecker = new CustomizableDisplayNameChecker(false);
        CagInteractor cag = new CagInteractor(repo, commentChecker, displayChecker, register);

        // Running thread
        CagInputData d = new CagInputData("Jim", "You call this a story", 1);
        CagInteractor.CagThread innerThreadInstance = cag.new CagThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        CagOutputData receivedData = ((CustomizableCagOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.INVALID_DISPLAY_NAME, receivedData.getRes().getCode(), "Wrong code");
    }


    /**
     * Testing when repo returns failure in commentAsGuest.
     * We expect to receive FAIL code
     */
    @Test
    @Timeout(1000)
    public void testCommentAsGuestFailure() {

        // Instantiating interactor
        repo = new CustomizableCagGatewayComments(false);
        pres = new CustomizableCagOutputBoundary();
        commentChecker = new CustomizableCommentChecker(true);
        displayChecker = new CustomizableDisplayNameChecker(true);
        CagInteractor cag = new CagInteractor(repo, commentChecker, displayChecker, register);

        // Running thread
        CagInputData d = new CagInputData("Jim", "You call this a story", 1);
        CagInteractor.CagThread innerThreadInstance = cag.new CagThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        CagOutputData receivedData = ((CustomizableCagOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.FAIL, receivedData.getRes().getCode(), "Wrong code");
    }


    /**
     * Testing when repo returns success in commentAsGuest.
     * We expect to receive SUCCESS code
     */
    @Test
    @Timeout(1000)
    public void testCommentAsGuestSuccess() {

        // Instantiating interactor
        repo = new CustomizableCagGatewayComments(true);
        pres = new CustomizableCagOutputBoundary();
        commentChecker = new CustomizableCommentChecker(true);
        displayChecker = new CustomizableDisplayNameChecker(true);
        CagInteractor cag = new CagInteractor(repo, commentChecker, displayChecker, register);

        // Running thread
        CagInputData d = new CagInputData("Jim", "You call this a story", 1);
        CagInteractor.CagThread innerThreadInstance = cag.new CagThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        CagOutputData receivedData = ((CustomizableCagOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.SUCCESS, receivedData.getRes().getCode(), "Wrong code");
    }

}
