package usecases.get_latest_stories;

import usecases.shutdown_server.SsOutputBoundary;

/**
 *  Output boundary Interface for Get Latest Stories use-case
 *  Implemented by the Presenter
 */

public interface GlsOutputBoundary extends SsOutputBoundary {
    /**
     * Updates ViewModel with the stories
     * @param data PdOutputData
     */
    void putStories (GlsOutputData data);

}
