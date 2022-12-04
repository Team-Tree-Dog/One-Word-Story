package usecases.get_most_liked_stories;

import usecases.StoryRepoData;

/**
 * Defines the abstract method for the repository to retrieve all stories
 */
public interface GmlsGatewayStory {
    /**
     * Abstract method for the repository to retrieve all stories
     * @return all stories from the repository, or null if the DB fails
     */
    StoryRepoData[] getAllStories();
}
