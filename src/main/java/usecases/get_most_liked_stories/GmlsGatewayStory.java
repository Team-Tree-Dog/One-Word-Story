package usecases.get_most_liked_stories;

import org.jetbrains.annotations.Nullable;
import usecases.StoryData;

/**
 * Defines the abstract method for the repository to retrieve all stories
 */
public interface GmlsGatewayStory {
    /**
     * @return all the currently saved stories, or null if DB fails
     */
    @Nullable
    StoryData[] getAllStories();
}
