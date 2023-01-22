package net.onewordstory.core.usecases.get_most_liked_stories;

import org.jetbrains.annotations.NotNull;
import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.StoryRepoData;

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
