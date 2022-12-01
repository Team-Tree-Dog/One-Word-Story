package adapters.controllers;

import usecases.submit_word.SwInputBoundary;
import usecases.submit_word.SwInputData;

public class SwController {

    private final SwInputBoundary sw;

    /**
     * Take in and set an instance of the input boundary that
     * is intended to be called by users from the view
     */
    public SwController (SwInputBoundary sw) {
        this.sw = sw;
    }

    /**
     * Called for a player who wishes to submit a word during their turn, or out
     * of turn which will result in an error
     * @param playerId Unique ID of player never previously used
     * @param word Word that the player is submitting
     */
    public void submitWord (String playerId, String word) {
        sw.submitWord(
                new SwInputData(word, playerId)
        );
    }
}
