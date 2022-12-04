package usecases.suggest_title;

import usecases.shutdown_server.SsOutputBoundary;

/**
 * Defines the abstract method to begin the Suggest Title use case
 */
public interface StInputBoundary {
    /**
     * Abstract method to begin the Suggest Title use case
     * @param data the input data for the use case
     */
    void suggestTitle(StInputData data);
}
