package usecases;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public class StoryRepoData implements Comparable<StoryRepoData> {

    private final int storyId;
    private final String story;
    private final String[] authorNames;
    private final LocalDateTime publishTimeStamp;
    private final int numberOfLikes;

    /**
     * Constructor for StoryData
     * @param story a single story from the repo
     * @param authors authors of a particular story
     * @param dt1 published date
     * @param numLikes number of likes corresponding to a story
     */
    public StoryRepoData(int storyId, String story, String[] authors, LocalDateTime dt1, int numLikes) {
        this.storyId = storyId;
        this.story = story;
        this.authorNames = authors;
        this.publishTimeStamp = dt1;
        this.numberOfLikes = numLikes;
    }

    /**
     * Getter for StoryData
     * @return the story
     */
    public String getStory() { return story; }

    /**
     * Getter for StoryData
     * @return author names
     */
    public String[] getAuthorNames() { return authorNames; }

    /**
     * Getter for StoryData
     * @return published date
     */
    public LocalDateTime getPublishTimeStamp() { return publishTimeStamp; }

    /**
     * Getter for StoryData
     * @return likes the story has
     */
    public int getLikes() { return numberOfLikes; }

    /**
     * Getter for StoryData
     * @return id of story
     */
    public int getStoryId() { return storyId; }

    /**
     * @param other StoryData compared on publishTimeStamp
     * @return -ve, 0, +ve integer based on the difference of
     * published date of other and this
     */
    @Override
    public int compareTo(StoryRepoData other) {
        return other.publishTimeStamp.compareTo(this.publishTimeStamp);
    }
}
