package adapters.controllers;

import adapters.presenters.JplPresenter;
import adapters.view_models.JplViewModel;
import org.example.ANSI;
import org.example.Log;
import usecases.join_public_lobby.JplInputBoundary;
import usecases.join_public_lobby.JplInputData;

public class JplController {
    private final JplInputBoundary jpl;

    /**
     * Take in and set an instance of the input boundary that
     * is intended to be called by users from the view
     */
    public JplController (JplInputBoundary jpl) {
        this.jpl = jpl;
    }

    /**
     * Provide a unique ID and a display name for a player who wishes to join a public lobby
     * @param playerId Unique ID of player never previously used
     * @param displayName Desired display name, may be duplicated
     * @return View model for this use case
     */
    public JplViewModel joinPublicLobby (String playerId, String displayName) {
        Log.sendMessage(ANSI.BLUE, "JPL", ANSI.LIGHT_BLUE,
                "Controller called with ID " + playerId + " NAME " + displayName);
        JplViewModel viewM = new JplViewModel();
        JplPresenter pres = new JplPresenter(viewM);
        jpl.joinPublicLobby(new JplInputData(displayName, playerId), pres);
        return viewM;
    }
}
