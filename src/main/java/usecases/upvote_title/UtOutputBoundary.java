package usecases.upvote_title;

import usecases.shutdown_server.SsOutputBoundary;

/**
 * Output boundary for upvote title use case.
 */
public interface UtOutputBoundary extends SsOutputBoundary {

    /**
     * Notify view model of success of liking the title
     * @param data  the output data that contains the record of whether upvoting the title was successful
     */
    void upvoteOutput(UtOutputData data);
}
