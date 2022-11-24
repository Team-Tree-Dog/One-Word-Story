package usecases.like_story;

public class LsInputData {

    private final String requestId;
    private final int storyId;

    /**
     * @param requestId The unique id for this request
     * @param storyId The id of the story that should be liked
     * */
    public LsInputData(String requestId, int storyId) {
        this.requestId = requestId;
        this.storyId = storyId;
    }

    /**
     * @return This method returns the request's id
     * */
    public String getRequestId() {
        return requestId;
    }


    /**
     * @return This method returns the id of the story that should receive a like
     * */
    public int getStoryId() {
        return storyId;
    }
}
