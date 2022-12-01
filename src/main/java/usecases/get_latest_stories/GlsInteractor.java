package usecases.get_latest_stories;

import usecases.StoryData;

import java.util.Arrays;

/**
 * Interactor for Get Latest Stories use-case
 */
public class GlsInteractor implements GlsInputBoundary{

    private final GlsOutputBoundary pres;
    private final GlsGateway repo;

    /**
     * Constructor for GlsInteractor
     * @param pres GlsOutputBoundary
     * @param repo GlsGateway used by this interactor
     */
    public GlsInteractor(GlsOutputBoundary pres, GlsGateway repo) {
        this.pres = pres;
        this.repo = repo;
    }

    @Override
    public void getLatestStories(GlsInputData data) { new Thread(new GlsThread(data)).start(); }

    /**
     * Thread for getting the latest stories
     */

    public class GlsThread implements Runnable {
        private final GlsInputData data;

        /**
         * Constructor for Get Latest Stories Thread
         * @param data GlsInputData
         */
        public GlsThread(GlsInputData data) { this.data = data; }

        @Override
        public void run() {
            GlsGatewayOutputData outputData = repo.getAllStories();
            StoryData[] stories = outputData.getStories();
            Arrays.sort(stories);

            if (data.getNumToGet() != null && data.getNumToGet() <= stories.length){

                StoryData[] stories2 = new StoryData[data.getNumToGet()];
                if (data.getNumToGet() >= 0) System.arraycopy(stories, 0, stories2, 0, data.getNumToGet());
                GlsOutputData outputData2 = new GlsOutputData(stories2);
                pres.putStories(outputData2);
            }
            else{
                GlsOutputData outputData1 = new GlsOutputData(stories);
                pres.putStories(outputData1);
            }
        }
    }
}
