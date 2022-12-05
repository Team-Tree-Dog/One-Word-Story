package frameworks_drivers.repository.in_memory;

import org.jetbrains.annotations.NotNull;
import usecases.CommentRepoData;
import usecases.RepoRes;
import usecases.Response;
import usecases.TitleRepoData;
import usecases.suggest_title.StGatewayTitles;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory implementation of the database component in charge of storing
 * suggested titles for stories
 */
public class InMemoryTitlesRepo implements GatGatewayTitles, StGatewayTitles {

    /**
     * DB table row for storing a single suggested title entry
     */
    private static class TitlesTableRow {

        private static int nextAvailableId = 0;
        private final int suggestionId;
        private final int storyId;
        private final String titleSuggestion;
        private int upvotes;

        public TitlesTableRow (int storyId, @NotNull String titleSuggestion, int upvotes) {
            suggestionId = nextAvailableId;
            nextAvailableId++;

            this.storyId = storyId;
            this.titleSuggestion = titleSuggestion;
            this.upvotes = upvotes;
        }

        public int getSuggestionId() { return suggestionId; }
        public int getUpvotes() { return upvotes; }
        public String getTitleSuggestion() { return titleSuggestion; }
        public int getStoryId() { return storyId; }

        /**
         * Will be used by Upvote Title use case
         */
        public void addUpvote() { upvotes++; }
    }

    private final List<TitlesTableRow> titlesTable;

    /**
     * Initialize comments table
     */
    public InMemoryTitlesRepo () { titlesTable = new ArrayList<>(); }


    /**
     * Defaults to 1 upvote when adding, from the player who suggested it
     * @param storyId unique primary key ID of story for which to save the title suggestion
     * @param titleSuggestion string suggestion of the title
     * @return if the title was successfully added to the DB
     */
    @Override
    @NotNull
    public Response suggestTitle (int storyId, @NotNull String titleSuggestion) {
        titlesTable.add(new TitlesTableRow(storyId, titleSuggestion, 1));
        return Response.getSuccessful("Successfully added new suggested title to Story ID" + storyId);
    }

    /**
     * @param storyId unique primary key ID of story for which to retrieve all suggested titles
     * @return all suggested titles pertaining to the requested story, or null if DB failed
     */
    @Override
    @NotNull
    public RepoRes<TitleRepoData> getAllTitles (int storyId) {
        RepoRes<TitleRepoData> res = new RepoRes<>();

        // Convert to CommentRepoData objects
        for (TitlesTableRow row : titlesTable) {
            res.addRow(new TitleRepoData(
                    row.getSuggestionId(), row.getStoryId(),
                    row.getTitleSuggestion(), row.getUpvotes()
            ));
        }

        res.setResponse(Response.getSuccessful("Successfully retrieved titles for story " + storyId));

        return res;
    }

}
