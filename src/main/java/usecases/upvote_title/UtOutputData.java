package usecases.upvote_title;

import usecases.Response;

public class UtOutputData {
    private String requestId;
    private Response res;

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
