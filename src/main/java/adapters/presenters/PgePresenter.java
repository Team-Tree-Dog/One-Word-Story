package adapters.presenters;

import adapters.view_models.PgeViewModel;
import usecases.pull_game_ended.PgeOutputBoundary;
import usecases.pull_game_ended.PgeOutputData;

public class PgePresenter implements PgeOutputBoundary {

    private final PgeViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public PgePresenter (PgeViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model that the current game has ended along with a list
     * of player IDs who were in that game and now have been removed since the game
     * ended. Also notify of any end-of-game data to display
     * @param data contains data to pass to this output boundary (relevant player ids)
     */
    @Override
    public void notifyGameEnded(PgeOutputData data) {

    }
}
