package net.onewordstory.core.usecases.get_latest_stories;

import org.jetbrains.annotations.NotNull;
import net.onewordstory.core.usecases.RepoRes;

public interface GlsGatewayTitles {
    /**
     * @param storyId id of the story for which to get the title
     * @return the string of the most upvoted suggested title for this story, or a fail
     * code if something is wrong
     */
    @NotNull
    RepoRes<String> getMostUpvotedStoryTitle(int storyId);
}
