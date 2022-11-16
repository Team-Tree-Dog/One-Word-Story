package usecases.submit_word;

/**
 * The boundary that passes the input data into the SwInteractor.
 */
public interface SwInputBoundary {

    void submitWord(SwInputData inputData);
}
