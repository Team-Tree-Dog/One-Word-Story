package usecases.pull_data;

import usecases.GameDTO;

/**
 * Output data class of Pull Data use-case
 */
public class PdOutputData {

    private final GameDTO gameInfo;

    /**
     * Constructor for PdOutputData
     * @param gameInfo GameDTO representing information about the game state to be processed
     */
    public PdOutputData (GameDTO gameInfo) { this.gameInfo = gameInfo; }

    /**
     * Getter for PdOutPutData
     * @return GameDTO corresponding game information
     */
    public GameDTO getGameInfo() { return this.gameInfo; }

}
