package usecases.get_latest_stories;

import usecases.*;

import java.util.Arrays;

/**
 * Interactor for Get Latest Stories use-case
 */
public class GlsInteractor implements GlsInputBoundary{

    private final GlsOutputBoundary pres;
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
    public GlsInteractor(GlsOutputBoundary pres, GlsGatewayStory repo, ThreadRegister register) {
        this.pres = pres;
        this.repo = repo;
        this.register = register;
    }

    @Override
    public void getLatestStories(GlsInputData data) {
        InterruptibleThread thread = new GlsThread(data);
        if (!register.registerThread(thread)) {
            pres.outputShutdownServer();
        }
    }

    /**
     * Thread for getting the latest stories
     */

    public class GlsThread extends InterruptibleThread {
        private final GlsInputData data;

        /**
         * Constructor for Get Latest Stories Thread
         * @param data GlsInputData
         */
        public GlsThread(GlsInputData data) {
            super(GlsInteractor.this.register, GlsInteractor.this.pres);
            this.data = data;
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
