package net.onewordstory.core.usecases.suggest_title;

import net.onewordstory.core.entities.suggested_title_checkers.SuggestedTitleChecker;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.ThreadRegister;
import net.onewordstory.core.usecases.TitleRepoData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StInteractorTests {
    private static final ThreadRegister register = new ThreadRegister();
    private static final TitleRepoData duplicateTRD = new TitleRepoData(1, 1, "Duplicate", 1);
    private static final List<TitleRepoData> duplicateRow = new ArrayList<TitleRepoData>(List.of(duplicateTRD));

    StOutputBoundary pres;
    StGatewayTitles repo;
    SuggestedTitleChecker titleChecker;

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
     * Customizable class to imitate repository with stories during testing.
     * Records received data to be compared with applied requests
     */
    static class CustomizableStGateway implements StGatewayTitles {

        private final boolean suggestTitleSuccess;
        private final boolean getAllTitlesReturnNull;

        /**
         * Constructor of our customizable gateway
         * @param getAllTitlesReturnNull detirmines return value for getAllTitles
         */
        public CustomizableStGateway(boolean suggestTitleSuccess, boolean getAllTitlesReturnNull) {
            this.suggestTitleSuccess = suggestTitleSuccess;
            this.getAllTitlesReturnNull = getAllTitlesReturnNull;
        }

        /**
         * Method imitating repository's suggestTitle with custom element based on
         * suggestTitleSuccess parameter.
         * @param storyId requested storyId
         * @param titleSuggestion requested title
         * @return "success" when suggestTitleSuccess is true, and "failure" otherwise
         */
        public @NotNull Response suggestTitle(int storyId, String titleSuggestion) {
            if (this.suggestTitleSuccess) {
                return Response.getSuccessful("Customizable StGateway would accept anything");
            } else {
                return Response.getFailure("Customizable StGateway would reject anything");
            }
        }

        /**
         * Method imitating repository's getAllTitles with custom element based on
         * getAllTitlesReturnNull parameter.
         * @param storyId requested storyId
         * @return null when getAllTitlesReturnNull is true, and "duplicate" row otherwise
         */
        public @NotNull RepoRes<TitleRepoData> getAllTitles(int storyId) {

            Response res = Response.getFailure("Customizable StGateway would reject anything");
            List<TitleRepoData> rows = null;

            if (!getAllTitlesReturnNull) {
                res = Response.getSuccessful("Customizable StGateway would accept anything");
                rows = duplicateRow;
            }

            return new RepoRes<TitleRepoData>(res, rows);
        }

    }

    /**
     * Customizable class to imitate title checker during testing.
     * Always returns true
     */
    static class CustomizableTitleChecker implements SuggestedTitleChecker {

        private String receivedData;
        private final boolean returnValue;
        public CustomizableTitleChecker(boolean returnValue) {
            this.receivedData = null;
            this.returnValue = returnValue;
        }

        @Override
        public boolean checkValid(String title) {
            receivedData = title;
            return this.returnValue;
        }

        public String getReceivedData() {
            return receivedData;
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
     * Testing when repo returns null.
     * We expect to receive FAIL code
     */
    @Test
    @Timeout(1000)
    public void testRepoReturnsNull() {

        // Instantiating interactor
        repo = new CustomizableStGateway(true, true);
        pres = new CustomizableStOutputBoundary();
        titleChecker = new CustomizableTitleChecker(true);
        StInteractor st = new StInteractor(repo, titleChecker, register);

        // Running thread
        StInputData d = new StInputData("Non-Duplicate", 1);
        StInteractor.StThread innerThreadInstance = st.new StThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        StOutputData receivedData = ((CustomizableStOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.FAIL, receivedData.getRes().getCode(), "Wrong code");
    }

    /**
     * Testing when titleChecker returns false.
     * We expect to receive INVALID_TITLE code
     */
    @Test
    @Timeout(1000)
    public void testTitleCheckerReturnsFalse() {

        // Instantiating interactor
        repo = new CustomizableStGateway(true, false);
        pres = new CustomizableStOutputBoundary();
        titleChecker = new CustomizableTitleChecker(false);
        StInteractor st = new StInteractor(repo, titleChecker, register);

        // Running thread
        StInputData d = new StInputData("Non-Duplicate", 1);
        StInteractor.StThread innerThreadInstance = st.new StThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        StOutputData receivedData = ((CustomizableStOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.INVALID_TITLE, receivedData.getRes().getCode(), "Wrong code");
    }

    /**
     * Testing when title preprocessing.
     * We expect title to see trimming and removal of double spaces
     */
    @Test
    @Timeout(1000)
    public void testTitlePreprocessing() {

        // Instantiating interactor
        repo = new CustomizableStGateway(true, false);
        pres = new CustomizableStOutputBoundary();
        titleChecker = new CustomizableTitleChecker(true);
        StInteractor st = new StInteractor(repo, titleChecker, register);

        // Running thread
        StInputData d = new StInputData(" d    d ", 1);
        StInteractor.StThread innerThreadInstance = st.new StThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        String receivedData = ((CustomizableTitleChecker) titleChecker).getReceivedData();
        assertNotNull(receivedData, "Title checker was not accessed");
        assertEquals("d d", receivedData, "Incorrect preprocessing");
    }


    /**
     * Testing when title already exists.
     * We expect to receive INVALID_TITLE code
     */
    @Test
    @Timeout(1000)
    public void testTitleAlreadyExists() {

        // Instantiating interactor
        repo = new CustomizableStGateway(true, false);
        pres = new CustomizableStOutputBoundary();
        titleChecker = new CustomizableTitleChecker(true);
        StInteractor st = new StInteractor(repo, titleChecker, register);

        // Running thread
        StInputData d = new StInputData("Duplicate", 1);
        StInteractor.StThread innerThreadInstance = st.new StThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        StOutputData receivedData = ((CustomizableStOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.TITLE_ALREADY_SUGGESTED, receivedData.getRes().getCode(), "Wrong code");
    }

    /**
     * Testing when failing to add a valid, new title.
     * Expect to see FAIL code
     */
    @Test
    @Timeout(1000)
    public void testSuggestTitleFails() {

        // Instantiating interactor
        repo = new CustomizableStGateway(false, false);
        pres = new CustomizableStOutputBoundary();
        titleChecker = new CustomizableTitleChecker(true);
        StInteractor st = new StInteractor(repo, titleChecker, register);

        // Running thread
        StInputData d = new StInputData("Non-Duplicate", 1);
        StInteractor.StThread innerThreadInstance = st.new StThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        StOutputData receivedData = ((CustomizableStOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.FAIL, receivedData.getRes().getCode(), "Wrong code");
    }

    /**
     * Testing when a valid, new title is being successfully added.
     * Expect to see SUCCESS code and presenter receives correct data
     */
    @Test
    @Timeout(1000)
    public void testSuggestTitleSucceeds() {

        // Instantiating interactor
        repo = new CustomizableStGateway(true, false);
        pres = new CustomizableStOutputBoundary();
        titleChecker = new CustomizableTitleChecker(true);
        StInteractor st = new StInteractor(repo, titleChecker, register);

        // Running thread
        StInputData d = new StInputData("Non-Duplicate", 1);
        StInteractor.StThread innerThreadInstance = st.new StThread(d, pres);
        innerThreadInstance.run();

        // Verifying results for presenter
        StOutputData receivedData = ((CustomizableStOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.SUCCESS, receivedData.getRes().getCode(), "Wrong code");
    }

}

