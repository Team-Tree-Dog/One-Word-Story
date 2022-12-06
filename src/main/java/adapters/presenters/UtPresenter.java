package adapters.presenters;

import adapters.ViewModel;
import usecases.upvote_title.UtOutputBoundary;
import usecases.upvote_title.UtOutputData;

public class UtPresenter implements UtOutputBoundary {
    private ViewModel viewM;

    public UtPresenter(ViewModel viewM) {this.viewM = viewM;}

    public void upvoteOutput(UtOutputData data){

    }

    public void outputShutdownServer(){

    }
}
