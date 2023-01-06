package usecases.get_story_by_id;

public interface GsbiInputBoundary {
    void getStory(int storyId, GsbiOutputBoundary pres);
}
