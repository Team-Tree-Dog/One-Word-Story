package adapters.controllers;

import usecases.upvote_title.UtInputBoundary;
import usecases.upvote_title.UtInputData;

public class UtController {
    private UtInputBoundary ut;

    public UtController(UtInputBoundary ut) {this.ut = ut;}

    public void shutdownServer(String requestId, int storyId, String titleToUpvote){
        UtInputData inputData = new UtInputData(requestId, storyId, titleToUpvote);
        ut.upvoteTitle(inputData);
    }
}
