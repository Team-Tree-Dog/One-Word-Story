package usecases.suggest_title;

import usecases.like_story.LsGatewayInputData;

/**
 * Interface for the repository to suggest a title for the given story.
 */
public interface StGateway {
    /**
     * Abstract method for the repository to add 1 like to the given story
     * @param d
     */
    StGatewayOutputData suggestTitle(StGatewayInputData d);
}
