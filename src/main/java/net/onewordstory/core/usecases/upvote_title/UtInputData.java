package net.onewordstory.core.usecases.upvote_title;

/**
 * Input data for this use case.
 */
public class UtInputData {
    private final int storyId;
    private final String titleToUpvote;
    private final String guestAccountId;

    /**
     * @param storyId       the ID of the story whose title we want to upvote
     * @param titleToUpvote the title of the story that is to be upvoted
     */
    public UtInputData(int storyId, String titleToUpvote, String guestAccountId) {
        this.storyId = storyId;
        this.titleToUpvote = titleToUpvote;
        this.guestAccountId = guestAccountId;
    }

    public int getStoryId() { return storyId; }

    public String getTitleToUpvote() { return titleToUpvote; }

    public String getGuestAccountId() { return guestAccountId; }
}
