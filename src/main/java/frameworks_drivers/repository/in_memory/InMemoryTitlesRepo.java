package frameworks_drivers.repository.in_memory;

import org.jetbrains.annotations.NotNull;
import usecases.RepoRes;
import usecases.Response;
import usecases.TitleRepoData;
import usecases.get_all_titles.GatGatewayTitles;
import usecases.get_latest_stories.GlsGatewayTitles;
import usecases.get_most_liked_stories.GmlsGatewayTitles;
import usecases.get_story_by_id.GsbiGatewayTitles;
import usecases.suggest_title.StGatewayTitles;
import usecases.upvote_title.UtGatewayTitles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In memory implementation of the database component in charge of storing
 * suggested titles for stories
 *
 * <h2> Thread Safety </h2>
 * This class is thread safe
 * <br> All read and write operations
 * engage a repo lock
 * <br><br>
 */
public class InMemoryTitlesRepo implements GatGatewayTitles, StGatewayTitles,
        UtGatewayTitles, GlsGatewayTitles, GmlsGatewayTitles, GsbiGatewayTitles {

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
    private final Lock lock;

    /**
     * Initialize comments table
     */
    public InMemoryTitlesRepo () {
        titlesTable = new ArrayList<>();
        lock = new ReentrantLock();
    }


    /**
     * Defaults to 1 upvote when adding, from the player who suggested it
     * <br><br>
     * <h2> Thread Safety </h2>
     * This method is thread safe with respect to the repo.
     * <br> All read and write operations
     * engage the repo lock
     * <br><br>
     * @param storyId unique primary key ID of story for which to save the title suggestion
     * @param titleSuggestion string suggestion of the title
     * @return if the title was successfully added to the DB
     */
    @Override
    @NotNull
    public Response suggestTitle (int storyId, @NotNull String titleSuggestion) {
        TitlesTableRow newRow = new TitlesTableRow(storyId, titleSuggestion, 1);
        Response res;

        lock.lock();

        // Row equality is based on storyId and titleSuggestion
        if (titlesTable.contains(newRow)) {
            res = new Response(Response.ResCode.TITLE_ALREADY_SUGGESTED,
                    "\"" + titleSuggestion + "\" has already been suggested for Story " + storyId);
        } else {
            titlesTable.add(newRow);

            res = Response.getSuccessful("Successfully added new suggested title to Story ID" + storyId);
        }
        lock.unlock();

        return res;
    }

    /**
     * <br><br>
     * <h2> Thread Safety </h2>
     * This method is thread safe with respect to the repo.
     * <br> All read and write operations
     * engage the repo lock
     * <br><br>
     * @param storyId unique primary key ID of story for which to retrieve all suggested titles
     * @return all suggested titles pertaining to the requested story, or null if DB failed
     */
    @Override
    @NotNull
    public RepoRes<TitleRepoData> getAllTitles (int storyId) {
        RepoRes<TitleRepoData> res = new RepoRes<>();

        lock.lock();
        // Convert to CommentRepoData objects
        for (TitlesTableRow row : titlesTable) {
            if (row.storyId == storyId) {
                res.addRow(new TitleRepoData(
                        row.getSuggestionId(), row.getStoryId(),
                        row.getTitleSuggestion(), row.getUpvotes()
                ));
            }
        }
        lock.unlock();

        res.setResponse(Response.getSuccessful("Successfully retrieved titles for story " + storyId));

        return res;
    }

    /**
     * @param storyId       the ID of the story whose title is to be upvoted
     * @param titleToUpvote the title to be upvoted
     * @return Response of upvoting title, success or fail code
     */
    @Override
    @NotNull
    public Response upvoteTitle(int storyId, String titleToUpvote) {
        Response res = null;

        lock.lock();
        for (TitlesTableRow row: titlesTable) {
            // If story id and title suggestion string match, add upvote
            if (row.getStoryId() == storyId && row.getTitleSuggestion().equals(titleToUpvote)) {
                row.addUpvote();
                res = Response.getSuccessful("Successfully upvoted title");
                break;
            }
        }
        lock.unlock();

        if (res == null) {
            res = new Response(Response.ResCode.TITLE_NOT_FOUND,
                    "Title \"" + titleToUpvote + "\" not found for story ID " + storyId);
        }

        return res;
    }

    @Override
    public @NotNull RepoRes<String> getMostUpvotedStoryTitle(int storyId) {
        lock.lock();

        String suggestion = null;
        int upvotes = -1;

        for (TitlesTableRow row : titlesTable) {
            if (row.getStoryId() == storyId) {
                if (row.getUpvotes() > upvotes) {
                    suggestion = row.getTitleSuggestion();
                    upvotes = row.getUpvotes();
                }
            }
        }

        lock.unlock();

        if (suggestion == null) {
            return new RepoRes<>(Response.getFailure("Either story doesn't exist or has no titles"));
        } else {
            List<String> s = new ArrayList<>();
            s.add(suggestion);

            return new RepoRes<>(Response.getSuccessful("Found most upvoted title"), s);
        }
    }
}
