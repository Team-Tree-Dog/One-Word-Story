package adapters.controllers;

import usecases.disconnecting.DcInputBoundary;
import usecases.disconnecting.DcInputData;

public class DcController {

    private final DcInputBoundary dc;

    /**
     * Take in and set an instance of the input boundary that
     * is intended to be called by users from the view
     */
    public DcController (DcInputBoundary dc) {
        this.dc = dc;
    }

    /**
     * Provide the ID of a player who wishes to disconnect from the game, or who has already
     * disconnected and the server is simply requesting their removal
     * @param playerId Unique ID of player never previously used
     */
    public void disconnect (String playerId) {
        dc.disconnect(
                new DcInputData(playerId)
        );
    }
}
