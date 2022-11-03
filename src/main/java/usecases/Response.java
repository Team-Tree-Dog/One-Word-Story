package usecases;

import exceptions.*;

/**
 * Response object created by use cases to send to the presenter. Has inner
 * enum with all entity error codes
 */
public class Response {

    /**
     * Success if no error was thrown in entities. Fail if an error
     * was thrown that doesn't have its own code. Otherwise, corresponding
     * error code.
     */
    public enum ResCode {
        SUCCESS,
        FAIL,
        PLAYER_NOT_FOUND,
        GAME_DOESNT_EXIST,
        GAME_RUNNING,
        OUT_OF_TURN,
        ID_IN_USE,
        INVALID_DISPLAY_NAME,
        INVALID_WORD
    }

    private final String message;
    private final ResCode code;

    /**
     * Create response object
     * @param code response code from enum
     * @param message response description
     */
    public Response (ResCode code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return response description message
     */
    public String getMessage() { return message; }

    /**
     * @return response code
     */
    public ResCode getCode() { return code; }

    /**
     * @param e Java exception
     * @param m Custom message of the response object
     * @return A response object with an error code corresponding to e
     * and a message m. Return FAIL code if the error didn't exist
     */
    public static Response fromException (Exception e, String m) {
        if (e instanceof PlayerNotFoundException) {
            return new Response(Response.ResCode.PLAYER_NOT_FOUND, m);
        } else if (e instanceof GameDoesntExistException) {
            return new Response(Response.ResCode.GAME_DOESNT_EXIST, m);
        } else if (e instanceof GameRunningException) {
            return new Response(Response.ResCode.GAME_RUNNING, m);
        } else if (e instanceof OutOfTurnException) {
            return new Response(Response.ResCode.OUT_OF_TURN, m);
        } else if (e instanceof IdInUseException) {
            return new Response(Response.ResCode.ID_IN_USE, m);
        } else if (e instanceof InvalidDisplayNameException) {
            return new Response(Response.ResCode.INVALID_DISPLAY_NAME, m);
        } else if (e instanceof InvalidWordException) {
            return new Response(Response.ResCode.INVALID_WORD, m);
        } else {
            return new Response(Response.ResCode.FAIL, m);
        }
    }

    /**
     * @param m Description of success
     * @return A response with success code
     */
    public static Response getSuccessful (String m) {
        return new Response(Response.ResCode.SUCCESS, m);
    }
}
