package com.example.springapp;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import adapters.view_models.JplCallback;
import adapters.view_models.JplViewModel;
import adapters.view_models.SwViewModel;
import org.example.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.Response;

import java.io.IOException;

import static com.example.springapp.SpringApp.coreAPI;

/**
 * Unifies a structure of all the possible INCOMING (RECEIVE) messages from clients.
 * <br><br>
 * Each response consists of a response header "code", followed by a separator, followed
 * by data content of the message.
 * <br><br>
 * For example, try_join is a request from a client to join a game with a particular
 * display name. The body of the message will contain their desired name
 * <br><br>
 * The format of the messages are agreed upon
 * between client side JS code and this backend Java code
 */
public sealed interface ClientCommand {
    String CMD_TRY_JOIN = "JPL";
    String CMD_SEND_WORD = "SW";

    char SEPARATOR = 30;

    /**
     * @param playerState player state object, duh
     * @return ServerResponses corresponding to the command, or empty array if this command
     * has no response (one way)
     * @throws InterruptedException If thread is interrupted
     */
    @NotNull
    ServerResponse[] handler(PlayerState playerState) throws InterruptedException;

    /**
     * JPL Command for initial joining
     * @param playerName desired display name of player who is trying to join
     */
    record JoinPublicLobby(String playerName) implements ClientCommand {
        /**
         * <h2>Sub Handler: JPL</h2>
         * A newly connected client must first tryJoin in order to continue to the game. All
         * other commands will be ignored until this one is successfully called.
         * <br><br>
         * Calls JPL with player's desired display name and returns a boolean indicating if
         * JPL's inPool response was a success (success if player was added to the pool and
         * their display name was valid)
         */
        @Override
        public ServerResponse[] handler(PlayerState playerState) throws InterruptedException {
            // Calls JPL
            JplViewModel jplViewM = coreAPI.jplController.joinPublicLobby(
                    playerState.playerId(), playerName);

            // Wait for properties to be set
            Response res = jplViewM.getResponseAwaitable().await();
            String playerId = jplViewM.getPlayerIdAwaitable().await();

            if (res.getCode() == Response.ResCode.SUCCESS) {
                // Player name was approved, move forward
                playerState.changeToInPool(jplViewM, playerName);

                // Inject callback to wait for further info
                jplViewM.injectCallback((hasCancelled, gameData) -> {
                    if (gameData != null) {
                        playerState.changeToInGame();
                        // Send initial game state info to notify player they were added to the game
                        try {
                            playerState.sendMessage((new ServerResponse.CurrentState(gameData,
                                    true, false, null)).pack());

                            Log.sendSocketGeneral("JPL Callback",
                                    "Initial Game Data sent to " + playerState.displayName());
                        } catch (IOException ignored) {
                            Log.sendSocketError("JPL Callback",
                                    "Client " + playerState.displayName() + " triggered IOException");
                        }
                    }
                });
            } else {
                playerState.changeToRejected();
            }

            return new ServerResponse[]{
                    new ServerResponse.JoinResponse(res, playerId, false, null)
            };
        }
    }

    /**
     * A player's command to try to submit a word to the game
     * @param word Punctuation and word that player wants to submit
     */
    record SubmitWord(String word) implements ClientCommand {
        /**
         * <h2>Sub Handler: SW</h2>
         * Handler to submit a word to the game. Called when a player
         * tries to submit a word to the game. The command is still
         * processed even when the player isn't in the pool or game. SW
         * will take care of checking and failing that situation
         */
        @Override
        public ServerResponse[] handler(PlayerState playerState) throws InterruptedException {
            SwViewModel viewM = coreAPI.swController.submitWord(playerState.playerId(), word);

            // Res is always set last, and gameData isnt guaranteed to be set, hence we do this:
            Response res = viewM.getResponseAwaitable().await();
            GameDisplayData gameData = viewM.getGameDataAwaitable().get();

            return new ServerResponse[]{
                    new ServerResponse.SubmitWordResponse(res, false, null),
                    new ServerResponse.CurrentState(gameData, false, true,
                            (p) -> p.state() == PlayerState.State.IN_GAME) // Broadcast new gamestate to those in game
            };
        }
    }

    /**
     * Parse raw payload into a client command object.
     * This is a factory
     * @param payload content received from a client over the websocket
     * @return a parsed client command object
     */
    static ClientCommand parseCommand(String payload) {

        // Splits the payload into COMMAND + BODY by the agreed separator
        String[] payloadBlocks = payload.split(Character.toString(SEPARATOR));

        // Switch on the command header
        return switch (payloadBlocks[0]) {
            case CMD_TRY_JOIN -> new JoinPublicLobby(payloadBlocks.length > 1 ? payloadBlocks[1] : "");
            case CMD_SEND_WORD -> new SubmitWord(payloadBlocks.length > 1 ? payloadBlocks[1] : "");

            // Crash if client sent a command which isn't recognized by the server
            default -> throw new UnsupportedOperationException("Invalid parameter: " + payloadBlocks[0]);
        };
    }
}
