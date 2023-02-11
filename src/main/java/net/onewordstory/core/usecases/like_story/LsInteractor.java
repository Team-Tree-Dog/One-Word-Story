package net.onewordstory.core.usecases.like_story;

import net.onewordstory.core.usecases.InterruptibleThread;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.ThreadRegister;

public class LsInteractor implements LsInputBoundary {

    private static final String SUCCESS_RESPONSE = "Like has been added successfully";
    private static final String FAIL_RESPONSE = "An error occurred. Please, try again";

    private final LsGatewayStory repository;
    private final LsGatewayGuestAccounts guestAccManager;

    /**
     * The ThreadRegister that keeps track of all the running use case threads
     * for the shutdown-server use case
     */
    private final ThreadRegister register;

    /**
     * @param repository The repository that stores the data about all the stories
     * */
    public LsInteractor(LsGatewayStory repository, LsGatewayGuestAccounts accManager, ThreadRegister register) {
        this.repository = repository;
        this.register = register;
        this.guestAccManager = accManager;
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
            // Prevents liking multiple times
            if (guestAccManager.hasLikedStory(data.getGuestAccountId(), data.getStoryId())) {
                presenter.likeOutput(new LsOutputData(new Response(Response.ResCode.ALREADY_DONE,
                        "You have already liked this story!")));
            }

            else {
                // Don't interrupt during DB write
                setBlockInterrupt(true);
                Response response = repository.likeStory(data.getStoryId());
                setBlockInterrupt(false);

                // Records like on account
                if (response.getCode() == Response.ResCode.SUCCESS) {
                    guestAccManager.setLikedStory(data.getGuestAccountId(), data.getStoryId());
                }

                LsOutputData outputData = new LsOutputData(response);
                presenter.likeOutput(outputData);
            }
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
