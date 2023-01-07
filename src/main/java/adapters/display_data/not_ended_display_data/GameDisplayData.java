package adapters.display_data.not_ended_display_data;

import org.jetbrains.annotations.NotNull;
import usecases.GameDTO;

/**
 * Data structure for game display data
 */
public class GameDisplayData {

    private final @NotNull PlayerDisplayData[] players;
    private final @NotNull PlayerDisplayData currentPlayerTurn;
    private final @NotNull String storyString;
    private final int secondsLeftInTurn;

    /**
     * Constructor for GameDisplayData
     * @param players the players in the "game"
     * @param currentPlayerTurn the player whose turn it currently is
     * @param storyString the story
     * @param secondsLeftInTurn the seconds left in the current turn
     */
    protected GameDisplayData(@NotNull PlayerDisplayData[] players,
                              @NotNull PlayerDisplayData currentPlayerTurn,
                              @NotNull String storyString, int secondsLeftInTurn) {
        this.players = players;
        this.currentPlayerTurn = currentPlayerTurn;
        this.storyString = storyString;
        this.secondsLeftInTurn = secondsLeftInTurn;
    }

    public @NotNull PlayerDisplayData[] getPlayers() { return players; }

    public @NotNull PlayerDisplayData getCurrentPlayerTurn() { return currentPlayerTurn; }

    public @NotNull String getStoryString() { return storyString; }

    public int getSecondsLeftInTurn() {return  secondsLeftInTurn; }

    /**
     * Factory method for making a GameDisplayData from GameDTO
     * @param gameData GameDTO object
     * @return new GameDisplayData from the GameDTO
     * @throws IllegalArgumentException if the passed GameDTO had some null fields which were missing
     * important game display information
     */
    public static GameDisplayData fromGameDTO(GameDTO gameData) throws IllegalArgumentException {
        return new GameDisplayDataBuilder()
                .addPlayersFromDTO(gameData.getPlayers(), gameData.getCurrentTurnPlayerId())
                .setStoryString(gameData.getStory())
                .setSecondsLeftInTurn(gameData.getSecondsLeftCurrentTurn())
                .build();
    }
}
