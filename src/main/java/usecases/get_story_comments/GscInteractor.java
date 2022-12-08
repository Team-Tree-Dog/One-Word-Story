package usecases.get_story_comments;

import usecases.*;

/**
 * Interactor for the Get Story Comments use case
 */
public class GscInteractor implements GscInputBoundary {

    private final GscGatewayComments repo;
    private final ThreadRegister register;

    /**
     * Constructor for GscInteractor
     * @param repo a repository, or gateway
     * @param register register for the thread
     */
    public GscInteractor(GscGatewayComments repo, ThreadRegister register) {

        this.repo = repo;
        this.register = register;
    }

    /**
     * Thread for getting story comments
     */
    public class GscThread extends InterruptibleThread {

        private final GscInputData data;
        private final GscOutputBoundary pres;

        /**
         * Constructor for GscThread
         * @param data GscInputData
         * @param pres output boundary for this use case
         */
        public GscThread(GscInputData data, GscOutputBoundary pres) {
            super(GscInteractor.this.register, pres);
            this.data = data;
            this.pres = pres;
        }

        @Override
        protected void threadLogic() throws InterruptedException {

            GscOutputData output;
            // true if there were no database errors
            RepoRes<CommentRepoData> res = repo.getAllComments(data.getStoryId());

            if (res.isSuccess()) {
                output = new GscOutputData(
                        res.getRows(),
                        new Response(Response.ResCode.SUCCESS, "Comments pulled successfully")
                );
            } else {
                // there was a database error, builds output with that specific response code
                output = new GscOutputData(null,
                        new Response(res.getRes().getCode(),
                                res.getRes().getMessage()));
            }
            pres.putStoryComments(output);
        }
    }

    /**
     * Gets story comments
     * @param data required data for getting story comments
     */
    public void getStoryComments(GscInputData data, GscOutputBoundary pres) {
        InterruptibleThread thread = new GscThread(data, pres);
        boolean success = register.registerThread(thread);
        if (!success) {
            pres.outputShutdownServer();
        }
    }
}
