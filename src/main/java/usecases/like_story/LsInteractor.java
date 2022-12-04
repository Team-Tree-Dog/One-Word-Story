package usecases.like_story;

import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

public class LsInteractor implements LsInputBoundary{

    private static final String SUCCESS_RESPONSE = "Like has been added successfully";
    private static final String FAIL_RESPONSE = "An error occurred. Please, try again";

    private final LsOutputBoundary presenter;
    private final LsGatewayStory repository;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    /**
     * @param presenter The presenter that is responsible for notifying the client
     * @param repository The repository that stores the data about all the stories
     * */
    public LsInteractor(LsOutputBoundary presenter, LsGatewayStory repository, ThreadRegister register) {
        this.presenter = presenter;
        this.repository = repository;
        this.register = register;
    }

    public class LsRunnable extends InterruptibleThread {

        private final LsInputData data;


        /**
         * @param data The input data
         * */
        public LsRunnable(LsInputData data) {
            super(LsInteractor.this.register, LsInteractor.this.presenter);
            this.data = data;
        }

        private Response getResponseBasedOnSuccess(boolean success) {
            Response.ResCode code;
            String message;
            if (success) {
                 code = Response.ResCode.SUCCESS;
                 message = SUCCESS_RESPONSE;
            } else {
                code = Response.ResCode.FAIL;
                message = FAIL_RESPONSE;
            }
            return new Response(code, message);
        }

        @Override
        public void threadLogic() {
            // Don't interrupt during DB write
            setBlockInterrupt(true);
            Response response = getResponseBasedOnSuccess(repository.likeStory(data.getStoryId()));
            setBlockInterrupt(false);

            LsOutputData outputData = new LsOutputData(data.getRequestId(), response);
            presenter.likeOutput(outputData);
        }
    }

    @Override
    public void likeStory(LsInputData data) {
        InterruptibleThread thread = new LsRunnable(data);
        if (!register.registerThread(thread)) {
            presenter.outputShutdownServer();
        }
    }
}
