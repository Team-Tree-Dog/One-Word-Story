package usecases.pull_game_ended;

/**
 * Data to pass to the output boundary
 */
public class PgeOutputData {

    private final PlayerStatisticDTO[] playerStatDTOs;

    /**
     * Constructor for PgeOutputData
     * @param playerStatDTOs a list of DTO objects of a players and their statistics
     */
    public PgeOutputData(PlayerStatisticDTO[] playerStatDTOs) {
        this.playerStatDTOs = playerStatDTOs;
    }

    public PlayerStatisticDTO[] getPlayerStatDTOs() {
        return playerStatDTOs;
    }
}
