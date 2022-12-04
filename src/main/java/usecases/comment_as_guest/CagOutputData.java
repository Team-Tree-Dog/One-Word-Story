package usecases.comment_as_guest;

import usecases.Response;

/**
 * Output data for Comment As Guest use case
 */
public class CagOutputData {

    private String requestId;
    private Response res;

    /**
     * Constructor for CagOutputData
     * @param requestId the id of this specific request
     * @param res the response, describing what the response was when commentAsGuest was called
     */
    public CagOutputData(String requestId, Response res) {

        this.requestId = requestId;
        this.res = res;
    }
}
