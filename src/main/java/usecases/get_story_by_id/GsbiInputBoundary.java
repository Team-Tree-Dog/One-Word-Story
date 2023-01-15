package usecases.get_story_by_id;

import org.jetbrains.annotations.NotNull;

public interface GsbiInputBoundary {
    void getStory(int storyId, GsbiOutputBoundary pres);
}
