package com.example.springapp;

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
    String CMD_TRY_JOIN = "try_join";
    String CMD_STATE_UPDATE = "state_update";
    String CMD_SEND_WORD = "send_word";
    String CMD_LEAVE = "leave";

    char SEPARATOR = 30;

    /**
     * JPL Command for initial joining
     * @param playerName desired display name of player who is trying to join
     */
    record TryJoin(String playerName) implements ClientCommand {}
    /**
     * A constant request from clients to get current game data if
     * that client is in a game
     */
    record StateUpdate() implements ClientCommand {}

    /**
     * A player's command to try to submit a word to the game
     * @param word Punctuation and word that player wants to submit
     */
    record SendWord(String word) implements ClientCommand {}

    /**
     * Parse raw payload into a client command object
     * @param payload content received from a client over the websocket
     * @return a parsed client command object
     */
    static ClientCommand parseCommand(String payload) {
        // Splits the payload into COMMAND + BODY by the agreed separator
        String[] payloadBlocks = payload.split(Character.toString(SEPARATOR));

        // Switch on the command header
        return switch (payloadBlocks[0]) {
            case CMD_TRY_JOIN -> new TryJoin(payloadBlocks[1]);
            case CMD_STATE_UPDATE -> new StateUpdate();
            case CMD_SEND_WORD -> new SendWord(payloadBlocks[1]);

            // Crash if client sent a command which isn't recognized by the server
            default -> throw new UnsupportedOperationException("Invalid parameter: " + payloadBlocks[0]);
        };
    }
}
