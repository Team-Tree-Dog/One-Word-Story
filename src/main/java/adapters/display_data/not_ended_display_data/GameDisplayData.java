package adapters.display_data.not_ended_display_data;

import org.jetbrains.annotations.NotNull;

/**
 * Data structure for game display data
 */
public class GameDisplayData {

    private @NotNull PlayerDisplayData[] players;
    private @NotNull PlayerDisplayData currentPlayerTurn;
    private @NotNull String storyString;
    private int secondsLeftInTurn;

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

    private @NotNull PlayerDisplayData[] getPlayers() { return players; }

    private @NotNull PlayerDisplayData getCurrentPlayerTurn() { return currentPlayerTurn; }

    private @NotNull String getStoryString() { return storyString; }

    private int getSecondsLeftInTurn() {return  secondsLeftInTurn; }
}
