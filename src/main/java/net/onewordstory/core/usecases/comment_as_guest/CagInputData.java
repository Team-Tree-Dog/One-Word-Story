package net.onewordstory.core.usecases.comment_as_guest;

/**
 * Input data for Comment As Guest use case
 */
public class CagInputData {

    private String displayName;
    private String comment;
    private int storyId;

    /**
     * Constructor for CagInputData
     * @param displayName the display name that the guest who commented chose
     * @param comment the comment the guest wrote
     * @param storyId the id of the story the comment was posted on
     */
    public CagInputData(String displayName, String comment, int storyId) {

        this.displayName = displayName;
        this.comment = comment;
        this.storyId = storyId;
    }

    /**
     * @return guest display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return comment guest typed
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return id of story guest commented on
     */
    public int getStoryId() {
        return storyId;
    }
}
