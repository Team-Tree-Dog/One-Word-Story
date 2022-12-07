package usecases.suggest_title;

/**
 * The input data for the Suggest Title use case
 */
public class StInputData {
    private String requestId;
    private String title;
    private int storyId;

    /**
     * Constructor for Input Data for Suggest Title use case
     * @param requestId the ID that the ViewModel uses to identify this specific suggestion request
     * @param title     the title for the story that is suggested by the user
     * @param storyId   the ID used to identify the story
     */
    public StInputData(String requestId, String title, int storyId){
        this.requestId = requestId;
        this.title = title;
        this.storyId = storyId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTitle() {
        return title;
    }

    public int getStoryId() {
        return storyId;
    }
}
