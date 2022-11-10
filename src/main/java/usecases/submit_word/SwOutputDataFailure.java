package usecases.submit_word;

import usecases.Response;

/**
 * The data that the ViewModel uses to update the GUI if the word is determined to be invalid.
 */
public class SwOutputDataFailure {

    /**
     * ID of the player that attempted to submit.
     */
    private String playerId;

    /**
     * Result Code.
     */
    private Response response;

    /**
     * Constructor.
     * @param playerId Player ID
     * @param response Result Code.
     */
    public SwOutputDataFailure(String playerId, Response response) {
        this.playerId = playerId;
        this.response = response;
    }

    public Response getResponse() {return this.response;}

    public String getPlayerId() {return this.playerId;}
}
