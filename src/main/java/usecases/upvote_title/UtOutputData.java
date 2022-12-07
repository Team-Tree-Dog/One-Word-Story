package usecases.upvote_title;

import usecases.Response;

/**
 * Output data for this use case.
 */
public class UtOutputData {
    private String requestId;
    private Response res;

    /**
     * @param requestId the ID tracking this particular request to upvote title
     * @param res       the response that records the success of upvoting the title
     */
    public UtOutputData(String requestId, Response res) {
        this.requestId = requestId;
        this.res = res;
    }

    public String getRequestId() {
        return requestId;
    }

    public Response getRes() {
        return res;
    }
}
