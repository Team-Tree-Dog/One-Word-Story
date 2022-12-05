package frameworks_drivers.repository.in_memory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.Response;

/**
 * In memory implementation of the database component in charge of storing
 * suggested titles for stories
 */
public class InMemoryTitlesRepo implements GatGatewayTitles, StGatewayTitles {

    /**
     * @param storyId unique primary key ID of story for which to save the title suggestion
     * @param titleSuggestion string suggestion of the title
     * @return if the title was successfully added to the DB
     */
    @Override
    @NotNull
    public Response suggestTitle (int storyId, @NotNull String titleSuggestion) {

    }

    /**
     * @param storyId unique primary key ID of story for which to retrieve all suggested titles
     * @return all suggested titles pertaining to the requested story, or null if DB failed
     */
    @Override
    @NotNull
    public RepoRes<TitleRepoData> getAllTitles (int storyId) {

    }

}
