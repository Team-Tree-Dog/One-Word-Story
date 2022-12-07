package usecases.get_most_liked_stories;

import usecases.shutdown_server.SsOutputBoundary;

/**
 * Defines the abstract method to update the View Model in the Get Most Liked Stories use case
 */
public interface GmlsOutputBoundary extends SsOutputBoundary {
    /**
     * The abstract method to update the View Model in the Get Most Liked Stories use case
     * @param data the output data for this use case
     */
    void putStories (GmlsOutputData data);
}
