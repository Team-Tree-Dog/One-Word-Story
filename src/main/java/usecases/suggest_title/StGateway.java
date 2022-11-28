package usecases.suggest_title;

import usecases.like_story.LsGatewayInputData;

/**
 * Interface for the repository to suggest a title for the given story. The repository implements this interface.
 * The abstract method defined in this interface is called in the use case to suggest the story title, and the
 * output that is returned records the success or failure of adding the title to the database.
 */
public interface StGateway {
    /**
     * Abstract method for the repository to suggest a title for the story, and to return an Output Data object
     * that records the success or failure of adding this title to the database.
     * @param d the input data object that contains the details of the story and the suggested title
     * @return  a gateway output data object that records whether adding the suggested title to the story in the
     *          database was successful
     */
    StGatewayOutputData suggestTitle(StGatewayInputData d);
}
