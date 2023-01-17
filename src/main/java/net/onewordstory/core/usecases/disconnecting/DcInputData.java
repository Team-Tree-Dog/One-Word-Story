package net.onewordstory.core.usecases.disconnecting;

/**
 * Input Data for Disconnecting Use Case
 */
public class DcInputData {

    private final String playerId;

    /**
     * Constructor for DcInputData
     * @param playerId id of player who needs to be disconnected
     */
    public DcInputData(String playerId) {this.playerId = playerId;}

    /**
     * @return id of player in this data
     */
    public String getPlayerId() {
        return playerId;
    }
}
