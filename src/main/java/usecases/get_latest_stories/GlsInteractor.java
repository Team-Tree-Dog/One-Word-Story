package usecases.get_latest_stories;

import usecases.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interactor for Get Latest Stories use-case
 */
public class GlsInteractor implements GlsInputBoundary{

    private final GlsGatewayStory storyRepo;
    private final GlsGatewayTitles titlesRepo;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    /**
     * Constructor for GlsInteractor
     * @param storyRepo GlsGateway used by this interactor
     */
    public GlsInteractor(GlsGatewayStory storyRepo, GlsGatewayTitles titlesRepo, ThreadRegister register) {
        this.storyRepo = storyRepo;
        this.titlesRepo = titlesRepo;
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
         * @param pres Output boundary for use case
         */
        public GlsThread(GlsInputData data, GlsOutputBoundary pres) {
            super(GlsInteractor.this.register, pres);
            this.data = data;
            this.pres = pres;
        }

        @Override
        public void threadLogic() {
            RepoRes<StoryRepoData> res = storyRepo.getAllStories();

            // DB Failed to get stories
            if (!res.isSuccess()) {
                pres.putStories(null,
                        res.getRes());
            }

            // DB Successfully retrieved stories
            else {
                // toArray can't produce a null pointer if res code is success
                StoryRepoData[] stories = res.getRows().toArray(new StoryRepoData[0]);

                Arrays.sort(stories);

                // If numToGet is set, then trim the stories list
                if (data.getNumToGet() != null && data.getNumToGet() <= stories.length){

                    StoryRepoData[] stories2 = new StoryRepoData[data.getNumToGet()];

                    if (data.getNumToGet() >= 0) System.arraycopy(stories, 0, stories2, 0, data.getNumToGet());

                    stories = stories2;
                }

                // Get titles for each story and convert to FullStoryDTO
                List<FullStoryDTO> out = new ArrayList<>();
                for (StoryRepoData storyData : stories) {
                    RepoRes<String> titleData = titlesRepo.getMostUpvotedStoryTitle(storyData.getStoryId());
                    if (titleData.isSuccess()) {
                        out.add(new FullStoryDTO(titleData.getRows().get(0), storyData));
                    } else {
                        out.add(new FullStoryDTO(null, storyData));
                    }
                }

                pres.putStories(out, Response.getSuccessful("Successfully got stories"));
            }
        }
    }
}
