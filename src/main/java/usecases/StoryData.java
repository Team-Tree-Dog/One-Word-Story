package usecases;

import java.time.LocalDateTime;

public class StoryData implements Comparable<StoryData> {

    private final String name;
    private final String story;
    private final String[] authorNames;
    private final LocalDateTime publishTimeStamp;

    private final int numberOfLikes;

    public StoryData(String story, String[] authors, LocalDateTime dt1, String name, int i) {
        this.name = name;
        this.story = story;
        this.authorNames = authors;
        this.publishTimeStamp = dt1;
        this.numberOfLikes = i;
    }

    public String getStory() {
        return story;
    }

    public String[] getAuthorNames() {
        return authorNames;
    }

    public LocalDateTime getPublishTimeStamp() {
        return publishTimeStamp;
    }

    public String getName() {return name;}

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    @Override
    public int compareTo(StoryData other) {
        return other.publishTimeStamp.compareTo(this.publishTimeStamp);
    }
}
