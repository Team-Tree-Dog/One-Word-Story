package usecases.get_latest_stories;

/**
 * Input Boundary Interface for Get Latest Stories use-case.
 * Implemented by GlsInteractor
 */
public interface GlsInputBoundary {

    /**
     * Performs a timely update.
     * @param data GlsInputData corresponding to the current game state
     */
    void getLatestStories(GlsInputData data, GlsOutputBoundary pres);
}
