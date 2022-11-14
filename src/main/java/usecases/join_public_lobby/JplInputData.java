package usecases.join_public_lobby;

/**
 * Contains the parameters necessary to identify and create
 * a player object in the game corresponding to the client
 * who wishes to join
 */
public class JplInputData {

    private final String displayName;
    private final String id;

    /**
     * @param displayName Desired name, can be the same as someone else
     * @param id Unique identification of this player
     */
    public JplInputData(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    /**
     * @return display name
     */
    public String getDisplayName() { return displayName; }

    /**
     * @return unique identification
     */
    public String getId() { return id; }
}
