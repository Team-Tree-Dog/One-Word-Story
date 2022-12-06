package usecases.get_all_titles;

import usecases.RepoRes;
import usecases.TitleRepoData;

/**
 * Gateway interface that is used to get all titles for a particular story
 */
public interface GatGatewayTitles {

    /**
     * Abstract method to get all suggested titles for this story
     * @param storyId   the ID of the story
     * @return          all suggested titles for this story, or null only if the DB has failed
     */
    RepoRes<TitleRepoData> getAllTitles(int storyId);
}
