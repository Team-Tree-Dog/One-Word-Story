package usecases.upvote_title;

import usecases.shutdown_server.SsOutputBoundary;

public interface UtOutputBoundary extends SsOutputBoundary {

    void upvoteOutput(UtOutputData data);
}
