package usecases.get_story_by_id;

import org.jetbrains.annotations.NotNull;
import usecases.*;

import java.util.ArrayList;
import java.util.List;

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
                pres.putStories(null, repoData.getRes());
            } else {
                StoryRepoData story = repoData.getRows().get(0);

                RepoRes<String> titleRepoData = titlesRepo.getMostUpvotedStoryTitle(storyId);

                List<FullStoryDTO> outList = new ArrayList<>();
                outList.add(new FullStoryDTO(
                        titleRepoData.isSuccess() ? titleRepoData.getRows().get(0) : null, story));

                pres.putStories(outList, Response.getSuccessful("Story retrieved successfully"));
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
