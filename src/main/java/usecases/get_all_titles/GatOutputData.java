package usecases.get_all_titles;

import org.jetbrains.annotations.Nullable;
import usecases.Response;
import usecases.TitleRepoData;
import java.util.*;

/**
 * The Output Data for this use case. Contains a Response containing the response code and description (whether the
 * request to get all titles was successful) to be sent to the presenter and the List of suggested titles if successful.
 */
public class GatOutputData {
    private List<TitleRepoData> suggestedTitles;
    private Response res;

    /**
     * Constructor for output data.
     * @param suggestedTitles list of suggested titles for the story. Is null if and only if getting all titles was
     *                        unsuccessful
     * @param res             the response object that notifies whether the request to get all titles was
     *                        successful
     */
    public GatOutputData(@Nullable List<TitleRepoData> suggestedTitles, Response res) {
        this.suggestedTitles = suggestedTitles;
        this.res = res;
    }

    /**
     * @return All suggested titles for the story, or can be null (in which case the request to get all titles has
     *         failed)
     */
    @Nullable
    public List<TitleRepoData> getSuggestedTitles() {return suggestedTitles;}

    public Response getRes() {return res;}
}
