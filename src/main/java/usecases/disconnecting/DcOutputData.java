package usecases.disconnecting;

import org.jetbrains.annotations.Nullable;
import usecases.GameDTO;
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
     private final GameDTO gameData;

    /**
     * Constructor for DcOutputData
     * @param response Response which reports whether disconnecting was successful or not
     * @param gameData Game data after disconnecting player from game. Null if player was removed from
     *                 pool or otherwise not found
     * @param playerId the ID of the player
     */
    public DcOutputData(Response response, String playerId, @Nullable GameDTO gameData) {
         this.response = response;
         this.playerId = playerId;
         this.gameData = gameData;
     }

    /**
     * Constructor for DCOutputData when player was not found in the game (instead found in pool or not
     * found at all). Sets game data to null
     * @param response Response which reports whether disconnecting was successful or not
     * @param playerId the ID of the player
     */
    public DcOutputData(Response response, String playerId) {
        this(response, playerId, null);
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

    /**
     * Null when: <br>
     * <ol>
     *     <li> DC hasn't finished executing so data not set </li>
     *     <li> Player was found in the pool and removed from pool </li>
     *     <li> Player was not found anywhere in the entities so an error code is sent </li>
     * </ol>
     * @return game data after disconnecting the player from the game.
     */
    @Nullable
    public GameDTO getGameData() { return gameData; }
}
