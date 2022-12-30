package adapters.display_data;

import util.RecursiveSymboledIntegerHashMap;

import java.io.Serializable;

/**
 * Data that needs to be sent to players who were still in the game when it ended
 */
public class GameEndPlayerDisplayData implements Serializable {

    private final String id;
    private final String displayName;
    private final RecursiveSymboledIntegerHashMap[] stats;

    /**
     * Constructor for GameEndPlayerDisplayData
     * @param id id of player
     * @param displayName display name of player
     * @param stats stats to display to player
     */
    public GameEndPlayerDisplayData(String id, String displayName,
                                    RecursiveSymboledIntegerHashMap[] stats) {
        this.id = id;
        this.displayName = displayName;
        this.stats = stats;
    }

    public String getId() { return id; }

    public String getDisplayName() { return displayName; }

    public RecursiveSymboledIntegerHashMap[] getStats() { return stats; }
}
