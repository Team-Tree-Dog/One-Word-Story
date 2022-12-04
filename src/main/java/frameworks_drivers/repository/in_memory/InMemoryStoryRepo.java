package frameworks_drivers.repository.in_memory;

import entities.Story;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.StoryData;
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
        private String title;
        private final String[] authors;
        private int likes;

        public StoryTableRow (@NotNull String story, double publishUnixTimestamp, String[] authors,
                              @Nullable String title) {
            storyId = nextAvailableId;
            nextAvailableId++;

            this.story = story;
            this.publishUnixTimestamp = publishUnixTimestamp;
            this.title = title;
            this.authors = authors;
            this.likes = 0;
        }

        public StoryTableRow (@NotNull String story, double publishUnixTimestamp, String[] authors) {
            this(story, publishUnixTimestamp, authors, null);
        }

        public int getStoryId() { return storyId; }
        public String getStory() { return story; }
        public double getPublishUnixTimestamp() { return publishUnixTimestamp; }
        public int getLikes() { return likes; }
        public String getTitle() { return title; }
        public String[] getAuthors() { return authors; }

        public void addLike () { this.likes++; }
        public void setTitle (String title) { this.title = title; }
    }

    private final List<StoryTableRow> storyTable;

    /**
     * Initialize story table
     */
    public InMemoryStoryRepo () {
        storyTable = new ArrayList<>();
    }

    /**
     * @return all the currently saved stories, or null if DB fails
     */
    @Override
    public StoryData @NotNull [] getAllStories() {
        StoryData[] stories = new StoryData[storyTable.size()];

        // Convert story table row entry to StoryData
        for (int i = 0; i <= storyTable.size(); i++) {
            StoryTableRow row = storyTable.get(i);

            stories[i] = new StoryData(
                    row.getStory(), row.getAuthors(),
                    // No idea what offset means, or nanoOfSecond. Just guessing here
                    LocalDateTime.ofEpochSecond((long) row.getPublishUnixTimestamp(),
                            0, ZoneOffset.UTC),
                    row.getTitle(), row.getLikes()
            );
        }

        return stories;
    }

    /**
     * @param storyId unique primary key ID of story to which to add a like
     * @return success of adding a like to the requested story
     */
    @Override
    public @NotNull boolean likeStory(int storyId) {
        for (StoryTableRow row : storyTable) {
            if (row.getStoryId() == storyId) {
                row.addLike();
                return true;
            }
        } return false;
    }

    /**
     * @param story string content of a completed story
     * @param publishUnixTimeStamp the unix epoch in seconds (seconds since Jan 1970 or something)
     * @param authorDisplayNames a list of strings of only the display names of contributing players.
     *                           if null is passed, no authors will be saved
     * @return success of saving the story to the DB
     */
    @Override
    public boolean saveStoryNoTitle (String story, double publishUnixTimeStamp,
                                     @Nullable String[] authorDisplayNames) {
        if (authorDisplayNames == null) {
            authorDisplayNames = new String[0];
        }

        storyTable.add(new StoryTableRow(
            story, publishUnixTimeStamp, authorDisplayNames
        ));

        return true;
    }
}
