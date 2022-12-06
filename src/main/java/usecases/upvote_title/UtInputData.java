package usecases.upvote_title;

/**
 * Input data for this use case.
 */
public class UtInputData {
    private String requestId;
    private int storyId;
    private String titleToUpvote;

    /**
     * @param requestId     the ID tracking this particular request to upvote the title
     * @param storyId       the ID of the story whose title we want to upvote
     * @param titleToUpvote the title of the story that is to be upvoted
     */
    public UtInputData(String requestId, int storyId, String titleToUpvote) {
        this.requestId = requestId;
        this.storyId = storyId;
        this.titleToUpvote = titleToUpvote;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getStoryId() {
        return storyId;
    }

    public String getTitleToUpvote() {
        return titleToUpvote;
    }
}
