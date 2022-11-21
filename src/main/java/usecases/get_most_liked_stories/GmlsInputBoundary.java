package usecases.get_most_liked_stories;

/**
 * Defines the abstract method to begin the Get Most Liked Stories use case
 */
public interface GmlsInputBoundary {

    /**
     * Abstract method to begin the Get Most Liked Stories use case
     * @param data the input data for the use case
     */
    void getMostLikedStories (GmlsInputData data);
}
