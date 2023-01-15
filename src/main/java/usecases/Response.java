package usecases;

import exceptions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Response object created by use cases to send to the presenter. Has inner
 * enum with all entity error codes
 */
public class Response {

    private static final Map<Class<? extends EntityException>, Response.ResCode>
            mapExceptionToResultCode = new HashMap<>();

    static {
        mapExceptionToResultCode.put(PlayerNotFoundException.class, Response.ResCode.PLAYER_NOT_FOUND);
        mapExceptionToResultCode.put(GameDoesntExistException.class, Response.ResCode.GAME_DOESNT_EXIST);
        mapExceptionToResultCode.put(GameRunningException.class, ResCode.GAME_RUNNING);
        mapExceptionToResultCode.put(OutOfTurnException.class, ResCode.OUT_OF_TURN);
        mapExceptionToResultCode.put(IdInUseException.class, ResCode.ID_IN_USE);
        mapExceptionToResultCode.put(InvalidDisplayNameException.class, ResCode.INVALID_DISPLAY_NAME);
        mapExceptionToResultCode.put(InvalidWordException.class, ResCode.INVALID_WORD);
    }

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
        INVALID_WORD,
        INVALID_COMMENT,
        INVALID_TITLE,
        TITLE_ALREADY_SUGGESTED,
        STORY_NOT_FOUND,
        SHUTTING_DOWN,
        TITLE_NOT_FOUND
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
     * @param e An entity exception
     * @param m Custom message of the response object
     * @return A response object with an error code corresponding to e
     * and a message m. Return FAIL code if the error didn't exist
     */
    public static Response fromException (EntityException e, String m) {
        ResCode resultCode = mapExceptionToResultCode.get(e.getClass());
        if(resultCode == null) {
            resultCode = ResCode.FAIL;
        }
        return new Response(resultCode, m);
    }

    /**
     * @param m Description of success
     * @return A response with success code
     */
    public static Response getSuccessful (String m) {
        return new Response(Response.ResCode.SUCCESS, m);
    }

    /**
     * @param m Description of failure
     * @return A response with plain FAIL code
     */
    public static Response getFailure (String m) {
        return new Response(Response.ResCode.FAIL, m);
    }

    @Override
    public String toString() {
        return "Response(" + code.toString() + ", " + message + ")" ;
    }
}
