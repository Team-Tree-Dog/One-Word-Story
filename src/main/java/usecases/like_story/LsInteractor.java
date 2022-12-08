package usecases.like_story;

import usecases.InterruptibleThread;
import usecases.Response;
import usecases.ThreadRegister;

public class LsInteractor implements LsInputBoundary {

    private static final String SUCCESS_RESPONSE = "Like has been added successfully";
    private static final String FAIL_RESPONSE = "An error occurred. Please, try again";

    private final LsGatewayStory repository;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    /**
     * @param repository The repository that stores the data about all the stories
     * */
    public LsInteractor(LsGatewayStory repository, ThreadRegister register) {
        this.repository = repository;
        this.register = register;
    }

    public class LsRunnable extends InterruptibleThread {

        private final LsInputData data;
        private final LsOutputBoundary presenter;

        /**
         * @param data The input data
         * @param presenter output boundary for this use case
         * */
        public LsRunnable(LsInputData data, LsOutputBoundary presenter) {
            super(LsInteractor.this.register, presenter);
            this.presenter = presenter;
            this.data = data;
        }

        @Override
        public void threadLogic() {
            // Don't interrupt during DB write
            setBlockInterrupt(true);
            Response response = repository.likeStory(data.getStoryId());
            setBlockInterrupt(false);

            LsOutputData outputData = new LsOutputData(data.getRequestId(), response);
            presenter.likeOutput(outputData);
        }
    }

    @Override
    public void likeStory(LsInputData data, LsOutputBoundary presenter) {
        InterruptibleThread thread = new LsRunnable(data, presenter);
        if (!register.registerThread(thread)) {
            presenter.outputShutdownServer();
        }
    }
}
