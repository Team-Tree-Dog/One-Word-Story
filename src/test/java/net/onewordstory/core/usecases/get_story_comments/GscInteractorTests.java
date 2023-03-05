package net.onewordstory.core.usecases.get_story_comments;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import net.onewordstory.core.usecases.CommentRepoData;
import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.ThreadRegister;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class GscInteractorTests {
    private static final ThreadRegister register = new ThreadRegister();
    private static final CommentRepoData duplicateCRD = new CommentRepoData(1, 1, "Julius", "Wow, just wow");
    private static final List<CommentRepoData> duplicateRow = new ArrayList<CommentRepoData>(List.of(duplicateCRD));
    GscOutputBoundary pres;
    GscGatewayComments repo;

    /**
     * Customizable class to imitate GscPresenter during testing
     */
    static class CustomizableGscOutputBoundary implements GscOutputBoundary {

        private GscOutputData receivedData;

        public CustomizableGscOutputBoundary() {
            this.receivedData = null;
        }

        public void putStoryComments(GscOutputData d) {
            this.receivedData = d;
        }

        public GscOutputData getReceivedData() {
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
    static class CustomizableGscGatewayComments implements GscGatewayComments {

        private final boolean getAllCommentsSuccess;

        /**
         * Constructor of our customizable gateway
         * @param getAllCommentsSuccess determines return value for getAllComments
         */
        public CustomizableGscGatewayComments(boolean getAllCommentsSuccess) {
            this.getAllCommentsSuccess = getAllCommentsSuccess;
        }

        /**
         * Method imitating repository's getAllComments with custom element based on
         * getAllCommentsSuccess parameter.
         * @param storyId requested storyId
         * @return "success" and predetermined output when commentAsGuestSuccess is true,
         * and "failure" with null otherwise
         */
        public @NotNull RepoRes<CommentRepoData> getAllComments(int storyId) {
            Response res = Response.getFailure("Customizable StGateway would reject anything");
            List<CommentRepoData> rows = null;

            if (getAllCommentsSuccess) {
                res = Response.getSuccessful("Customizable StGateway would accept anything");
                rows = duplicateRow;
            }

            return new RepoRes<CommentRepoData>(res, rows);
        }

    }

    @BeforeEach
    public void setup () {

    }

    @AfterEach
    public void teardown () {

    }

    /**
     * Testing when repo fails and returns failure plus null.
     * We expect to receive FAIL code
     */
    @Test
    @Timeout(1000)
    public void testRepoFails() {

        // Instantiating interactor
        repo = new CustomizableGscGatewayComments(false);
        pres = new CustomizableGscOutputBoundary();
        GscInteractor gsc = new GscInteractor(repo, register);

        // Running thread
        GscInputData d = new GscInputData(1);
        GscInteractor.GscThread innerThreadInstance = gsc.new GscThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        GscOutputData receivedData = ((CustomizableGscOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.FAIL, receivedData.getRes().getCode(), "Wrong code");
    }


    /**
     * Testing when repo succeeds and returns success plus predetermined comments.
     * We expect to receive SUCCESS code and correct list of comments
     */
    @Test
    @Timeout(1000)
    public void testRepoSucceeds() {

        // Instantiating interactor
        repo = new CustomizableGscGatewayComments(true);
        pres = new CustomizableGscOutputBoundary();
        GscInteractor gsc = new GscInteractor(repo, register);

        // Running thread
        GscInputData d = new GscInputData(1);
        GscInteractor.GscThread innerThreadInstance = gsc.new GscThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        GscOutputData receivedData = ((CustomizableGscOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.SUCCESS, receivedData.getRes().getCode(), "Wrong code");
        assertEquals(1, receivedData.getComments().size());
        assertEquals("Wow, just wow", receivedData.getComments().get(0).getContent(), "Wrong comment");
    }

}
