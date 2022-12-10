package com.example.springapp;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import adapters.view_models.DcViewModel;
import adapters.view_models.JplViewModel;
import adapters.view_models.SwViewModel;
import com.example.springapp.controllers.Log;
import frameworks_drivers.views.View;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import usecases.Response;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler logic for game websocket!
 */
public class SocketTextHandler extends TextWebSocketHandler {
    /**
     * Reference to abstract View class in core project which gives access
     * to all the use case controllers, as well as the PGE and PD view models
     * which contain game data
     */
    private final View viewRef;

    /**
     * @param viewRef Reference to view object with use case controllers
     */
    public SocketTextHandler(View viewRef) {
        this.viewRef = viewRef;
    }

    /**
     * Stores information about a particular connected client and their corresponding
     * player information, such as their display name, player id, and progress
     * through the matchmaking process
     */
    private static class PlayerState {
        public String playerId;
        public String displayName = null;
        public JplViewModel jplViewM = null;

        /**
         * @param playerId Create a new unique ID never used before to identify this client
         *                 (UUID recommended)
         */
        public PlayerState(String playerId) {
            this.playerId = playerId;
        }
    }

    /**
     * Maps the unique connected player's session ID to their corresponding player object.
     * <br><br>
     * <b>Note</b> that the session ID is DIFFERENT from the PlayerState.playerId.
     */
    private final static Map<String, PlayerState> sessionToPlyId = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionToPlyId.put(session.getId(), new PlayerState(UUID.randomUUID().toString()));
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        PlayerState p = sessionToPlyId.get(session.getId());

        // Calls disconnect on a thread. Response shouldn't matter, player has disconnected!
        DcViewModel viewM = viewRef.dcController.disconnect(p.playerId);

        // Waits for view model data TODO: FIX VIEW MODELS
        while (viewM.getResponseCode() == null) {
            Thread.sleep(20);
        }

        // Prints DC output
        if (viewM.getResponseCode() == Response.ResCode.SUCCESS) {
            Log.sendSocketSuccess("CLOSED",
                    viewM.getResponseCode() + " " + viewM.getResponseMessage());
        } else {
            Log.sendSocketError("CLOSED",
                    viewM.getResponseCode() + " " + viewM.getResponseMessage());
        }

        // Delete PlayerState object for this player
        sessionToPlyId.remove(session.getId());
    }

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
        try {
            String payload = message.getPayload();

            if (payload.isEmpty()) {
                return;
            }

            // Convert raw payload to command object
            ClientCommand incomingCmd = ClientCommand.parseCommand(payload);

            // Prepare response object
            ServerResponse response = null;

            // Check command type and call corresponding sub-handler.
            // Create response object with sub-handler output
            if(incomingCmd instanceof ClientCommand.TryJoin) {
                boolean result = tryJoin(session.getId(), ((ClientCommand.TryJoin) incomingCmd).playerName());
                response = new ServerResponse.JoinResponse(result);

            } else if(incomingCmd instanceof ClientCommand.StateUpdate) {
                GameDisplayData data = getCurrentGameState(session.getId());
                response = new ServerResponse.CurrentState(data);

            } else if(incomingCmd instanceof ClientCommand.SendWord) {
                newWord(session.getId(), ((ClientCommand.SendWord) incomingCmd).word());
            }

            Log.sendSocketGeneral("HANDLE CMD RECV", incomingCmd.toString());

            // Sends response, if there is one
            if(response != null) {
                // Pack response object into raw payload
                String msg = response.pack();

                Log.sendSocketGeneral("HANDLE PREP RES", msg);

                // Sends message to client
                session.sendMessage(new TextMessage(msg));
            }

        } catch (Exception e) {
            Log.sendSocketError("HANDLE", e.toString());
        }
    }

    /**
     * <h2>Sub Handler: JPL</h2>
     * A newly connected client must first tryJoin in order to continue to the game. All
     * other commands will be ignored until this one is successfully called.
     * <br><br>
     * Calls JPL with player's desired display name and returns a boolean indicating if
     * JPL's inPool response was a success (success if player was added to the pool and
     * their display name was valid)
     */
    boolean tryJoin(String sessionId, String playerName) throws InterruptedException {
        PlayerState dat = sessionToPlyId.get(sessionId);

        // Calls JPL
        JplViewModel jplViewM = viewRef.jplController.joinPublicLobby(
                dat.playerId, playerName);

        // Wait for initial inPool response
        while (jplViewM.getResponseCode() == null) {
            Thread.sleep(20);
        }

        if (jplViewM.getResponseCode() == Response.ResCode.SUCCESS) {
            // Player name was approved, move forward
            dat.displayName = playerName;
            dat.jplViewM = jplViewM;
            return true;
        } else {
            // Tell frontend to reload page and disconnect, display name was bad
            return false;
        }
        // TODO: Convert socket system to allow multi message sending
    }

    /**
     * <h2>Sub Handler: PD</h2>
     * Handler to retrieve current game state from PD view model. Called periodically by
     * all players in a game. returns null if player didn't pass tryJoin
     * <br><br>
     * Gets GameDisplayData from PD view model and returns it
     */
    GameDisplayData getCurrentGameState(String sessionId) {
        PlayerState p = sessionToPlyId.get(sessionId);
        if (p.displayName != null) {
            return viewRef.pdViewM.getCurrentGameState();
        } else {
            return null;
        }
    }

    /**
     * <h2>Sub Handler: SW</h2>
     * Handler to submit a word to the game. Called when a player
     * tries to submit a word to the game. ignores command if player didn't pass tryJoin
     * OR if the player was not yet sorted into the game
     * <br><br>
     * Gets GameDisplayData from PD view model and returns it
     */
    void newWord(String sessionId, String word) throws InterruptedException {
        PlayerState dat = sessionToPlyId.get(sessionId);

        if (dat.displayName != null && dat.jplViewM.getGameState() != null) {
            SwViewModel viewM = viewRef.swController.submitWord(dat.playerId, word);

            while (viewM.getResponseCode() == null) {
                Thread.sleep(20);
            }
        }

        // TODO: Ideally, return response code
    }
}
