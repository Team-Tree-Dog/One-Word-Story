package usecases.suggest_title;

/**
 * Gateway Output Data that contains the list of titles that have been previously suggested. This gateway output data
 * will be used to verify whether a title has already been suggested.
 * Not to be confused with StGatewayOutputDataSuccess, which records the success or failure of a particular
 * suggestion request.
 */
public class StGatewayOutputDataTitles {
    private String[] suggestedTitles;

    /**
     * Constructor for Titles Gateway Output Data.
     * @param suggestedTitles the list of titles that have already been suggested.
     */
    public StGatewayOutputDataTitles(String[] suggestedTitles){this.suggestedTitles = suggestedTitles;}

    public String[] getSuggestedTitles() {return suggestedTitles;}
}
