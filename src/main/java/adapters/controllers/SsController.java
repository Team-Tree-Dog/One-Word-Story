package adapters.controllers;

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
     */
    public void shutdownServer() {
        inputBoundary.shutdownServer();
    }

}
