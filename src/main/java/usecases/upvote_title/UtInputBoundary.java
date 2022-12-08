package usecases.upvote_title;

/**
 * Input boundary for upvote title use case.
 */
public interface UtInputBoundary {

    /**
     * Upvote the title as specified in this input data.
     * @param data  Input data for the use case, contains the title to be upvoted and storyId of the story
     *              hose title is to be upvoted
     */
    void upvoteTitle(UtInputData data, UtOutputBoundary pres);
}
