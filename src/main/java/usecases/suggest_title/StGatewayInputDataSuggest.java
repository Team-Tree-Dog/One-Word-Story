package usecases.suggest_title;

/**
 * The Gateway Input Data Object that contains
 * 1. the ID of the story for which the user suggests a title
 * 2. the suggested title for this particular story
 * Not to be confused with class StGatewayInputDataGet, which we use when retrieving all previously suggested titles
 * for a given story.
 */
public class StGatewayInputDataSuggest {
    private int storyID;
    private String titleSuggestion;

    /**
     * The constructor for this Gateway Input Data Object
     * @param storyID           the ID for the story whose title is suggested by the user
     * @param titleSuggestion   the title that the user suggests for the story with ID storyID
     */
    public StGatewayInputDataSuggest(int storyID, String titleSuggestion){
        this.storyID = storyID;
        this.titleSuggestion = titleSuggestion;
    }

    public int getStoryID() {return storyID;}

    public String getTitleSuggestion() {return titleSuggestion;}
}
