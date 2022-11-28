package usecases.get_most_liked_stories;

/**
 * Defines the abstract method to update the View Model in the Get Most Liked Stories use case
 */
public interface GmlsOutputBoundary {
    /**
     * The abstract method to update the View Model in the Get Most Liked Stories use case
     * @param data the output data for this use case
     */
    void putStories (GmlsOutputData data);
}
