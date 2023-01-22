package net.onewordstory.core.usecases.pull_game_ended;

/**
 * Input boundary for PgeInteractor
 */
public interface PgeInputBoundary {

    /**
     * Performs tasks necessary when a game has ended
     * @param data contains data from the ended game
     */
    void onGameEnded(PgeInputData data);
}
