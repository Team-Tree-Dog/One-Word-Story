package usecases.get_latest_stories;

import usecases.StoryData;

/**
 * Input Boundary Interface for Get Latest Stories use-case.
 * Implemented by GlsInteractor
 */

public interface GlsGatewayStory {
    /**
     * @return all stories from the repository, or null if the DB failed in some way
     */
    StoryData[] getAllStories ();
}
