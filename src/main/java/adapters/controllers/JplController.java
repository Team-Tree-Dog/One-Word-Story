package adapters.controllers;

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
     */
    public void joinPublicLobby (String playerId, String displayName) {
        jpl.joinPublicLobby(
                new JplInputData(displayName, playerId)
        );
    }
}
