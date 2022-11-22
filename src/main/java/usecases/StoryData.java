package usecases;

import java.time.LocalDateTime;

public class StoryData implements Comparable<StoryData> {

    private final String title;
    private final String story;
    private final String[] authorNames;
    private final LocalDateTime publishTimeStamp;
    private final int numberOfLikes;

    /**
     * Constructor for StoryData
     * @param story a single story from the repo
     * @param authors authors of a particular story
     * @param dt1 published date
     * @param title title of the story
     * @param i number of likes corresponding to a story
     */
    public StoryData(String story, String[] authors, LocalDateTime dt1, String title, int i) {
        this.title = title;
        this.story = story;
        this.authorNames = authors;
        this.publishTimeStamp = dt1;
        this.numberOfLikes = i;
    }

    /**
     * Getter for StoryData
     * @return the story
     */
    public String getStory() {
        return story;
    }

    /**
     * Getter for StoryData
     * @return author names
     */
    public String[] getAuthorNames() {
        return authorNames;
    }

    /**
     * Getter for StoryData
     * @return published date
     */
    public LocalDateTime getPublishTimeStamp() {
        return publishTimeStamp;
    }

    /**
     * Getter for StoryData
     * @return title of story
     */
    public String getName() {return title;}

    /**
     * Getter for StoryData
     * @return likes the story has
     */
    public int getLikes() {
        return numberOfLikes;
    }

    /**
     * @param other StoryData compared on publishTimeStamp
     * @return -ve, 0, +ve integer based on the difference of
     * published date of other and this
     */
    @Override
    public int compareTo(StoryData other) {
        return other.publishTimeStamp.compareTo(this.publishTimeStamp);
    }
}
