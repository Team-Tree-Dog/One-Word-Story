package net.onewordstory.core.adapters.presenters;

import net.onewordstory.core.adapters.display_data.not_ended_display_data.GameDisplayData;
import net.onewordstory.core.adapters.view_models.PdViewModel;
import net.onewordstory.core.usecases.pull_data.PdOutputBoundary;
import net.onewordstory.core.usecases.pull_data.PdOutputData;

public class PdPresenter implements PdOutputBoundary {

    private final PdViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public PdPresenter (PdViewModel viewM) { this.viewM = viewM; }

    /**
     * Update the view model's state of the current game
     * @param d PdOutputData
     */
    @Override
    public void updateGameInfo(PdOutputData d) {
        // Sets state in view model
        viewM.setCurrentGameState(GameDisplayData.fromGameDTO(d.getGameInfo()));
    }
}
