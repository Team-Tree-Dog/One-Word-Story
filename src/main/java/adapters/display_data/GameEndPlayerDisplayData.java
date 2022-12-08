package adapters.display_data;

/**
 * Data that needs to be sent to players who were still in the game when it ended
 */
public class GameEndPlayerDisplayData {

    public String id;
    public String displayName;
    public RecursiveSymboledIntegerHashmap[] stats;

    /**
     * Constructor for GameEndPlayerDisplayData
     * @param id id of player
     * @param displayName display name of player
     * @param stats stats to display to player
     */
    public GameEndPlayerDisplayData(String id, String displayName,
                                    RecursiveSymboledIntegerHashmap[] stats) {
        this.id = id;
        this.displayName = displayName;
        this.stats = stats;
    }

    public String getId() { return id; }

    public String getDisplayName() { return displayName; }

    public RecursiveSymboledIntegerHashmap[] getStats() { return stats; }
}
