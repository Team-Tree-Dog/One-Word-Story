package entities.games;

import entities.Player;


import java.util.*;

/**
 * A game, with the "regular" rules and constraints
 */
public class GameRegular extends Game {


    public static int REGULAR_GAME_SECONDS_PER_TURN = 15;
    private final Queue<Player> players;

    /**
     * Constructor for GameRegular
     * @param initialPlayers The initial players in this GameRegular
     */
    public GameRegular(Queue<Player> initialPlayers) {
        super(REGULAR_GAME_SECONDS_PER_TURN, word -> {
            // Currently accepting all the words
            return true;
        });
        players = new LinkedList<>(initialPlayers);
    }

    @Override
    public Collection<Player> getPlayers() {
        return players;
    }

    /**
     * Currently implemented as no-operation
     */
    @Override
    public void onTimerUpdate() {

    }

    @Override
    public Player getPlayerById(String playerId) {
        return players.stream().filter(p -> p.getPlayerId().equals(playerId)).findAny().orElse(null);
    }

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
        setSecondsLeftInCurrentTurn(getSecondsPerTurn());
        return players.add(players.remove());
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
    public boolean isGameOver() {
        return players.size() < 2;
    }
}
