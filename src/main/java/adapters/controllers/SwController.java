package adapters.controllers;

import adapters.presenters.SwPresenter;
import adapters.view_models.SwViewModel;
import org.example.ANSI;
import org.example.Log;
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
     * @return View model for this use case
     */
    public SwViewModel submitWord (String playerId, String word) {
        Log.sendMessage(ANSI.BLUE, "SW", ANSI.LIGHT_BLUE,
                "Controller called with ID " + playerId + " WORD " + word);
        SwViewModel viewM = new SwViewModel();
        SwPresenter presenter = new SwPresenter(viewM);
        sw.submitWord(new SwInputData(word, playerId), presenter);
        return viewM;
    }
}
