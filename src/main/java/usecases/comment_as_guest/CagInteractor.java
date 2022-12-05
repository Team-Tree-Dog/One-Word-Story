package usecases.comment_as_guest;

import entities.comment_checkers.CommentChecker;
import entities.display_name_checkers.DisplayNameChecker;
import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

import static usecases.Response.ResCode.*;

/**
 * Interactor for Comment As Guest use case
 */
public class CagInteractor implements CagInputBoundary {

    CagOutputBoundary pres;
    CagGatewayComments repo;
    CommentChecker commentChecker;
    DisplayNameChecker displayChecker;
    ThreadRegister register;

    /**
     * Constructor for CagInteractor
     * @param pres a presenter, or output boundary
     * @param repo a repository, or gateway
     * @param commentChecker determines if comments are valid based on criteria of checker
     * @param displayChecker determines if names are valid based on criteria of checker
     * @param register register for the thread
     */
    public CagInteractor(CagOutputBoundary pres, CagGatewayComments repo, CommentChecker commentChecker,
                         DisplayNameChecker displayChecker, ThreadRegister register) {

        this.pres = pres;
        this.repo = repo;
        this.commentChecker = commentChecker;
        this.displayChecker = displayChecker;
        this.register = register;
    }

    /**
     * Thread for commenting as guest
     */
    public class CagThread extends InterruptibleThread {

        CagInputData data;

        /**
         * Constructor for CagThread
         * @param data CagInputData
         */
        public CagThread(CagInputData data) {

            super(CagInteractor.this.register, pres);
            this.data = data;
        }

        /**
         * Helper method for building CagOutputData in threadLogic
         * @param resCode the response code to use
         * @param message the message to send
         * @return the built CagOutputData
         */
        private CagOutputData outputHelper(Response.ResCode resCode, String message) {
            return new CagOutputData(data.getRequestId(), new Response(resCode, message));
        }

        @Override
        protected void threadLogic() throws InterruptedException {
            CagOutputData output;

            // verifies comment and display name are valid
            boolean isValidComment = commentChecker.checkValid(data.getComment());
            boolean isValidDisplay = displayChecker.checkValid(data.getDisplayName());

            // creates output based on error (if one exists)
            if (!isValidComment) {
                output = outputHelper(INVALID_COMMENT, "Comment is invalid");
            } else if (!isValidDisplay) {
                output = outputHelper(INVALID_DISPLAY_NAME, "Display name is invalid");

            // otherwise writes the comment to the repository
            } else {
                setBlockInterrupt(true);
                Response successData = repo.commentAsGuest(data.getStoryId(),
                        data.getDisplayName(), data.getComment());
                setBlockInterrupt(false);
                if (successData.getCode() == SUCCESS) {
                    output = outputHelper(SUCCESS, "Comment added successfully");
                } else {
                // there was a database error, in which case output is made with a code
                    output = outputHelper(successData.getCode(), successData.getMessage());
                }
            }
            pres.commentAsGuestOutput(output);
        }
    }

    /**
     * Comments as guest
     * @param data required data for comment
     */
    public void commentAsGuest(CagInputData data) {
        InterruptibleThread thread = new CagThread(data);
        boolean success = register.registerThread(thread);
        if (!success) {
            pres.outputShutdownServer();
        }
    }

}
