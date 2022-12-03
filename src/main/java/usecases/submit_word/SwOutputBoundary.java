package usecases.submit_word;

import usecases.shutdown_server.SsOutputBoundary;

/**
 * Methods which will be called by the presenter that become the changes in the GUI.
 */
public interface SwOutputBoundary extends SsOutputBoundary {

    /**
     * Method called if the player is in the game and the word is determined to be valid.
     * @param outputDataValidWord the wrapped output data.
     */
    void valid(SwOutputDataValidWord outputDataValidWord);

    /**
     * Method called if either the player is not in the game or the word is not determined to be valid.
     * @param outputDataFailure the wrapped output data.
     */
    void invalid(SwOutputDataFailure outputDataFailure);
}
