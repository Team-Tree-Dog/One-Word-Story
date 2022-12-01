package usecases.suggest_title;

/**
 * Output Boundary for the Suggest Title use case.
 */
public interface StOutputBoundary {
    /**
     * Notifies the view model of the success of adding the suggested title to the database.
     * @param data The output data for the use case.
     */
    void suggestTitleOutput(StOutputData data);
}
