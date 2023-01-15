package usecases;

import entities.Player;
import entities.games.GameReadOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A data transfer object for a Game entity. Contains data from a Game
 * object which is necessary to display game state information to client players.
 * This object represents a single snapshot of the game state and becomes outdated
 * over time as the game progresses.
 */
public class GameDTO {

    private final String story;
    private final ArrayList<PlayerDTO> players;
    private final String currentTurnPlayerId;
    private final int secondsLeftCurrentTurn;

    /**
     * Create a game data transfer object from a Game Entity. Converts player collection
     * to an arraylist of playerDTO objects
     * @param story String of the current full story of the game entity being represented
     * @param players Collection of players from the game
     * @param currentTurnPlayerId id of player whose turn it is in the game
     * @param secondsLeftCurrentTurn seconds remaining in current turn
     */
    public GameDTO (String story, Collection<Player> players,
                    String currentTurnPlayerId, int secondsLeftCurrentTurn) {
        this.story = story;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.secondsLeftCurrentTurn = secondsLeftCurrentTurn;

        this.players = new ArrayList<>();
        for (Player p: players) {
            this.players.add(new PlayerDTO(p.getDisplayName(), p.getPlayerId()));
        }
    }

    /**
     * @return String of the story in this game state data
     */
    public String getStory() { return story; }

    /**
     * @return Seconds left in current turn in this game state data
     */
    public int getSecondsLeftCurrentTurn() { return secondsLeftCurrentTurn; }

    /**
     * @return Id of player whose turn it is in this game state data
     */
    public String getCurrentTurnPlayerId() { return currentTurnPlayerId; }

    /**
     * @return List of players who were in the game in this game state data
     */
    public List<PlayerDTO> getPlayers() { return players; }

    /**
     * Convenience method for building a GameDTO from a Game Object
     * <br>
     * BE SURE TO LOCK THE GAME if you are passing the game object directly
     * @param game Game entity object to build DTO from
     * @return GameDTO built from provided game
     */
    @NotNull
    public static GameDTO fromGame (@NotNull GameReadOnly game) {
        return new GameDTO(
                game.getStoryString(),
                game.getPlayers(),
                game.getCurrentTurnPlayer() == null ? "" : game.getCurrentTurnPlayer().getPlayerId(),
                game.getSecondsLeftInCurrentTurn());
    }

    @Override
    public String toString() {
        return "GameDTO('" + story + "', " + currentTurnPlayerId + ", "
                + secondsLeftCurrentTurn + ", " + players + ")";
    }
}
