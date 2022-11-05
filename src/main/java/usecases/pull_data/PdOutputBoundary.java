package usecases.pull_data;

/**
 *  Output boundary for Pull Data use-case
 *  Implemented by the Presenter
 */
public interface PdOutputBoundary {

    /**
     * Update some game data object in ViewModel using received game data
     * @param d PdOutputData
     */
    void updateGameInfo (PdOutputData d);

}
