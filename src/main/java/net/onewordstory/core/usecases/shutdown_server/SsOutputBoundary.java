package net.onewordstory.core.usecases.shutdown_server;

/**
 * All the output boundaries of all non-daemon use cases must extend this interface.
 */
public interface SsOutputBoundary {

    /**
     * This method notifies that the use case was interrupted by server shutdown. React accordingly
     */
    void outputShutdownServer();

}
