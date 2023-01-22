package net.onewordstory.core.usecases.get_most_liked_stories;

/**
 * Defines the abstract method to begin the Get Most Liked Stories use case
 */
public interface GmlsInputBoundary {

    /**
     * Abstract method to begin the Get Most Liked Stories use case
     * @param data the input data for the use case
     * @param pres output boundary for this use case
     */
    void getMostLikedStories (GmlsInputData data, GmlsOutputBoundary pres);
}
