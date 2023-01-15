package adapters.controllers;

import adapters.presenters.SsPresenter;
import adapters.view_models.SsViewModel;
import org.example.ANSI;
import org.example.Log;
import usecases.shutdown_server.SsInputBoundary;

/**
 * The controller for the "shutdown-server" use case
 */
public class SsController {

    private final SsInputBoundary inputBoundary;

    /**
     * @param inputBoundary The input boundary for the "shutdown-server" use case
     */
    public SsController(SsInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    /**
     * This method initiates the process of shutting down the server
     * @return View model for this use case
     */
    public SsViewModel shutdownServer() {
        Log.sendMessage(ANSI.BLUE, "SS", ANSI.LIGHT_BLUE,
                "Controller called");
        SsViewModel viewM = new SsViewModel();
        SsPresenter pres = new SsPresenter(viewM);
        inputBoundary.shutdownServer(pres);
        return viewM;
    }

}
