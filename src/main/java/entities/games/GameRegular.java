package entities.games;

import entities.Player;
import entities.ValidityChecker;
import entities.boundaries.GameEndedBoundary;
import entities.boundaries.OnTimerUpdateBoundary;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * A game, with the "regular" rules and constraints
 */
public class GameRegular extends Game {

    private LinkedList<Player> players = new LinkedList<Player>();

    /**
     * Constructor for GameRegular
     * @param initialPlayers The initial players in this GameRegular
     * @param otub The timer update boundary
     * @param geb The game ended boundary
     */
    public GameRegular(List<Player> initialPlayers, OnTimerUpdateBoundary otub,
                       GameEndedBoundary geb) {
        super(initialPlayers, otub, geb, 15, new ValidityChecker() {
            @Override
            public boolean isValid(String word) {
                //TODO: delete this lol
                return true;
            }
        });
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Does PUT_THINGS_HERE when the timer updates
     */
    @Override
    protected void onTimerUpdate() {}

    /**
     * @param PlayerId The unique id of the player to search for in this GameRegular instance.
     * @return The Player with the corresponding PlayerId, or null if this player does not exist
     */
    @Override
    public Player getPlayerById(String PlayerId) {
        for (Player player : this.getPlayers()) {
            if (Objects.equals(player.getPlayerId(), PlayerId)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Removes the player specified from this GameRegular instance
     * @param playerToRemove The Player to be removed
     * @return if the player was successfully removed
     */
    @Override
    public boolean removePlayer(Player playerToRemove) {
        return players.remove(playerToRemove);
    }

    /**
     * Adds the player specified to this GameRegular instance
     * @param playerToAdd The Player to be added
     * @return if the player was successfully added
     */
    @Override
    public boolean addPlayer(Player playerToAdd) {
        return players.add(playerToAdd);
    }

    /**
     * Moves the player whose turn it currently is from the front of the list of players to the back
     * It is now the new player in the front's turn
     * @return if the turn switch was successful
     */
    @Override
    public boolean switchTurn() {
        return players.add(players.remove());
    }

    /**
     * Modifies the current amount of seconds left in current turn to the amount of seconds per turn
     */
    @Override
    public void modifyTurnTime() {
        this.setSecondsLeftInCurrentTurn(getSecondsPerTurn());
    }

    /**
     * @return the first player in the player list
     */
    @Override
    public Player getCurrentTurnPlayer() {
        return players.peek();
    }

    /**
     * Checks if the amount of players in the game is less than 2, which means the game is over
     * @return if the game is over
     */
    @Override
    protected boolean isGameOver() {
        return players.size() < 2;
    }
}
