package usecases.suggest_title;

import org.jetbrains.annotations.NotNull;
import usecases.RepoRes;
import usecases.Response;
import usecases.TitleRepoData;

/**
 * Gateway interface implemented by the repository for this use case.
 * This interface contains an abstract method to suggest the story title, for which the
 * output that is returned records the success or failure of adding the title to the database.
 * This interface also contains an abstract method to retrieve all previously suggested story titles from the repo,
 * which is utilized in this use case to check whether the suggested title has not been previously suggested.
 */
public interface StGatewayTitles {
    /**
     * Abstract method for the repository to suggest a title for the story, and to return an Output Data object
     * that records the success or failure of adding this title to the database.
     * @param storyId           the ID to track this story
     * @param titleSuggestion   the user suggested title for this story
     * @return  a response object that contains the appropriate message depending on whether the request to add the
     *          title was successful or not
     */
    @NotNull
    Response suggestTitle(int storyId, @NotNull String titleSuggestion);

    /**
     * Abstract method to retrieve all previously suggested story titles from the repository
     * @param storyId the ID for the story whose previously suggested titles we want to retrieve
     * @return        the previously suggested titles for this particular story
     */
    @NotNull
    RepoRes<TitleRepoData> getAllTitles(int storyId);
}
