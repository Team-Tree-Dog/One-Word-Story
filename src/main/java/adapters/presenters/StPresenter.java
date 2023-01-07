package adapters.presenters;

import adapters.view_models.StViewModel;
import usecases.Response;
import usecases.suggest_title.StOutputBoundary;
import usecases.suggest_title.StOutputData;

import static usecases.Response.ResCode.SHUTTING_DOWN;

/**
 * The presenter for this use case. Updates the view model once the processes for these use case are completed.
 */
public class StPresenter implements StOutputBoundary {
    private final StViewModel viewM;

    /**
     * The constructor for this presenter. Takes in the ViewModel and sets it as an attribute for this use case.
     * @param viewM the view model for this use case.
     */
    public StPresenter(StViewModel viewM) {
        this.viewM = viewM;
    }

    /**
     * Update the view model with the success or failure of suggesting the title for a particular story
     * @param data  the output data that contains the response corresponding to the success of
     */
    @Override
    public void suggestTitleOutput(StOutputData data) {
        viewM.getResponseAwaitable().set(data.getRes());
    }

    @Override
    public void outputShutdownServer() {
        viewM.getResponseAwaitable().set(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
