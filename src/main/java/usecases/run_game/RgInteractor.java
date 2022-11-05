package usecases.run_game;

import entities.games.Game;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Interactor for the Run Game use-case
 */
public class RgInteractor {

    private final Game g;
    private final JplOutputBoundary p;
    private final PgeOutputBoundary pge;
    private final PdInputBoundary pd;

    /**
     * @param g Game that we interact with
     * @param p Join Public Lobby use-case output boundary
     * @param pge Pull Game Ended use-case output boundary
     * @param pd Pull Data use-case input boundary
     */
    public RgInteractor (Game g, JplOutputBoundary p, PgeOutputBoundary pge, PdInputBoundary pd) {
        this.g = g;
        this.p = p;
        this.pge = pge;
        this.pd = pd;
    }

    public static class RgTask extends TimerTask {

        private final Game game;
        public RgTask (Game game) { // TODO: check with Alex
            this.game = game;
        }

        @Override
        public void run () {
            if (game.isGameOver()) {
                game.getGameTimer().cancel();
                pge.onGameEnded(new PgeInputData(game.getPlayers()));
                game.setTimerStopped();
            } else {
                game.setSecondsLeftInCurrentTurn(game.getSecondsPerTurn() - 1);  // TODO: implement this in Game class
                if (game.getSecondsLeftInCurrentTurn() == -1) {
                    game.switchTurn();
                }
                game.onTimerUpdate();
                pd.onTimerUpdate(game);
            }
        }
    }

    public void startTimer () {
        this.g.getGameTimer().schedule(new RgTask(this.g), 1000, 1000);
    }

}
