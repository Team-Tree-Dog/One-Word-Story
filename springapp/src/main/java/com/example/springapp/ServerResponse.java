package com.example.springapp;

import adapters.display_data.not_ended_display_data.GameDisplayData;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unifies a structure of all the possible OUTGOING (SEND) responses to the clients.
 * <br><br>
 * Each response consists of a response header "code", followed by a separator, followed
 * by data content of the message.
 * <br><br>
 * For example, current_state is a response that sends a JSON serialized GameDisplayData
 * object to clients in a running game. The format of the messages are agreed upon
 * between client side JS code and this backend Java code
 */
public sealed interface ServerResponse {
    String RESPONSE_JOIN = "join_response";
    String RESPONSE_STATE = "current_state";

    char SEPARATOR = 30;

    /**
     * Convert the ServerResponse to a string payload
     * @return a string payload ready to send to clients over the socket
     */
    String pack() throws Exception;

    /**
     * Response sent upon processing a client's tryJoin command. Either the player's
     * display name was valid and they were added to the waiting pool, or the name
     * was invalid, so the player will not be allowed further into the game
     * @param response whether the player's display name was accepted
     */
    record JoinResponse(boolean response) implements ServerResponse {
        @Override
        public String pack() {
            return RESPONSE_JOIN + SEPARATOR + (response ? "true" : "false");
        }
    }

    /**
     * Response sent each time a player asks to get new game state (which should be very
     * frequent when players are in a game) containing the current game information
     * @param data game information necessary to display the current game state
     */
    record CurrentState(GameDisplayData data) implements ServerResponse {
        private static final ObjectMapper mapper = new ObjectMapper();	// thread-safe

        @Override
        public String pack() throws Exception {
            return RESPONSE_STATE + SEPARATOR + mapper.writeValueAsString(data);
        }
    }
}
