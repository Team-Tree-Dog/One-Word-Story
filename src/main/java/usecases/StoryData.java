package usecases;

import java.sql.Date;
import java.sql.Timestamp;

public class StoryData implements Comparable<StoryData> {

    private String story;
    private String[] authorNames;
    private Date publishTimeStamp;


    @Override
    public int compareTo(StoryData other) {
        return this.publishTimeStamp.compareTo(other.publishTimeStamp);
    }
}
