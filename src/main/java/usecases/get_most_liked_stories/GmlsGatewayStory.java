package usecases.get_most_liked_stories;

import org.jetbrains.annotations.NotNull;
import usecases.RepoRes;
import usecases.StoryRepoData;

/**
 * Defines the abstract method for the repository to retrieve all stories
 */
public interface GmlsGatewayStory {
    /**
     * @return all stories from the repository in the RepoRes wrapper object. If repo operation
     * fails, RepoRes will reflect it
     */
    @NotNull
    RepoRes<StoryRepoData> getAllStories();
}
