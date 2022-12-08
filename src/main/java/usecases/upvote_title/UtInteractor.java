package usecases.upvote_title;

import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

/**
 * The interactor for this use case: carries out all the processes involved in upvoting a title.
 */
public class UtInteractor implements UtInputBoundary{
    private final UtOutputBoundary pres;
    private final UtGatewayTitles repo;
    private final ThreadRegister register;

    /**
     * @param pres      the presenter for this use case
     * @param repo      the repository containing the stories and the titles. Implements UtGatewayTitles
     * @param register  the register for the interruptible threads
     */
    public UtInteractor(UtOutputBoundary pres, UtGatewayTitles repo, ThreadRegister register) {
        this.pres = pres;
        this.repo = repo;
        this.register = register;
    }

    /**
     * A nested class that defines the InterruptibleThread for this use case
     */
    public class UtThread extends InterruptibleThread{
        private UtInputData data;

        /**
         * @param data  the input data that this thread takes in to carry out the processes for this use case.
         */
        public UtThread(UtInputData data) {
            super(UtInteractor.this.register, UtInteractor.this.pres);
            this.data = data;
        }

        /**
         * The threadLogic() method for this interruptible thread:
         * 1. upvotes the given title in the input data and receives the response from the repo call
         * 2. constructs the output data using this response
         * 3. passes the output data to the presenter, which then updates the view model
         */
        public void threadLogic() {
            Response res = repo.upvoteTitle(data.getStoryId(), data.getTitleToUpvote());
            UtOutputData outputData = new UtOutputData(data.getRequestId(), res); //builds output data
            pres.upvoteOutput(outputData);
        }
    }

    /**
     * Implementation of upvoteTitle() in input boundary. Upvotes the title in this input data
     * @param data  input data for this use case.
     */
    @Override
    public void upvoteTitle(UtInputData data){
        InterruptibleThread thread = new UtThread(data);
        boolean success = register.registerThread(thread);
        if (!success){pres.outputShutdownServer();}
    }
}
