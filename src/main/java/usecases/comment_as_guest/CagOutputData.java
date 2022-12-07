package usecases.comment_as_guest;

import org.jetbrains.annotations.NotNull;
import usecases.Response;

/**
 * Output data for Comment As Guest use case
 */
public class CagOutputData {

    private final String requestId;
    private final Response res;

    /**
     * Constructor for CagOutputData
     * @param requestId the id of this specific request
     * @param res the response, describing what the response was when commentAsGuest was called
     */
    public CagOutputData(@NotNull String requestId, @NotNull Response res) {

        this.requestId = requestId;
        this.res = res;
    }

    @NotNull
    public Response getRes() { return res; }

    @NotNull
    public String getRequestId() { return requestId; }
}
