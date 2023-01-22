package net.onewordstory.core.entities;

import net.onewordstory.core.entities.games.Game;

import java.util.concurrent.locks.Lock;

/**
 * Used by lobby manager to track objects which added a player to the
 * pool and are waiting for a response.
 */
public interface PlayerPoolListener {
    /**
     * Called by sortPlayers thread when a player has been sorted into
     * a game
     * @param game The game that the player has been sorted into
     */
    void onJoinGamePlayer (Game game);

    /**
     * Called if the player cancelled their waiting by the cancelPlayer
     * method in LobbyManager
     */
    void onCancelPlayer ();

    /**
     * This method can be called by other classes to lock some shared data inside critical section.
     * Notice that this "enforces" every implementation of this interface to use thread
     */
    Lock getLock();

}
