package net.onewordstory.core.usecases.pull_game_ended;

import net.onewordstory.core.util.RecursiveSymboledIntegerHashMap;

/**
 * Used by PGE to notify the view model of players who are in a finished
 * game, along with their associated statistics
 */
public class PlayerStatisticDTO {

    private final String playerId;
    private final String displayName;
    private final RecursiveSymboledIntegerHashMap[] statData;

    /**
     *
     * @param playerId id of player
     * @param displayName Display name of player
     * @param statData An array of JSON-like recursive maps of String : SymboledInteger key
     *                 value pairs at the base case
     */
    public PlayerStatisticDTO(String playerId, String displayName,
                              RecursiveSymboledIntegerHashMap[] statData) {
        this.playerId = playerId;
        this.displayName = displayName;
        this.statData = statData;
    }

    public String getPlayerId() { return playerId; }

    public String getDisplayName() { return displayName; }

    public RecursiveSymboledIntegerHashMap[] getStatData() { return statData; }
}
