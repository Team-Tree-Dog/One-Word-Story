package frameworks_drivers.repository.in_memory;

import entities.Story;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.RepoRes;
import usecases.Response;
import usecases.StoryRepoData;
import usecases.get_latest_stories.GlsGatewayStory;
import usecases.get_most_liked_stories.GmlsGatewayStory;
import usecases.like_story.LsGatewayStory;
import java.time.LocalDateTime;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * In memory implementation of the database component in charge of storing stories
 * along with their authors
 */
public class InMemoryStoryRepo implements LsGatewayStory, GlsGatewayStory,
        GmlsGatewayStory, PgeGatewayStory {

    /**
     * Simulates a single row entry in a DB table. Does not separate authors table
     * and instead stores a list of author strings since we don't have the power
     * of SQL join statements here
     */
    private static class StoryTableRow {
        // Simulates primary keys for storyId
        private static int nextAvailableId = 0;

        private final int storyId;
        private final String story;
        private final double publishUnixTimestamp;
        private final String[] authors;
        private int likes;

        public StoryTableRow (@NotNull String story, double publishUnixTimestamp, String[] authors) {
            storyId = nextAvailableId;
            nextAvailableId++;

            this.story = story;
            this.publishUnixTimestamp = publishUnixTimestamp;
            this.authors = authors;
            this.likes = 0;
        }

        public int getStoryId() { return storyId; }
        public String getStory() { return story; }
        public double getPublishUnixTimestamp() { return publishUnixTimestamp; }
        public int getLikes() { return likes; }
        public String[] getAuthors() { return authors; }

        public void addLike () { this.likes++; }
    }

    private final List<StoryTableRow> storyTable;

    /**
     * Initialize story table
     */
    public InMemoryStoryRepo () {
        storyTable = new ArrayList<>();
    }


    /**
     * @return all stories from the repository in the RepoRes wrapper object. If repo operation
     *      * fails, RepoRes will reflect it
     */
    @Override
    @NotNull
    public RepoRes<StoryRepoData> getAllStories() {
        RepoRes<StoryRepoData> storyData = new RepoRes<>();

        // Convert story table rows to StoryRepoData objects
        for (StoryTableRow row : storyTable) {

            storyData.addRow(new StoryRepoData(
                    row.getStoryId(), row.getStory(), row.getAuthors(),
                    // No idea what offset means, or nanoOfSecond. Just guessing here
                    LocalDateTime.ofEpochSecond((long) row.getPublishUnixTimestamp(),
                            0, ZoneOffset.UTC), row.getLikes()
            ));
        }

        storyData.setResponse(Response.getSuccessful("Stories successfully retrieved"));

        return storyData;
    }

    /**
     * This method adds a like to the given story
     * @param storyId unique primary key ID of story to which to add a like
     * @return success of the operation or a fail code
     * */
    @Override
    @NotNull
    public Response likeStory(int storyId) {
        for (StoryTableRow row : storyTable) {
            if (row.getStoryId() == storyId) {
                row.addLike();
                return Response.getSuccessful("Like added to story with ID: " + storyId);
            }
        } return new Response(Response.ResCode.STORY_NOT_FOUND,
                "A story with ID " + storyId + " doesn't exist");
    }

    /**
     * @param story string content of a completed story
     * @param publishUnixTimeStamp the unix epoch in seconds (seconds since Jan 1970 or something)
     * @param authorDisplayNames a list of strings of only the display names of contributing players.
     *                           if null is passed, no authors will be saved
     * @return success of saving the story to the DB
     */
    @Override
    @NotNull
    public Response saveStoryNoTitle (String story, double publishUnixTimeStamp,
                                     @Nullable String[] authorDisplayNames) {
        if (authorDisplayNames == null) {
            authorDisplayNames = new String[0];
        }

        storyTable.add(new StoryTableRow(
            story, publishUnixTimeStamp, authorDisplayNames
        ));

        return Response.getSuccessful("Story successfully saved");
    }
}
