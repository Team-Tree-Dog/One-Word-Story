package adapters.display_data;

import java.util.List;

/**
 * Builds GameDisplayData
 */
public class GameDisplayDataBuilder {

    private List<PlayerDisplayData> players;
    private PlayerDisplayData curTurnPlayer;
    private int secondsLeftInTurn;
    private String storyString;

    /**
     * Constructor for GameDisplayDataBuilder
     * @param players list of PlayerDisplayData
     * @param curTurnPlayer the "player" whose turn it currently is
     * @param secondsLeftInTurn the seconds left in the turn
     * @param storyString
     */
    public GameDisplayDataBuilder(List<PlayerDisplayData> players, PlayerDisplayData curTurnPlayer,
                                  int secondsLeftInTurn, String storyString) {
        this.players = players;
        this.curTurnPlayer = curTurnPlayer;
        this.secondsLeftInTurn = secondsLeftInTurn;
        this.storyString = storyString;
    }
}
