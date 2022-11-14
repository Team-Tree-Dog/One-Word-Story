package usecases.pull_game_ended;

/**
 * Data to pass to the output boundary
 */
public class PgeOutputData {

    public String[] playerIds;

    /**
     * Constructor for PgeOutputData
     * @param playerIds the player ids of players in the game that ended
     */
    public PgeOutputData(String[] playerIds) {
        this.playerIds = playerIds;
    }
}