package usecases.get_all_titles;

/**
 * The input boundary for the get all titles use case. It is used to initiate the use case.
 */
public interface GatInputBoundary {

    /**
     * Abstract method to begin the get all titles use case
     * @param data  the input data for this use case
     */
    void getAllTitles(GatInputData data);
}
