package usecases.get_all_titles;

/**
 * Input data for the get all titles use case that contains the ID of the story for which we want to get all titles.
 */
public class GatInputData {
    private int storyId;

    /**
     * Constructor for the input data
     * @param storyId   the ID of the story for which we want to get all titles
     */
    public GatInputData(int storyId){this.storyId = storyId;}

    public int getStoryId() {return storyId;}
}
