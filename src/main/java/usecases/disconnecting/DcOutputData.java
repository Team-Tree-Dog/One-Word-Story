package usecases.disconnecting;

import usecases.Response;

/**
 * Output Data for Disconnecting Use Case. A fail response is only given
 * if the player was not found. Otherwise, the player is guaranteed to have
 * been removed. In both cases, the player is guaranteed to be gone from
 * the entities after the disconnect call
 */
public class DcOutputData {
     private final Response response;
     private final String playerId;

    /**
     * Constructor for DcOutputData
     * @param response Response which reports whether disconnecting was successful or not
     */
    public DcOutputData(Response response, String playerId) {
         this.response = response;
         this.playerId = playerId;
     }

    /**
     * @return the response object in this data
     */
    public Response getResponse() {
        return response;
    }

    /**
     * @return associated player ID
     */
    public String getPlayerId() { return playerId; }
}
