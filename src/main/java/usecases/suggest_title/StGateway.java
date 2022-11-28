package usecases.suggest_title;

import usecases.like_story.LsGatewayInputData;

/**
 * Interface for the repository to add 1 like to the given story
 */
public interface StGateway {
    /**
     * Abstract method for the repository to add 1 like to the given story
     */
    StGatewayInputData suggestTitle(LsGatewayInputData d);
}
