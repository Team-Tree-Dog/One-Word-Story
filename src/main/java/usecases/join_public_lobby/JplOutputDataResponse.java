package usecases.join_public_lobby;

import usecases.Response;

/**
 * Data to notify the server (view) that a player with a certain ID
 * has produced a particular Response. Used by two output methods, one which
 * uses it to indicate the success of joining the matchmaking pool, and the
 * other to indicate if a player cancelled.
 */
public class JplOutputDataResponse {
    private final Response res;
    private final String playerId;

    /**
     * @param res Response object
     * @param playerId ID of player to whom this response is referring.
     */
    public JplOutputDataResponse(Response res, String playerId) {
        this.res = res;
        this.playerId = playerId;
    }

    /**
     * @return Response object
     */
    public Response getRes() {
        return res;
    }

    /**
     * @return Player ID
     */
    public String getPlayerId() {
        return playerId;
    }
}
