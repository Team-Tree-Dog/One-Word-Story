package net.onewordstory.core.usecases.upvote_title;

import net.onewordstory.core.usecases.Response;

/**
 * Output data for this use case.
 */
public class UtOutputData {
    private Response res;

    /**
     * @param res       the response that records the success of upvoting the title
     */
    public UtOutputData(Response res) {
        this.res = res;
    }

    public Response getRes() {
        return res;
    }
}
