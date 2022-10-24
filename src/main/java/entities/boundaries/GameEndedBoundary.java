package entities.boundaries;

/**
 * Inject an implementing  class (which resides in use case layer) to get called when game ends.
 * (Potentially passing statistics in the future to be saved to repo)
 *
 */
public interface GameEndedBoundary {

    /**
     * @param d the Game data obtained at the end of the Game.
     */
    void onGameEnded(GameEndedData d);
}
