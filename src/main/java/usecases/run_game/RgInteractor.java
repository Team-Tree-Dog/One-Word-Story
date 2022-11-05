package usecases.run_game;

import entities.games.Game;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Interactor for the Run Game use-case
 */
public class RgInteractor {

    private final Game g;
    private final PgeOutputBoundary pge;
    private final PdInputBoundary pd;

    /**
     * @param g Game that we interact with
     * @param pge "Pull Game Ended" use-case output boundary
     * @param pd "Pull Data" use-case input boundary
     */
    public RgInteractor (Game g, PgeOutputBoundary pge, PdInputBoundary pd) {
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

                // Cancel the timer
                g.getGameTimer().cancel();

                // Perform "Game Ended" use-case
                pge.onGameEnded(new PgeInputData(g.getPlayers()));

                // Notify the game, after timer cancellation, when the
                // last execution of the run method has finished, meaning,
                // no more game timer code will run
                g.setTimerStopped();

            } else {
                // Regular procedure:

                //
                g.setSecondsLeftInCurrentTurn(g.getSecondsPerTurn() - 1);
                if (g.getSecondsLeftInCurrentTurn() == -1) { // We want to display 0 before, it is over
                    g.switchTurn();
                }
                g.onTimerUpdate();
                pd.onTimerUpdate(g);
            }
        }
    }

    public void startTimer () {
        this.g.getGameTimer().schedule(new RgTask(), 1000, 1000);
    }

}
