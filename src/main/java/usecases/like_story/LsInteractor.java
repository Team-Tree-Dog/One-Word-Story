package usecases.like_story;

import usecases.Response;

public class LsInteractor implements LsInputBoundary{

    private static final String SUCCESS_RESPONSE = "Like has been added successfully";
    private static final String FAIL_RESPONSE = "An error occurred. Please, try again";

    private final LsOutputBoundary presenter;
    private final LsGateway repository;

    /**
     * @param presenter The presenter that is responsible for notifying the client
     * @param repository The repository that stores the data about all the stories
     * */
    public LsInteractor(LsOutputBoundary presenter, LsGateway repository) {
        this.presenter = presenter;
        this.repository = repository;
    }

    public class LsRunnable implements Runnable {

        private final LsInputData data;


        /**
         * @param data The input data
         * */
        public LsRunnable(LsInputData data) {
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
        public void run() {
            LsGatewayInputData inputData = new LsGatewayInputData(data.getStoryId());
            LsGatewayOutputData gatewayOutputData = repository.likeStory(inputData);
            Response response = getResponseBasedOnSuccess(gatewayOutputData.isSuccess());
            LsOutputData outputData = new LsOutputData(data.getRequestId(), response);
            presenter.likeOutput(outputData);
        }
    }

    @Override
    public void likeStory(LsInputData data) { new Thread(new LsRunnable(data)).start(); }
}
