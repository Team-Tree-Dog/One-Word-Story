package adapters.presenters;

import adapters.view_models.UtViewModel;
import usecases.upvote_title.UtOutputBoundary;
import usecases.upvote_title.UtOutputData;

public class UtPresenter implements UtOutputBoundary {
    private UtViewModel viewM;

    /**
     * Constructor for the upvote title use case presenter. Takes in and sets the view model for this use case.
     * @param viewM instance of the view model for this use case.
     */
    public UtPresenter(UtViewModel viewM) {this.viewM = viewM;}

    /**
     * Notify the view model of the success of upvoting a title
     * @param data  the output data for this use case that contains the response recording the success of upvoting
     *              a title
     */
    @Override
    public void upvoteOutput(UtOutputData data){

    }

    @Override
    public void outputShutdownServer(){

    }
}
