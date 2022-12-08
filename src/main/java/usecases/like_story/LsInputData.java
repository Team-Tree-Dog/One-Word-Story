package usecases.like_story;

public class LsInputData {

    private final int storyId;

    /**
     * @param storyId The id of the story that should be liked
     * */
    public LsInputData(int storyId) {
        this.storyId = storyId;
    }

    /**
     * @return This method returns the id of the story that should receive a like
     * */
    public int getStoryId() {
        return storyId;
    }
}
