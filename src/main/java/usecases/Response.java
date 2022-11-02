package usecases;

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
}
