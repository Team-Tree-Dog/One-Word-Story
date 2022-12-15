package com.example.springapp;

import adapters.view_models.JplViewModel;
import adapters.view_models.SwViewModel;
import org.jetbrains.annotations.Nullable;
import usecases.Response;

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
    String CMD_STATE_UPDATE = "state_update";
    String CMD_SEND_WORD = "SW";

    char SEPARATOR = 30;

    /**
     * @param playerState
     * @return ServerResponse corresponding to the command, or null if this command has no response (one way)
     * @throws InterruptedException If thread is interrupted
     */
    @Nullable
    ServerResponse handler(PlayerState playerState) throws InterruptedException;

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
        public ServerResponse handler(PlayerState playerState) throws InterruptedException {
            // Calls JPL
            JplViewModel jplViewM = coreAPI.jplController.joinPublicLobby(
                    playerState.playerId(), playerName);

            // Wait for initial inPool response
            while (jplViewM.getResponse() == null) {
                Thread.sleep(20);
            }

            if (jplViewM.getResponse().getCode() == Response.ResCode.SUCCESS) {
                // Player name was approved, move forward
                playerState.changeToInPool(jplViewM, playerName);
            }

            return new ServerResponse.JoinResponse(jplViewM.getResponse());
            // TODO: Convert socket system to allow multi message sending
            // The solution to ^ might be to call another method before returning this one
            // and have that method await JPL for the second reply and send the message.
            // Of course, this still means modifying the API to allow messages to be sent like
            // that
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
        public ServerResponse handler(PlayerState playerState) throws InterruptedException {
            SwViewModel viewM = coreAPI.swController.submitWord(playerState.playerId(), word);

            while (viewM.getResponse() == null) {
                Thread.sleep(20);
            }

            return new ServerResponse.SubmitWordResponse(viewM.getResponse(), viewM.getGameData());
        }
    }

    /**
     * A constant request from clients to get current game data if
     * that client is in a game
     */
    record StateUpdate() implements ClientCommand {
        /**
         * <h2>Sub Handler: PD</h2>
         * Handler to retrieve current game state from PD view model. Called periodically by
         * all players in a game. returns null if player didn't pass tryJoin
         * <br><br>
         * Gets GameDisplayData from PD view model and returns it
         */
        @Override
        public ServerResponse handler(PlayerState playerState) throws InterruptedException {
            if (playerState.displayName() != null) {
                return new ServerResponse.CurrentState(
                        coreAPI.pdViewM.getCurrentGameState()
                );
            } else {
                return null;
            }
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
            case CMD_STATE_UPDATE -> new StateUpdate();
            case CMD_SEND_WORD -> new SubmitWord(payloadBlocks.length > 1 ? payloadBlocks[1] : "");

            // Crash if client sent a command which isn't recognized by the server
            default -> throw new UnsupportedOperationException("Invalid parameter: " + payloadBlocks[0]);
        };
    }
}
