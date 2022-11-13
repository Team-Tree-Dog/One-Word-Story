package usecases.pull_game_ended;

import entities.Player;

import java.util.List;

/**
 * Data to pass along when a game ends
 */
public class PgeInputData {

    public List<Player> players;

    /**
     * Constructor for PgeInputData
     * @param players the players in the game that ended
     */
    public PgeInputData(List<Player> players) {
        this.players = players;
    }
}
