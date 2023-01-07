package usecases.submit_word;
import usecases.GameDTO;
import usecases.Response;

/**
 * The data that the ViewModel uses to update the GUI if the word is determined to be valid.
 */
public class SwOutputDataValidWord {
    /**
     * Word that was submitted, represented as a string.
     */
    private final GameDTO gameData;

    /**
     * ID of the player that attempted to submit.
     */
    private final String playerId;

    /**
     * Response.
     */
    private final Response response;

    /**
     * Constructor.
     * @param gameData GameDTO of new gamestate so clients can update view with submitted word.
     * @param playerId Player ID.
     * @param response Result Code.
     */
    public SwOutputDataValidWord(GameDTO gameData, String playerId, Response response) {
        this.gameData = gameData;
        this.playerId = playerId;
        this.response = response;
    }

    public String getPlayerId() {return this.playerId;}

    public GameDTO getGameData() {return this.gameData;}

    public Response getResponse() {return response;}
}
