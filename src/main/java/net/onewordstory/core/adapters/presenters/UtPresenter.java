package net.onewordstory.core.adapters.presenters;

import net.onewordstory.core.adapters.view_models.UtViewModel;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.upvote_title.UtOutputBoundary;
import net.onewordstory.core.usecases.upvote_title.UtOutputData;

import static net.onewordstory.core.usecases.Response.ResCode.SHUTTING_DOWN;

public class UtPresenter implements UtOutputBoundary {
    private final UtViewModel viewM;

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
        viewM.getResponseAwaitable().set(data.getRes());
    }

    @Override
    public void outputShutdownServer(){
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
