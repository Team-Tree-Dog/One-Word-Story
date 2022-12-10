package adapters.display_data.not_ended_display_data;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds GameDisplayData
 */
public class GameDisplayDataBuilder {

    private final List<PlayerDisplayData> players;
    private PlayerDisplayData curTurnPlayer;
    private int secondsLeftInTurn;
    private String storyString;

    /**
     * Initialize builder
     */
    public GameDisplayDataBuilder() {
        players = new ArrayList<>();
    }

    /**
     * @param id ID of player
     * @param displayName Display name if player
     * @param isCurrentTurnPlayer is it this players turn in the current game snapshot?
     * @return this builder
     */
    public GameDisplayDataBuilder addPlayer(String id, String displayName,
                                            boolean isCurrentTurnPlayer) {
        PlayerDisplayData ply = new PlayerDisplayData(id, displayName, isCurrentTurnPlayer);
        players.add(ply);
        if (isCurrentTurnPlayer) { curTurnPlayer = ply; }
        return this;
    }

    /**
     * @param secs time in seconds left in the current turn
     * @return this builder
     */
    public GameDisplayDataBuilder setSecondsLeftInTurn(int secs) {
        secondsLeftInTurn = secs;
        return this;
    }

    /**
     * @param story String of the story in the game currently
     * @return this builder
     */
    public GameDisplayDataBuilder setStoryString(String story) {
        storyString = story;
        return this;
    }

    /**
     * @return the built GameDisplayData object
     */
    public GameDisplayData build() {
        return new GameDisplayData(players.toArray(new PlayerDisplayData[0]),
                curTurnPlayer, storyString, secondsLeftInTurn);
    }
}
