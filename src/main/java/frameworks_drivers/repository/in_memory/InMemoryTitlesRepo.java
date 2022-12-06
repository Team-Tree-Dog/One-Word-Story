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

        /**
         * Criteria of equality:
         * <ol>
         *     <li> obj is of type TitlesRowTable </li>
         *     <li> both have equal storyId </li>
         *     <li> both have equal titleSuggestion </li>
         * </ol>
         * @param obj object to compare equality to
         * @return if this row is equal to another object by described criteria
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TitlesTableRow) {
                TitlesTableRow row = (TitlesTableRow) obj;

                return row.titleSuggestion.equals(this.titleSuggestion) &&
                        row.storyId == this.storyId;
            } return false;
        }
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
        TitlesTableRow newRow = new TitlesTableRow(storyId, titleSuggestion, 1);

        // Row equality is based on storyId and titleSuggestion
        if (titlesTable.contains(newRow)) {
            return new Response(Response.ResCode.TITLE_ALREADY_SUGGESTED,
                    "\"" + titleSuggestion + "\" has already been suggested for Story " + storyId);
        }

        titlesTable.add(newRow);
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
