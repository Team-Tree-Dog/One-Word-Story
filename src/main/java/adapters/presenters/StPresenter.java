package adapters.presenters;

import adapters.ViewModel;
import usecases.suggest_title.*;

/**
 * The presenter for this use case. Updates the view model once the processes for these use case are completed.
 */
public class StPresenter implements StOutputBoundary{
    private final ViewModel viewM;

    /**
     * The constructor for this presenter. Takes in the ViewModel and sets it as an attribute for this use case.
     * @param viewM the view model for this use case.
     */
    public StPresenter(ViewModel viewM) {
        this.viewM = viewM;
    }

    /**
     * Update the view model with the success or failure of suggesting the title for a particular story
     * @param data  the output data that contains the response corresponding to the success of
     */
    public void suggestTitleOutput(StOutputData data){}
}
