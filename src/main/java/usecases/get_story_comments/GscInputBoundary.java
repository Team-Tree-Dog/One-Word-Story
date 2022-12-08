package usecases.get_story_comments;

/**
 * Input boundary for Get Story Comments use case
 */
public interface GscInputBoundary {

    /**
     * Gets all comments made on a story
     * @param data contains the id of the story to retrieve the comments from
     * @param pres output boundary for this use case
     */
    void getStoryComments(GscInputData data, GscOutputBoundary pres);
}
