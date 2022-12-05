package usecases.get_story_comments;

import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

/**
 * Interactor for the Get Story Comments use case
 */
public class GscInteractor implements GscInputBoundary {

    private final GscOutputBoundary pres;
    private final GscGatewayComments repo;
    private final ThreadRegister register;

    /**
     * Constructor for GscInteractor
     * @param pres a presenter, or output boundary
     * @param repo a repository, or gateway
     * @param register register for the thread
     */
    public GscInteractor(GscOutputBoundary pres, GscGatewayComments repo, ThreadRegister register) {

        this.pres = pres;
        this.repo = repo;
        this.register = register;
    }

    /**
     * Thread for getting story comments
     */
    public class GscThread extends InterruptibleThread {

        private final GscInputData data;

        /**
         * Constructor for GscThread
         * @param data GscInputData
         */
        public GscThread(GscInputData data) {
            super(GscInteractor.this.register, pres);
            this.data = data;
        }

        @Override
        protected void threadLogic() throws InterruptedException {

            GscOutputData output;
            // true if there were no database errors
            boolean success = repo.getAllComments(data.getStoryId()).isSuccess();
            if (success) {
                output = new GscOutputData(
                        repo.getAllComments(data.getStoryId()).getRows(),
                        new Response(Response.ResCode.SUCCESS, "Comments pulled successfully")
                );
            } else {
                // there was a database error, builds output with that specific response code
                output = new GscOutputData(null,
                        new Response(repo.getAllComments(data.getStoryId()).getRes().getCode(),
                                "Database failed"));
            }
            pres.putStoryComments(output);
        }
    }

    /**
     * Gets story comments
     * @param data required data for getting story comments
     */
    public void getStoryComments(GscInputData data) {
        InterruptibleThread thread = new GscThread(data);
        boolean success = register.registerThread(thread);
        if (!success) {
            pres.outputShutdownServer();
        }
    }
}
