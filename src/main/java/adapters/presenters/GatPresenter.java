package adapters.presenters;

import adapters.ViewModel;
import usecases.get_all_titles.GatOutputBoundary;
import usecases.get_all_titles.GatOutputData;

public class GatPresenter implements GatOutputBoundary {
    private ViewModel viewM;

    public GatPresenter(ViewModel viewM) {this.viewM = viewM;}

    public void putSuggestedTitles(GatOutputData data){

    }

    @Override
    public void outputShutdownServer(){

    }
}
