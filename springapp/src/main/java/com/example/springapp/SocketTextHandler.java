package com.example.springapp;
import adapters.display_data.not_ended_display_data.GameDisplayData;
import adapters.view_models.DcViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import usecases.Response;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.springapp.SpringApp.coreAPI;

/**
 * Handler logic for game websocket!
 */
public class SocketTextHandler extends TextWebSocketHandler {

    /**
     * Injects into PD a lambda which will broadcast the new PD content to all
     * clients
     */
    public SocketTextHandler() {
        coreAPI.pdViewM.injectCallback((GameDisplayData gameData) -> {
            try {
                Log.sendSocketGeneral("PD Callback", "Broadcasting new Game State");
                broadcast((new ServerResponse.CurrentState(gameData, false, true)).pack());
            } catch (JsonProcessingException e) {
                Log.sendSocketError("PD Callback", "Failed to process JSON");
            }

        });
    }

    /**
     * Maps the unique connected player's session ID to their corresponding player object.
     * <br><br>
     * <b>Note</b> that the session ID is DIFFERENT from the PlayerState.playerId.
     */
    private static final Map<String, PlayerState> sessionToPlyState = new ConcurrentHashMap<>();

    /**
     * Send a message to all clients <br><br>
     * Note that since WebSocket sessions are not concurrent with respect
     * to sending, a lock is engaged for each client before sending.
     */
    public void broadcast(String message) {
        for (PlayerState p: sessionToPlyState.values()) {
            try {
                Log.sendSocketGeneral("Broadcast", "Sending '"+message+"' to " + p.displayName());
                p.sendMessage(message);
            }
            // It is likely this occurs when the player has disconnected and the .values()
            // has stale information
            catch (IOException ignored) {
                Log.sendSocketError("Broadcast",
                        "Client " + p.displayName() + " triggered IOException when sending broadcast");
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionToPlyState.put(session.getId(), new PlayerState(UUID.randomUUID().toString(), session));
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        PlayerState p = sessionToPlyState.get(session.getId());

        // Delete PlayerState object from map so no send calls can be made
        sessionToPlyState.remove(session.getId());

        // Calls disconnect on a thread. Response shouldn't matter, player has disconnected!
        DcViewModel viewM = coreAPI.dcController.disconnect(p.playerId());

        // Waits for view model data TODO: FIX VIEW MODELS
        while (viewM.getResponse() == null) {
            Thread.sleep(20);
        }

        // Broadcasts new game data to clients if this player was disconnected from game
        if (viewM.getGameData() != null) {
            Log.sendSocketGeneral("DC Broadcast",
                    "Broadcasting new Game State; " + p.displayName() + " disconnected from game!");
            broadcast((new ServerResponse.CurrentState(
                    viewM.getGameData(), false, true)).pack());
        }

        // Prints DC output
        if (viewM.getResponse().getCode() == Response.ResCode.SUCCESS) {
            Log.sendSocketSuccess("CLOSED", viewM.getResponse().toString());
        } else {
            Log.sendSocketError("CLOSED", viewM.getResponse().toString());
        }


    }

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
        try {
            String payload = message.getPayload();

            if (payload.isEmpty()) {
                return;
            }

            // Convert raw payload to command object. Throws error if the payload isn't
            // a known type
            ClientCommand incomingCmd = ClientCommand.parseCommand(payload);
            Log.sendSocketGeneral("HANDLE CMD RECV", incomingCmd.toString());

            // Call command handler to get corresponding server response
            ServerResponse[] responses = incomingCmd.handler(sessionToPlyState.get(session.getId()));

            // Sends responses
            for (ServerResponse response : responses) {
                if (response != null) {
                    // Pack response object into raw payload
                    String msg = response.pack();

                    Log.sendSocketGeneral("HANDLE PREP RES", msg);

                    // Thread safely either broadcast to ALL clients, or to THIS client
                    if (response.isBroadcast()) {
                        broadcast(msg);
                    } else {
                        PlayerState p = sessionToPlyState.get(session.getId());
                        p.sendMessage(msg);
                    }
                }
            }

        } catch (Exception e) {
            Log.sendSocketError("HANDLE", e.toString());
            e.printStackTrace();
        }
    }
}
