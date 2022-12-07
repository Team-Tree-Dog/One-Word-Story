package usecases.comment_as_guest;

import usecases.shutdown_server.SsOutputBoundary;

/**
 * Output boundary for Comment As Guest use case
 */
public interface CagOutputBoundary extends SsOutputBoundary {

    void commentAsGuestOutput(CagOutputData data);
}
