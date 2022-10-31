package entities;

import entities.games.Game;

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
}
