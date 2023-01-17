package net.onewordstory.core.usecases.disconnecting;

import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

/**
 * Output Boundary interface for Disconnecting Use Case
 * It is implemented by Presenter
 */

public interface DcOutputBoundary extends SsOutputBoundary {

    /**
     * Notifies if user has been disconnected from the server
     * @param data response
     */
    void hasDisconnected(DcOutputData data);
}
