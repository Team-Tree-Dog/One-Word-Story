package usecases.get_latest_stories;

import org.jetbrains.annotations.Nullable;
import usecases.StoryData;

/**
 * Input Boundary Interface for Get Latest Stories use-case.
 * Implemented by GlsInteractor
 */

public interface GlsGatewayStory {
    /**
     * @return all the currently saved stories, or null if DB fails
     */
    @Nullable
    StoryData[] getAllStories ();
}
