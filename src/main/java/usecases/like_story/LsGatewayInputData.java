package usecases.like_story;

public class LsGatewayInputData {

    private final int storyId;

    /**
     * @param storyId The id of the story that should be liked
     * */
    public LsGatewayInputData(int storyId) {
        this.storyId = storyId;
    }


    /**
     * @return Returns the id of the story that should be liked
     * */
    public int getStoryId() {
        return storyId;
    }
}
