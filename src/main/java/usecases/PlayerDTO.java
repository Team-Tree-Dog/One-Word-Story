package usecases;

/**
 * Stores data necessary to identify and display a Player entity
 */
public class PlayerDTO {

    private final String displayName;
    private final String playerId;

    /**
     * Create a data transfer object from a Player entity's display name and id
     */
    public PlayerDTO(String displayName, String playerId) {
        this.displayName = displayName;
        this.playerId = playerId;
    }

    /**
     * @return The display name of the player represented by this data transfer object
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return The id of the player represented by this data transfer object
     */
    public String getPlayerId() {
        return playerId;
    }
}
