package entities.games;

import entities.Player;
import entities.statistics.PerPlayerIntStatistic;
import entities.validity_checkers.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A game, with the "regular" rules and constraints
 */
public class GameRegular extends Game {

    public static int REGULAR_GAME_SECONDS_PER_TURN = 15;
    private final Queue<Player> players;
    private static final PunctuationValidityChecker puncValidityCheckerRegular =
            new PunctuationValidityCheckerRegular();
    private static final WordValidityChecker wordValidityCheckerRegular =
            new WordValidityCheckerRegular();
    public static final ValidityCheckerFacade v = new ValidityCheckerFacade(
            puncValidityCheckerRegular, wordValidityCheckerRegular);

    /**
     * Constructor for GameRegular
     * @param initialPlayers The initial players in this GameRegular
     */
    public GameRegular(Queue<Player> initialPlayers, PerPlayerIntStatistic[] playerStats) {
        super(REGULAR_GAME_SECONDS_PER_TURN, v, playerStats);
        players = new LinkedList<>(initialPlayers);
    }

    @Override
    public @NotNull Collection<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Currently implemented as no-operation
     */
    @Override
    protected void onTimeUpdateLogic() {}

    /**
     * Gets Player from this game by its id
     * @param playerId ID of searched player
     * @return searched Player
     */
    @Override
    public Player getPlayerById(String playerId) {
        return players.stream().filter(p -> p.getPlayerId().equals(playerId)).findAny().orElse(null);
    }

    /**
     * Removes requested player from this game
     * @param playerToRemove The Player to be removed
     * @return success of failure as boolean
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
    protected boolean switchTurnLogic() {
        setSecondsLeftInCurrentTurn(getSecondsPerTurn());
        return players.add(players.remove());
    }

    /**
     * @return the first player in the player list
     */
    @Override
    @Nullable
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
