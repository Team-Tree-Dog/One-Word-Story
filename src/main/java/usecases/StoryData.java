package usecases;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class StoryData implements Comparable<StoryData> {

    private String name;
    private String story;
    private String[] authorNames;
    private LocalDateTime publishTimeStamp;

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

    public StoryData(String name, String[] authors, LocalDateTime dt1, String s1, int i) {

    }


    @Override
    public int compareTo(StoryData other) {
        return this.publishTimeStamp.compareTo(other.publishTimeStamp);
    }
}
