package net.onewordstory.core.usecases.pull_data;

/**
 * Input Boundary Interface for Pull Data use-case.
 * Implemented by PdInteractor
 */
public interface PdInputBoundary {

    /**
     * Performs a timely update.
     * @param d PdInputData corresponding to the current game state
     */
    public void onTimerUpdate (PdInputData d);

}
