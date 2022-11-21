package usecases.like_story;

import usecases.Response;

public class LsOutputData {

    private final String requestId;
    private final Response response;

    /**
     * @param requestId The id of the initial request for adding likes
     * @param response The response from the repository
     * */
    public LsOutputData(String requestId, Response response) {
        this.requestId = requestId;
        this.response = response;
    }

    /**
     * @return Returns the request's id
     * */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @return Returns the response from the repository
     * */
    public Response getResponse() {
        return response;
    }
}
