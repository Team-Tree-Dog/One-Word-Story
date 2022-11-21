package adapters.presenters;

import adapters.ViewModel;
import usecases.pull_data.PdOutputBoundary;
import usecases.pull_data.PdOutputData;

public class PdPresenter implements PdOutputBoundary {

    private final ViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public PdPresenter (ViewModel viewM) { this.viewM = viewM; }

    /**
     * Update the view model's state of the current game
     * @param d PdOutputData
     */
    @Override
    public void updateGameInfo(PdOutputData d) {

    }
}
