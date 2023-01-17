package net.onewordstory.core.usecases.pull_data;

import net.onewordstory.core.usecases.GameDTO;

/**
 * Interactor for Pull Data use-case
 */
public class PdInteractor implements PdInputBoundary {

    private final PdOutputBoundary p;

    /**
     * Constructor for PdInteractor
     * @param p PdOutputBoundary used by this interactor
     */
    public PdInteractor (PdOutputBoundary p) { this.p = p; }

    /**
     * Forward update from input data to the output boundary
     * @param d PdInputData storing the updated game state
     */
    public void onTimerUpdate (PdInputData d) {
        GameDTO gameInfo = GameDTO.fromGame(d.getGame());
        PdOutputData od = new PdOutputData(gameInfo);
        p.updateGameInfo(od);
    }

}
