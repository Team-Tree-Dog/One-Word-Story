package net.onewordstory.core.usecases.get_latest_stories;

import org.jetbrains.annotations.NotNull;
import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.StoryRepoData;

public interface GlsGatewayStory {

    /**
     * @return all stories from the repository in the RepoRes wrapper object. If repo operation
     *      * fails, RepoRes will reflect it
     */
    @NotNull
    RepoRes<StoryRepoData> getAllStories();
}
