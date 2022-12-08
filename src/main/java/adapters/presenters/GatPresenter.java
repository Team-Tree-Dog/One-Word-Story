package adapters.presenters;

import adapters.view_models.GatViewModel;
import usecases.get_all_titles.GatOutputBoundary;
import usecases.get_all_titles.GatOutputData;

public class GatPresenter implements GatOutputBoundary {
    private GatViewModel viewM;

    public GatPresenter(GatViewModel viewM) {this.viewM = viewM;}

    @Override
    public void putSuggestedTitles(GatOutputData data){

    }

    @Override
    public void outputShutdownServer(){

    }
}
