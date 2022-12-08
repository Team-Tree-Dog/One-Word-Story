package adapters.presenters;

import adapters.view_models.GatViewModel;
import usecases.Response;
import usecases.get_all_titles.GatOutputBoundary;
import usecases.get_all_titles.GatOutputData;

import java.util.List;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class GatPresenter implements GatOutputBoundary {
    private GatViewModel viewM;

    public GatPresenter(GatViewModel viewM) {this.viewM = viewM;}

    @Override
    public void putSuggestedTitles(GatOutputData data){
        if (data.getSuggestedTitles() == null) {
            viewM.setResponse(data.getRes());
        } else {
            viewM.setSuggestedTitles(data.getSuggestedTitles());
            viewM.setResponse(data.getRes());
        }
    }

    @Override
    public void outputShutdownServer(){
        viewM.setResponse(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
