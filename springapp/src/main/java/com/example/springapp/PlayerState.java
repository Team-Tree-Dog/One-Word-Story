package com.example.springapp;

import adapters.view_models.JplViewModel;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        NOT_PROCESSED(0),
        /**
         * The player's display name has been rejected. This player should disconnect
         * since no further action will be taken on their part.
         * <br><br>
         * displayName and jplViewM will both be null
         */
        REJECTED(1),
        /**
         * Player's display name was accepted and the player is waiting in the pool.
         * <br><br>
         * displayName and jplViewM will both be set
         */
        IN_POOL(2),
        /**
         * Player has been added to a game
         * <br><br>
         * displayName and jplViewM will both be set
         */
        IN_GAME(3),
        /**
         * Player has been removed from the entities and is now disconnected, frontend should
         * remove this player
         * <br><br>
         * displayName and jplViewM will both be set
         */
        DISCONNECTED(4);

        final int level;
        State(int level) {this.level = level;}
    }

    private State state;
    private final String playerId;
    private String displayName;
    private JplViewModel jplViewM;
    private final WebSocketSession session;
    private final Lock lock;
    private final Lock sendingLock;

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

        lock = new ReentrantLock();
        sendingLock = new ReentrantLock();
    }

    public State state() { lock.lock(); State out = this.state; lock.unlock(); return out; }
    public String playerId() { return this.playerId; }
    public String displayName() { lock.lock(); String out = this.displayName; lock.unlock(); return out; }
    public JplViewModel jplViewM() { lock.lock(); JplViewModel out = this.jplViewM; lock.unlock(); return out; }

    /**
     * Thread-Safely send a message to this client's session object. (Since sending
     * cannot be done concurrently)
     * @param payload string content to send
     */
    public void sendMessage(String payload) throws IOException {
        sendingLock.lock();
        session.sendMessage(new TextMessage(payload));
        sendingLock.unlock();
    }

    public void changeToRejected() {
        this.state = State.REJECTED;
    }

    public void changeToInPool(JplViewModel viewM, String displayName) {
        lock.lock();
        this.displayName = displayName;
        this.jplViewM = viewM;
        this.state = State.IN_POOL;
        lock.unlock();
    }

    public void changeToInGame() {
        lock.lock();
        this.state = State.IN_GAME;
        lock.unlock();
    }

    public void changeToDisconnected() {
        lock.lock();
        this.state = State.DISCONNECTED;
        lock.unlock();
    }
}
