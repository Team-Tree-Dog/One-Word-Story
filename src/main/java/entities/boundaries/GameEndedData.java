package entities.boundaries;

import entities.Player;
import java.util.List;

/**
 * Data that will be passed along when a game ends (e.g statistics).
 * Includes the players which are in the game which need to be notified that the game ended.
 *
 */
public class GameEndedData {

    private final List<Player> players;

    /**
     * Constructor.
     *
     * @param playersNew the list of Players at the end of the Game.
     */
    public GameEndedData(List<Player> playersNew) {this.players = playersNew;}

}
