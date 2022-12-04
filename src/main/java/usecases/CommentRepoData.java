package usecases;

/**
 * A single comment from the repository
 */
public class CommentRepoData {

    private int commentId;
    private int storyId;
    private String displayName;
    private String content;

    /**
     * Constructor for CommentRepoData
     * @param commentId the unique identifier for this comment
     * @param storyId the unique identifier for the story this comment is on
     * @param displayName the chosen display name of the guest that wrote the comment
     * @param content the actual comment written
     */
    public CommentRepoData(int commentId, int storyId, String displayName, String content) {

        this.commentId = commentId;
        this.storyId = storyId;
        this.displayName = displayName;
        this.content = content;
    }
}
