package adapters.controllers;

import usecases.comment_as_guest.CagInputBoundary;
import usecases.comment_as_guest.CagInputData;

public class CagController {

    private final CagInputBoundary inputBoundary;

    /**
     * Constructor for CagController
     * @param inputBoundary input boundary for the Comment As Guest use case
     */
    public CagController(CagInputBoundary inputBoundary) { this.inputBoundary = inputBoundary; }

    /**
     * Takes in information about a comment and adds it to a specified story
     * @param requestId the request's id
     * @param displayName chosen display name of the guest who wrote the comment
     * @param comment the comment the guest wrote
     * @param storyId the unique id of the story the comment is under
     */
    public void commentAsGuest(String requestId, String displayName, String comment, int storyId) {

        CagInputData inputData = new CagInputData(requestId, displayName, comment, storyId);
        inputBoundary.commentAsGuest(inputData);
    }
}
