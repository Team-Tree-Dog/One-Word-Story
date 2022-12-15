package com.example.springapp;
import adapters.view_models.DcViewModel;
import org.example.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import usecases.Response;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.springapp.SpringApp.coreAPI;

/**
 * Handler logic for game websocket!
 */
public class SocketTextHandler extends TextWebSocketHandler {

    /**
     * Maps the unique connected player's session ID to their corresponding player object.
     * <br><br>
     * <b>Note</b> that the session ID is DIFFERENT from the PlayerState.playerId.
     */
    private static final Map<String, PlayerState> sessionToPlyState = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionToPlyState.put(session.getId(), new PlayerState(UUID.randomUUID().toString(), session));
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        PlayerState p = sessionToPlyState.get(session.getId());

        // Calls disconnect on a thread. Response shouldn't matter, player has disconnected!
        DcViewModel viewM = coreAPI.dcController.disconnect(p.playerId());

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
        sessionToPlyState.remove(session.getId());
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
            ServerResponse response = incomingCmd.handler(sessionToPlyState.get(session.getId()));

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
            e.printStackTrace();
        }
    }
}
