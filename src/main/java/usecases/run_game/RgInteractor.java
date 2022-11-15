package usecases.run_game;

import entities.Player;
import entities.games.Game;
import usecases.pull_data.PdInputBoundary;
import usecases.pull_data.PdInputData;
import usecases.pull_game_ended.PgeInputBoundary;
import usecases.pull_game_ended.PgeInputData;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Interactor for the Run Game use-case
 */
public class RgInteractor {

    private final Game g;
    private final PgeInputBoundary pge;
    private final PdInputBoundary pd;

    /**
     * @param g Game that we interact with
     * @param pge "Pull Game Ended" use-case input boundary
     * @param pd "Pull Data" use-case input boundary
     */
    public RgInteractor (Game g, PgeInputBoundary pge, PdInputBoundary pd) {
        this.g = g;
        this.pge = pge;
        this.pd = pd;
    }

    /**
     * Internal Timer Task to be used within the main Timer below
     */
    public class RgTask extends TimerTask {

        /**
         * Method to override from abstract TimerTask
         */
        @Override
        public void run () {

            if (RgInteractor.this.g.isGameOver()) {
                // Game ending procedure:

                // Cancel the game timer
                RgInteractor.this.g.getGameTimer().cancel();

                // Perform "Game Ended" use-case via PgeInteractor
                RgInteractor.this.pge.onGameEnded(new PgeInputData(new ArrayList<>(RgInteractor.this.g.getPlayers())));

                // Notify the game, after timer cancellation, when the
                // last execution of the run method has finished, meaning,
                // no more game timer code will run
                RgInteractor.this.g.setTimerStopped();

            } else {
                // Regular game procedure:

                // Decrement seconds counter and switch turn if needed
                RgInteractor.this.g.setSecondsLeftInCurrentTurn(RgInteractor.this.g.getSecondsLeftInCurrentTurn() - 1);
                if (RgInteractor.this.g.getSecondsLeftInCurrentTurn() == 0) { // Displaying 0 before, it is over
                    RgInteractor.this.g.switchTurn();
                }

                // Push corresponding updates to our game and PdInteractor
                RgInteractor.this.g.onTimerUpdate(); // note: was empty at the time of implementation
                RgInteractor.this.pd.onTimerUpdate(new PdInputData(RgInteractor.this.g));

            }
        }
    }

    /**
     * Launch timer with the above-provided RgTask
     */
    public void startTimer () {
        this.g.setSecondsLeftInCurrentTurn(g.getSecondsPerTurn());
        this.g.getGameTimer().schedule(new RgTask(), 1000, 1000);
    }

}
