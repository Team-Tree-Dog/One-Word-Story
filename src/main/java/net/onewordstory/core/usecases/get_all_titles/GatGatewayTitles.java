package net.onewordstory.core.usecases.get_all_titles;

import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.TitleRepoData;

/**
 * Gateway interface that is used to get all titles for a particular story
 */
public interface GatGatewayTitles {

    /**
     * Abstract method to get all suggested titles for this story
     * @param storyId   the ID of the story
     * @return          all suggested titles for this story, or null only if the repo has failed for some reason
     */
    RepoRes<TitleRepoData> getAllTitles(int storyId);
}
