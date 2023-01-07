package frameworks_drivers.repository.in_memory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.RepoRes;
import usecases.Response;
import usecases.StoryRepoData;
import usecases.get_latest_stories.GlsGatewayStory;
import usecases.get_most_liked_stories.GmlsGatewayStory;
import usecases.get_story_by_id.GsbiGatewayStories;
import usecases.like_story.LsGatewayStory;
import usecases.pull_game_ended.PgeGatewayStory;

import java.time.LocalDateTime;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In memory implementation of the database component in charge of storing stories
 * along with their authors
 *
 * <h2> Thread Safety </h2>
 * This class is thread safe
 * <br> All read and write operations
 * engage a repo lock
 * <br><br>
 */
public class InMemoryStoryRepo implements LsGatewayStory, GlsGatewayStory,
        GmlsGatewayStory, GsbiGatewayStories, PgeGatewayStory {

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
    private final Lock lock;

    /**
     * Initialize story table
     */
    public InMemoryStoryRepo () {
        storyTable = new ArrayList<>();
        lock = new ReentrantLock();
    }


    /**
     * <h2> Thread Safety </h2>
     * This method is thread safe with respect to the repo.
     * <br> All read and write operations
     * engage the repo lock
     * <br><br>
     * @return all stories from the repository in the RepoRes wrapper object. If repo operation
     *      * fails, RepoRes will reflect it
     */
    @Override
    @NotNull
    public RepoRes<StoryRepoData> getAllStories() {
        lock.lock();
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
        lock.unlock();

        storyData.setResponse(Response.getSuccessful("Stories successfully retrieved"));

        return storyData;
    }

    /**
     * This method adds a like to the given story
     * <br><br>
     * <h2> Thread Safety </h2>
     * This method is thread safe with respect to the repo.
     * <br> All read and write operations
     * engage the repo lock
     * <br><br>
     * @param storyId unique primary key ID of story to which to add a like
     * @return success of the operation or a fail code
     * */
    @Override
    @NotNull
    public Response likeStory(int storyId) {
        lock.lock();
        Response res = null;
        for (StoryTableRow row : storyTable) {
            if (row.getStoryId() == storyId) {
                row.addLike();
                res = Response.getSuccessful("Like added to story with ID: " + storyId);
                break;
            }
        }
        lock.unlock();

        // If null, means story wasn't found
        if (res == null) {
            res = new Response(Response.ResCode.STORY_NOT_FOUND,
                    "A story with ID " + storyId + " doesn't exist");
        }
        return res;
    }

    /**
     * <br><br>
     * <h2> Thread Safety </h2>
     * This method is thread safe with respect to the repo.
     * <br> All read and write operations
     * engage the repo lock
     * <br><br>
     * @param story string content of a completed story
     * @param publishUnixTimeStamp the unix epoch in seconds (seconds since Jan 1970 or something)
     * @param authorDisplayNames a list of strings of only the display names of contributing players.
     *                           if null is passed, no authors will be saved
     * @return success of saving the story to the DB
     */
    @Override
    @NotNull
    public Response saveStory (String story, double publishUnixTimeStamp,
                                     @Nullable Set<String> authorDisplayNames) {
        if (authorDisplayNames == null) {
            authorDisplayNames = new HashSet<>();
        }

        lock.lock();
        storyTable.add(new StoryTableRow(
            story, publishUnixTimeStamp, authorDisplayNames.toArray(new String[0])
        ));
        lock.unlock();

        return Response.getSuccessful("Story successfully saved");
    }

    @Override
    public @NotNull RepoRes<StoryRepoData> getStoryById(int storyId) {
        RepoRes<StoryRepoData> repoRes = new RepoRes<>(new Response(Response.ResCode.STORY_NOT_FOUND,
                "Story with id " + storyId + " not found!"));

        lock.lock();
        for (StoryTableRow row: storyTable) {
            if (row.getStoryId() == storyId) {
                repoRes.addRow(new StoryRepoData(
                        row.getStoryId(), row.getStory(), row.getAuthors(),
                        // No idea what offset means, or nanoOfSecond. Just guessing here
                        LocalDateTime.ofEpochSecond((long) row.getPublishUnixTimestamp(),
                                0, ZoneOffset.UTC), row.getLikes()));

                repoRes.setResponse(Response.getSuccessful("Story found!"));
                break;
            }
        }
        lock.unlock();

        return repoRes;
    }
}
