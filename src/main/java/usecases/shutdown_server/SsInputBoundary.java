package usecases.shutdown_server;


/**
 * The input boundary for the "shutdown-server" use case
 */
public interface SsInputBoundary {

    /**
     * This method completely shuts down the server
     * @param pres output boundary for this use case
     */
    void shutdownServer(SsOutputBoundary pres);

}
