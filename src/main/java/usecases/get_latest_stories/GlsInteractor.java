package usecases.get_latest_stories;

import usecases.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Interactor for Get Latest Stories use-case
 */
public class GlsInteractor implements GlsInputBoundary{

    private final GlsGatewayStory repo;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    /**
     * Constructor for GlsInteractor
     * @param pres GlsOutputBoundary
     * @param repo GlsGateway used by this interactor
     */
    public GlsInteractor(GlsGatewayStory repo, ThreadRegister register) {
        this.repo = repo;
        this.register = register;
    }

    @Override
    public void getLatestStories(GlsInputData data, GlsOutputBoundary pres) {
        InterruptibleThread thread = new GlsThread(data, pres);
        if (!register.registerThread(thread)) {
            pres.outputShutdownServer();
        }
    }

    /**
     * Thread for getting the latest stories
     */
    public class GlsThread extends InterruptibleThread {
        private final GlsInputData data;
        private final GlsOutputBoundary pres;

        /**
         * Constructor for Get Latest Stories Thread
         * @param data GlsInputData
         */
        public GlsThread(GlsInputData data, GlsOutputBoundary pres) {
            super(GlsInteractor.this.register, pres);
            this.data = data;
            this.pres = pres;
        }

        @Override
        public void threadLogic() {
            RepoRes<StoryRepoData> res = repo.getAllStories();

            // DB Failed to get stories
            if (!res.isSuccess()) {
                pres.putStories(new GlsOutputData(null,
                        Response.getFailure("DB Failed to get stories")));
            }

            // DB Successfully retrieved stories
            else {
                StoryRepoData[] stories = res.getRows().toArray(new StoryRepoData[0]);

                Arrays.sort(stories);

                if (data.getNumToGet() != null && data.getNumToGet() <= stories.length){

                    StoryRepoData[] stories2 = new StoryRepoData[data.getNumToGet()];
                    if (data.getNumToGet() >= 0) System.arraycopy(stories, 0, stories2, 0, data.getNumToGet());
                    GlsOutputData outputData2 = new GlsOutputData(stories2,
                            Response.getSuccessful("Succesfully got stories"));
                    pres.putStories(outputData2);
                }
                else{
                    GlsOutputData outputData1 = new GlsOutputData(stories,
                            Response.getSuccessful("Successfully got stories"));
                    pres.putStories(outputData1);
                }
            }


        }
    }
}
