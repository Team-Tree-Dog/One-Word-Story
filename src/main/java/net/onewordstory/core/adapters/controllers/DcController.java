package net.onewordstory.core.adapters.controllers;

import net.onewordstory.core.adapters.presenters.DcPresenter;
import net.onewordstory.core.adapters.view_models.DcViewModel;
import org.example.ANSI;
import org.example.Log;
import net.onewordstory.core.usecases.disconnecting.DcInputBoundary;
import net.onewordstory.core.usecases.disconnecting.DcInputData;

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
     * @return View model for this use case
     */
    public DcViewModel disconnect (String playerId) {
        Log.sendMessage(ANSI.BLUE, "DC", ANSI.LIGHT_BLUE,
                "Controller called with ID " + playerId);
        DcViewModel viewM = new DcViewModel();
        DcPresenter pres = new DcPresenter(viewM);
        dc.disconnect(new DcInputData(playerId), pres);
        return viewM;
    }
}
