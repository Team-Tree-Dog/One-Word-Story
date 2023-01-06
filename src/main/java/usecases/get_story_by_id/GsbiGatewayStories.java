package usecases.get_story_by_id;

import org.jetbrains.annotations.NotNull;
import usecases.RepoRes;
import usecases.StoryRepoData;

public interface GsbiGatewayStories {

    /**
     * Get single story by specified ID
     * @param storyId id of story to retrieve
     * @return a RepoRes object, which contains a response. If response is a success, it contains
     * a list of rows containing a single row representing the retrieved story. otherwise this extra
     * data is null
     */
    @NotNull
    RepoRes<StoryRepoData> getStoryById(int storyId);
}
