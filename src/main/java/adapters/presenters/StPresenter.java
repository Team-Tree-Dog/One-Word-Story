package adapters.presenters;

import adapters.ViewModel;
import usecases.suggest_title.*;

public class StPresenter implements StOutputBoundary{
    ViewModel viewM;

    /**
     * Update the view model with the success or failure of suggesting the title for a particular story
     * @param data  the output data that contains the response corresponding to the success of
     */
    public void suggestTitleOutput(StOutputData data){}
}
