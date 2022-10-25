package entities.boundaries;

import entities.games.Game;

/**
 * Inject an implementing  class (which resides in use case layer)
 * to get called repeatedly when the game timer executes.
 */
public interface OnTimerUpdateBoundary {

    /**
     *
     * @param game Game that we are changing turn timer for.
     */
    void onTimerUpdate(Game game);
}
