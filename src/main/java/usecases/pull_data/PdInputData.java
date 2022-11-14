package usecases.pull_data;

import entities.games.Game;

/**
 * Input data class of Pull Data use-case
 */
public class PdInputData {

    private final Game game;

    /**
     * Constructor for PdInputData
     * @param game Game to be stored
     */
    public PdInputData(Game game) { this.game = game; }

    /**
     * Getter for PdInputData
     * @return corresponding game state
     */
    public Game getGame() { return this.game; }

}
