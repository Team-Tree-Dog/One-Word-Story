package usecases.suggest_title;
/**
 * The Gateway Input Data Object that contains the ID of the story for which we want to retrieve all previously
 * suggested titles
 * Not to be confused with class StGatewayInputDataSuggest, which contains the story ID and suggested title for a
 * particular suggestion request.
 */
public class StGatewayInputDataGet {
    private int storyID;

    /**
     * The constructor for this Gateway Input Data Object
     * @param storyID the ID of the story of which we want to get all previously suggested titles
     */
    public StGatewayInputDataGet(int storyID){this.storyID = storyID;}

    public int getStoryID() {return storyID;}
}
