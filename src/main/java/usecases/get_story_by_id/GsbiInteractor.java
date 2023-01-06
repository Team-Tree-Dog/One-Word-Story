package usecases.get_story_by_id;

import usecases.InterruptibleThread;
import usecases.RepoRes;
import usecases.StoryRepoData;
import usecases.ThreadRegister;

public class GsbiInteractor implements GsbiInputBoundary {

    private final ThreadRegister register;
    private final GsbiGatewayStories storyRepo;
    private final GsbiGatewayTitles titlesRepo;

    public GsbiInteractor(GsbiGatewayStories storyRepo, GsbiGatewayTitles titleRepo, ThreadRegister register) {
        this.register = register;
        this.storyRepo = storyRepo;
        this.titlesRepo = titleRepo;
    }

    /**
     * Thread to run use case logic
     */
    public class GsbiThread extends InterruptibleThread {

        private final int storyId;
        private final GsbiOutputBoundary pres;

        public GsbiThread(int storyId, GsbiOutputBoundary pres) {
            super(GsbiInteractor.this.register, pres);
            this.storyId = storyId;
            this.pres = pres;
        }

        @Override
        protected void threadLogic() throws InterruptedException {
            RepoRes<StoryRepoData> repoData = storyRepo.getStoryById(storyId);

            if (!repoData.isSuccess()) {
                pres.putStories(null, ); //TODO: Pass repo fail code??
            }
        }
    }

    /**
     * Thin wrapper to start thread. Gets single story with specified id
     * @param storyId id of story to retrieve
     * @param pres output boundary where to send output
     */
    @Override
    public void getStory(int storyId, GsbiOutputBoundary pres) {
        InterruptibleThread gsbiThread = new GsbiThread(storyId, pres);
        if (!register.registerThread(gsbiThread)) {
            pres.outputShutdownServer();
        }
    }
}
