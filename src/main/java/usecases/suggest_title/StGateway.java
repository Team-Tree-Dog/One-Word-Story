package usecases.suggest_title;

/**
 * Gateway interface implemented by the repository for this use case.
 * This interface contains an abstract method to suggest the story title, for which the
 * output that is returned records the success or failure of adding the title to the database.
 * This interface also contains an abstract method to retrieve all previously suggested story titles from the repo,
 * which is utilized in this use case to check whether the suggested title has not been previously suggested.
 */
public interface StGateway {
    /**
     * Abstract method for the repository to suggest a title for the story, and to return an Output Data object
     * that records the success or failure of adding this title to the database.
     * @param d the input data object that contains the details of the story and the suggested title
     * @return  a gateway output data object that records whether adding the suggested title to the story in the
     *          database was successful
     */
    StGatewayOutputDataSuccess suggestTitle(StGatewayInputDataSuggest d);

    /**
     * Abstract method to retrieve all previously suggested story titles from the repository, and to return these
     * titles in a Titles Gateway Output Data object that is defined for this use case
     * @param d The Input Data object containing the ID of the story for which we want to get all previously
     *          suggested titles
     * @return  A Gateway Output Data object that contains the previously suggested titles for the story
     *          corresponding to the Get Gateway Input Data d
     */
    StGatewayOutputDataTitles getAllTitles(StGatewayInputDataGet d);
}
