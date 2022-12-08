package usecases.suggest_title;

/**
 * Defines the abstract method to begin the Suggest Title use case
 */
public interface StInputBoundary {
    /**
     * Abstract method to begin the Suggest Title use case
     * @param data the input data for the use case
     * @param pres output boundary for this use case
     */
    void suggestTitle(StInputData data, StOutputBoundary pres);
}
