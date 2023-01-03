package adapters.presenters;

import adapters.view_models.GatViewModel;
import usecases.Response;
import usecases.get_all_titles.GatOutputBoundary;
import usecases.get_all_titles.GatOutputData;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class GatPresenter implements GatOutputBoundary {
    private final GatViewModel viewM;

    public GatPresenter(GatViewModel viewM) {this.viewM = viewM;}

    @Override
    public void putSuggestedTitles(GatOutputData data){
        if (data.getSuggestedTitles() == null) {
            viewM.getResponseAwaitable().set(data.getRes());
        } else {
            viewM.getSuggestedTitlesAwaitable().set(data.getSuggestedTitles());
            viewM.getResponseAwaitable().set(data.getRes());
        }
    }

    @Override
    public void outputShutdownServer(){
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
