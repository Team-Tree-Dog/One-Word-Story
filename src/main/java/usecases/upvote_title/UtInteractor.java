package usecases.upvote_title;

import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;
import usecases.shutdown_server.SsOutputBoundary;

public class UtInteractor implements UtInputBoundary{
    private UtOutputBoundary pres;
    private UtGatewayTitles repo;
    private ThreadRegister register;

    public UtInteractor(UtOutputBoundary pres, UtGatewayTitles repo, ThreadRegister register) {
        this.pres = pres;
        this.repo = repo;
        this.register = register;
    }

    public class UtThread extends InterruptibleThread{
        private UtInputData data;

        public UtThread(UtInputData data) {
            super(UtInteractor.this.register, UtInteractor.this.pres);
            this.data = data;
        }

        public void threadLogic() {
            int storyId = data.getStoryId();
            String titleToUpvote = data.getTitleToUpvote();
            Response res = repo.upvoteTitle(storyId, titleToUpvote);
            UtOutputData outputData = new UtOutputData(data.getRequestId(), res);
            pres.upvoteOutput(outputData);
        }
    }

    public void upvoteTitle(UtInputData data){
        InterruptibleThread thread = new UtThread(data);
        boolean success = register.registerThread(thread);
        if (!success){pres.outputShutdownServer();}
    }
}
