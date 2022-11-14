package usecases.disconnecting;

/**
 * Input Data for Disconnecting Use Case
 */
public class DcInputData {

    public String playerId;

    /**
     * Constructor for DcInputData
     * @param playerId id of player who needs to be disconnected
     */
    public DcInputData(String playerId) {this.playerId = playerId;}
}
