package usecases.upvote_title;

/**
 * Input data for this use case.
 */
public class UtInputData {
    private int storyId;
    private String titleToUpvote;

    /**
     * @param storyId       the ID of the story whose title we want to upvote
     * @param titleToUpvote the title of the story that is to be upvoted
     */
    public UtInputData(int storyId, String titleToUpvote) {
        this.storyId = storyId;
        this.titleToUpvote = titleToUpvote;
    }

    public int getStoryId() {
        return storyId;
    }

    public String getTitleToUpvote() {
        return titleToUpvote;
    }
}
