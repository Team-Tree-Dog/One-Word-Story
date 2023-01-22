package net.onewordstory.core.usecases.get_all_titles;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class GatInteractorTests {
    private static final ThreadRegister register = new ThreadRegister();
    GatOutputBoundary pres;
    GatGatewayTitles repo;

    /**
     * Customizable class to imitate GatPresenter during testing
     */
    static class CustomizableGatOutputBoundary implements GatOutputBoundary {

        private GatOutputData receivedData;

        public CustomizableGatOutputBoundary() {
            this.receivedData = null;
        }

        public void putSuggestedTitles(GatOutputData d) {
            this.receivedData = d;
        }

        public GatOutputData getReceivedData() {
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
    static class CustomizableGatGatewayTitles implements GatGatewayTitles {

        private final boolean getAllTitlesSuccess;

        /**
         * Constructor of our customizable gateway
         * @param getAllTitlesSuccess determines return value for getAllTitles
         */
        public CustomizableGatGatewayTitles(boolean getAllTitlesSuccess) {
            this.getAllTitlesSuccess = getAllTitlesSuccess;
        }

        /**
         * Method imitating repository's getAllTitles with custom element based on
         * getAllTitlesSuccess parameter.
         * @param storyId requested storyId
         * @return "success" and predetermined output when getAllTitlesSuccess is true,
         * and "failure" with null otherwise
         */
        public @NotNull RepoRes<TitleRepoData> getAllTitles(int storyId) {
            Response res = Response.getFailure("Customizable StGateway would reject anything");
            List<TitleRepoData> rows = null;

            if (getAllTitlesSuccess) {
                res = Response.getSuccessful("Customizable StGateway would accept anything");
                TitleRepoData trd1 = new TitleRepoData(1, 1, "title1", 1);
                TitleRepoData trd2 = new TitleRepoData(2, 1, "title2", 1);
                rows = new ArrayList<TitleRepoData>(List.of(trd1, trd2));
            }

            return new RepoRes<TitleRepoData>(res, rows);
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
        repo = new CustomizableGatGatewayTitles(false);
        pres = new CustomizableGatOutputBoundary();
        GatInteractor gat = new GatInteractor(repo, register);

        // Running thread
        GatInputData d = new GatInputData(1);
        GatInteractor.GatThread innerThreadInstance = gat.new GatThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        GatOutputData receivedData = ((CustomizableGatOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.FAIL, receivedData.getRes().getCode(), "Wrong code");
    }


    /**
     * Testing when repo succeeds and returns correct titles.
     * We expect to receive SUCCESS code and valid titles
     */
    @Test
    @Timeout(1000)
    public void testRepoSucceeds() {

        // Instantiating interactor
        repo = new CustomizableGatGatewayTitles(true);
        pres = new CustomizableGatOutputBoundary();
        GatInteractor gat = new GatInteractor(repo, register);

        // Running thread
        GatInputData d = new GatInputData(1);
        GatInteractor.GatThread innerThreadInstance = gat.new GatThread(d, pres);
        innerThreadInstance.run();

        // Verifying results
        GatOutputData receivedData = ((CustomizableGatOutputBoundary) pres).getReceivedData();
        assertNotNull(receivedData, "Presenter was not accessed");
        assertEquals(Response.ResCode.SUCCESS, receivedData.getRes().getCode(), "Wrong code");

        List<String> titlesList = new ArrayList<>();
        for (TitleRepoData trd : receivedData.getSuggestedTitles()) {
            titlesList.add(trd.getTitle());
        }
        assertEquals(2, titlesList.size());
        assertTrue(titlesList.contains("title1"));
        assertTrue(titlesList.contains("title2"));
    }

}
