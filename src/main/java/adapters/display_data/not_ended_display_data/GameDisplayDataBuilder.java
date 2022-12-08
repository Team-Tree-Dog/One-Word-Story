package adapters.display_data.not_ended_display_data;

import java.util.List;

/**
 * Builds GameDisplayData
 */
public class GameDisplayDataBuilder {

    private List<PlayerDisplayData> players;
    private PlayerDisplayData curTurnPlayer;
    private int secondsLeftInTurn;
    private String storyString;

    public GameDisplayDataBuilder addPlayer(String id, String displayName,
                                            boolean isCurrentTurnPlayer) {
        PlayerDisplayData ply = new PlayerDisplayData(id, displayName, isCurrentTurnPlayer);
        players.add(ply);
        if (isCurrentTurnPlayer) { curTurnPlayer = ply; }
        return this;
    }

    public GameDisplayDataBuilder setSecondsLeftInTurn(int secs) {
        secondsLeftInTurn = secs;
        return this;
    }

    public GameDisplayDataBuilder setStoryString(String story) {
        storyString = story;
        return this;
    }

    public GameDisplayData build() {
        return new GameDisplayData(players.toArray(new PlayerDisplayData[0]),
                curTurnPlayer, storyString, secondsLeftInTurn);
    }
}
