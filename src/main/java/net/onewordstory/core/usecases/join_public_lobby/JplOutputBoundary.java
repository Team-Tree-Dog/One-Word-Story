package net.onewordstory.core.usecases.join_public_lobby;

import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

/**
 * Methods needed for an object to be able to accept output from this use case
 */
public interface JplOutputBoundary extends SsOutputBoundary {

    /**
     * Called with success code when player has been added to the matchmaking pool,
     * or with an error code if something went wrong. Respond accordingly
     * @param dataJoinedPool Data that associates a player with a response
     */
    void inPool (JplOutputDataResponse dataJoinedPool);

    /**
     * Called with success code when player was matched and added into a game
     * @param dataJoinedGame Data that provides a player ID and the game that player joined
     */
    void inGame (JplOutputDataJoinedGame dataJoinedGame);

    /**
     * Called with a success code when player cancelled their waiting in the pool
     * @param dataCancelled Data that associates a player with a response
     */
    void cancelled (JplOutputDataResponse dataCancelled);

}
