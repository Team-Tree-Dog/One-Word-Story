package usecases.get_all_titles;

import usecases.InterruptibleThread;
import usecases.RepoRes;
import usecases.ThreadRegister;
import usecases.TitleRepoData;
import usecases.shutdown_server.SsOutputBoundary;

public class GatInteractor {
    private GatOutputBoundary pres;
    private GatGatewayTitles repo;
    private ThreadRegister register;

    public GatInteractor(GatOutputBoundary pres, GatGatewayTitles repo, ThreadRegister register) {
        this.pres = pres;
        this.repo = repo;
        this.register = register;
    }

    public class GatThread extends InterruptibleThread{
        private GatInputData data;

        public GatThread(GatInputData data) {
            super(GatInteractor.this.register, (SsOutputBoundary) GatInteractor.this.pres);
            this.data = data;
        }

        public void threadLogic(){
            int storyId = data.getStoryId();
            RepoRes<TitleRepoData> suggestedTitles = repo.getAllTitles(storyId);
            GatOutputData gatOutputData = new GatOutputData(suggestedTitles.getRows(), suggestedTitles.getRes());
            pres.putSuggestedTitles(gatOutputData);
        }
    }
}
