package net.onewordstory.core.usecases.join_public_lobby;

import net.onewordstory.core.usecases.GameDTO;
import net.onewordstory.core.usecases.Response;

/**
 * Data used to notify server that player has joined a game
 */
public class JplOutputDataJoinedGame {

    private final Response res;
    private final String playerId;
    private final GameDTO gameData;

    /**
     * @param res Response object, should be success
     * @param playerId ID of player who joined the game
     * @param gameData Data of game that the player joined, or null if not a success
     */
    public JplOutputDataJoinedGame(Response res, String playerId, GameDTO gameData) {
        this.res = res;
        this.playerId = playerId;
        this.gameData = gameData;
    }

    /**
     * @return Response object
     */
    public Response getRes() { return res; }

    /**
     * @return Player ID
     */
    public String getPlayerId() { return playerId; }

    /**
     * @return Game data of the game this player has been added into,
     * or null if an error occurred
     */
    public GameDTO getGameData() { return gameData; }
}
