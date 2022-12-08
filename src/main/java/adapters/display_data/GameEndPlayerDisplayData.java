package adapters.display_data;

import util.RecursiveSymboledIntegerHashMap;

/**
 * Data that needs to be sent to players who were still in the game when it ended
 */
public class GameEndPlayerDisplayData {

    private String id;
    private String displayName;
    private RecursiveSymboledIntegerHashMap[] stats;

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
