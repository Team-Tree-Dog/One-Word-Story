package usecases.get_most_liked_stories;

public class GmlsInputData {
    private final Integer lowerInclusive;
    private final Integer upperExclusive;

    /**
     * Constructor for GmlsInputData
     * @param lowerInclusive the lower inclusive bound for the range of stories sorted
     *                       in descending order by likes
     * @param upperExclusive the upper inclusive bound for the range of stories sorted
     *                       in descending order by likes
     */
    public GmlsInputData(Integer lowerInclusive, Integer upperExclusive){
        this.lowerInclusive = lowerInclusive;
        this.upperExclusive = upperExclusive;
    }

    public Integer getLowerInclusive(){
        return this.lowerInclusive;
    }

    public Integer getUpperExclusive(){ return this.upperExclusive; }
}
