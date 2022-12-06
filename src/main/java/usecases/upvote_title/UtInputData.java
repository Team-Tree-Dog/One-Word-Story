package usecases.upvote_title;

public class UtInputData {
    private String requestId;
    private int storyId;
    private String titleToUpvote;

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
