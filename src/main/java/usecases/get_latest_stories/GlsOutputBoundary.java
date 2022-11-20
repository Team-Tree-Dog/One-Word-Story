package usecases.get_latest_stories;

/**
 *  Output boundary Interface for Get Latest Stories use-case
 *  Implemented by the Presenter
 */

public interface GlsOutputBoundary {
    /**
     * Updates ViewModel with the stories
     * @param data PdOutputData
     */
    void putStories (GlsOutputData data);

}
