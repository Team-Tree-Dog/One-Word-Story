package usecases.pull_game_ended;

/**
 * Output boundary for PgeInteractor
 */
public interface PgeOutputBoundary {

    /**
     * Notifies players (by player id) that the game has ended, so they can disconnect
     * @param data contains data to pass to this output boundary (relevant player ids)
     */
    void notifyGameEnded(PgeOutputData data);
}
