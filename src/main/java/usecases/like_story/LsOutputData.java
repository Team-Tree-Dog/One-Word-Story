package usecases.like_story;

import usecases.Response;

public class LsOutputData {

    private final Response response;

    /**
     * @param response The response from the repository
     * */
    public LsOutputData(Response response) {
        this.response = response;
    }

    /**
     * @return Returns the response from the repository
     * */
    public Response getResponse() {
        return response;
    }
}
