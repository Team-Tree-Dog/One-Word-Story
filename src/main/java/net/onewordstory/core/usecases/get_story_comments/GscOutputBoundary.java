package net.onewordstory.core.usecases.get_story_comments;

import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

/**
 * Output boundary for Get Story Comments use case
 */
public interface GscOutputBoundary extends SsOutputBoundary {

    void putStoryComments(GscOutputData data);
}
