package net.onewordstory.core.usecases.like_story;

public class LsInputData {

    private final int storyId;
    private final String guestAccountId;

    /**
     * @param storyId The id of the story that should be liked
     * */
    public LsInputData(int storyId, String guestAccountId) {
        this.storyId = storyId;
        this.guestAccountId = guestAccountId;
    }

    /**
     * @return This method returns the id of the story that should receive a like
     * */
    public int getStoryId() { return storyId; }

    /**
     * @return ID of account liking the story
     */
    public String getGuestAccountId() { return guestAccountId; }
}
