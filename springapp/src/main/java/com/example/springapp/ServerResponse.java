package com.example.springapp;

import adapters.display_data.GameEndPlayerDisplayData;
import adapters.display_data.not_ended_display_data.GameDisplayData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.Response;
import util.RecursiveSymboledIntegerHashMap;

import java.util.function.Predicate;

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
    String RESPONSE_JOIN = "JPL:out:in_pool";
    String RESPONSE_SUBMIT_WORD = "SW:out";
    String RESPONSE_STATE = "current_state";
    String RESPONSE_GAME_ENDED = "PGE:out";

    char SEPARATOR = 30;

    /**
     * Convert the ServerResponse to a string payload
     * @return a string payload ready to send to clients over the socket
     */
    String pack() throws JsonProcessingException;

    /**
     * @return if this response should be broadcast to all clients
     */
    boolean isBroadcast();

    /**
     * If predicate is null, broadcast to everyone
     * @return a predicate for filtering if a specific PlayerState should be broadcast to
     */
    @Nullable
    Predicate<PlayerState> isDoBroadcast();

    /**
     * Response sent upon processing a client's JoinPublicLobby command. Either the player's
     * display name was valid and they were added to the waiting pool, or the name
     * was invalid, so the player will not be allowed further into the game
     * @param response whether the player's display name was accepted
     * @param isDoBroadcast Custom filter function for broadcasting, return true if you want to broadcast
     *                      to the given player; irrelevant if isBroadcast is false
     */
    record JoinResponse(Response response, String playerID, boolean isBroadcast,
                        @Nullable Predicate<PlayerState> isDoBroadcast) implements ServerResponse {
        @Override
        public String pack() throws JsonProcessingException {
            return RESPONSE_JOIN + SEPARATOR + (new ObjectMapper()).writeValueAsString(response) +
                    SEPARATOR + playerID;
        }

        @Override
        public boolean isBroadcast() {
            return isBroadcast;
        }
    }

    /**
     * Response sent upon processing a client's submit word command. Either the player's
     * word was valid, so we pass back game data, or it was invalid, so we pass only a
     * response with fail code
     * @param response whether the player's word was added
     * @param isDoBroadcast Custom filter function for broadcasting, return true if you want to broadcast
     *                      to the given player; irrelevant if isBroadcast is false
     */
    record SubmitWordResponse(Response response, boolean isBroadcast,
                              @Nullable Predicate<PlayerState> isDoBroadcast) implements ServerResponse {
        @Override
        public String pack() throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            return RESPONSE_SUBMIT_WORD + SEPARATOR +
                    mapper.writeValueAsString(response);
        }

        @Override
        public boolean isBroadcast() {
            return isBroadcast;
        }
    }

    /**
     * Response sent each time a player asks to get new game state (which should be very
     * frequent when players are in a game) containing the current game information
     * <br><br>
     * <h2>Note on isInitialJPLState</h2> Currently, PD just broadcasts game updates to ALL connected
     * clients when a game is running. A client moves from the waiting screen to the game screen when
     * they receive a PD message. However, this PD broadcast DOES NOT guarantee that the player is
     * actually in the game in the entities layer. Consider a game that is not accepting anymore
     * players. Players who are in the waiting stage will not be moved into the game but will be
     * fooled by a PD message to move the the game screen. They will not be able to submit any words
     * and won't even be displayed in the players list. Effectively, they will be spectators. We do not
     * want this, so we set this flag to notify when a player was actually added to the game
     *
     * @param data game information necessary to display the current game state
     * @param isInitialJPLState This flag indicates that this game data being sent is a special
     *                          response for a SPECIFIC client that gets sent as soon as the client
     *                          got added to a game.
     * @param isDoBroadcast Custom filter function for broadcasting, return true if you want to broadcast
     *                      to the given player; irrelevant if isBroadcast is false
     */
    record CurrentState(@Nullable GameDisplayData data,
                        Boolean isInitialJPLState, boolean isBroadcast,
                        @Nullable Predicate<PlayerState> isDoBroadcast) implements ServerResponse {
        @Override
        public String pack() throws JsonProcessingException {
            return RESPONSE_STATE + SEPARATOR +
                    (new ObjectMapper()).writeValueAsString(data) + SEPARATOR +
                    isInitialJPLState.toString();
        }

        @Override
        public boolean isBroadcast() {
            return isBroadcast;
        }
    }

    /**
     * Response each time a game ends (PGE)
     * @param playerStats a single player's statistic
     * @param isDoBroadcast Custom filter function for broadcasting, return true if you want to broadcast
     *                      to the given player; irrelevant if isBroadcast is false
     */
    record GameEndResponse(@NotNull GameEndPlayerDisplayData playerStats,
                           boolean isBroadcast,
                           @Nullable Predicate<PlayerState> isDoBroadcast) implements ServerResponse {

        @Override
        public String pack() throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode root = mapper.createObjectNode();
            root.put("id", playerStats.getId());
            root.put("display", playerStats.getDisplayName());

            ArrayNode statNode = mapper.createArrayNode();
            for (RecursiveSymboledIntegerHashMap stat: playerStats.getStats()) {
                statNode.add(stat.getJsonNode());
            }

            root.set("stats", statNode);

            return RESPONSE_GAME_ENDED + SEPARATOR + mapper.writeValueAsString(root);
        }

        @Override
        public boolean isBroadcast() { return isBroadcast; }
    }

    // Add new state for disconnected PlayerState, and add boradcast constraints to prevent game
    // broadcast to non-joined players or disconnected ones
}
