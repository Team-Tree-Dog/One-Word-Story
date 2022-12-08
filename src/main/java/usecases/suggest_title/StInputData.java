package usecases.suggest_title;

/**
 * The input data for the Suggest Title use case
 */
public class StInputData {
    private String title;
    private int storyId;

    /**
     * Constructor for Input Data for Suggest Title use case
     * @param title     the title for the story that is suggested by the user
     * @param storyId   the ID used to identify the story
     */
    public StInputData(String title, int storyId){
        this.title = title;
        this.storyId = storyId;
    }

    public String getTitle() {
        return title;
    }

    public int getStoryId() {
        return storyId;
    }
}
