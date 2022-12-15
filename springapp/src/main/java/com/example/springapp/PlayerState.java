package com.example.springapp;

import adapters.view_models.JplViewModel;
import org.springframework.web.socket.WebSocketSession;

/**
 * Stores information about a particular connected client and their corresponding
 * player information, such as their display name, player id, and progress
 * through the matchmaking process
 */
public class PlayerState {

    public enum State {
        /**
         * The player has successfully connected to the socket but has not
         * attempted to (JPL) join public lobby yet.
         * <br><br>
         * displayName and jplViewM will both be null
         */
        NOT_PROCESSED,
        /**
         * The player's display name has been rejected. This player should disconnect
         * since no further action will be taken on their part.
         * <br><br>
         * displayName and jplViewM will both be null
         */
        REJECTED,
        /**
         * Player's display name was accepted and the player is waiting in the pool.
         * <br><br>
         * displayName and jplViewM will both be set
         */
        IN_POOL,
        /**
         * Player has been added to a game
         * <br><br>
         * displayName and jplViewM will both be set
         */
        IN_GAME
    }

    private State state;
    private final String playerId;
    private String displayName;
    private JplViewModel jplViewM;
    private final WebSocketSession session;

    /**
     * @param playerId Create a new unique ID never used before to identify this client
     *                 (UUID recommended)
     */
    public PlayerState(String playerId, WebSocketSession session) {
        this.playerId = playerId;
        this.session = session;

        state = State.NOT_PROCESSED;
        displayName = null;
        jplViewM = null;
    }

    public State state() { return this.state; }
    public String playerId() { return this.playerId; }
    public String displayName() { return this.displayName; }
    public JplViewModel jplViewM() { return this.jplViewM; }
    public WebSocketSession session() { return this.session; }

    public void changeToRejected() {
        this.state = State.REJECTED;
    }

    public void changeToInPool(JplViewModel viewM, String displayName) {
        this.displayName = displayName;
        this.jplViewM = viewM;
        this.state = State.IN_POOL;
    }

    public void changeToInGame() {
        this.state = State.IN_GAME;
    }
}
