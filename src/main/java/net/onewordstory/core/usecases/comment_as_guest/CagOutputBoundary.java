package net.onewordstory.core.usecases.comment_as_guest;

import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

/**
 * Output boundary for Comment As Guest use case
 */
public interface CagOutputBoundary extends SsOutputBoundary {

    void commentAsGuestOutput(CagOutputData data);
}
