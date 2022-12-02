package usecases.suggest_title;
import entities.SuggestedTitleChecker;
import usecases.Response;

import java.util.Arrays;

/**
 * The interactor for this use case. Contains a thread that carries out the processes involved in suggesting a story\
 * title.
 */
public class StInteractor {
    StOutputBoundary pres;
    StGateway repo;
    SuggestedTitleChecker titleChecker;

    /**
     * This nested class is a thread for the processes involved in suggesting a title for the story. The tasks done
     * by the run() method in this thread are:
     * 1. Processing the title input by the user. (trimmed and multiple whitespaces replaced with single whitespace)
     * 2. Constructing the input data object corresponding to getting all previously suggested titles from the repo.
     * 3. Returning a failure response if the title suggested by the user is not a valid title or has
     *    already been suggested.
     * 4. If no failure response is returned in the previous step, constructing the input data object corresponding to
     *    adding the suggested title to the database.
     * 5. Running the method to add the suggested title, and, depending on the success or failure of this process,
     *    returning an appropriate response.
     * 6. Passing the output data to the viewModel to update the view according to the success or failure of the
     *    request to change title.
     */
    public class StThread implements Runnable{
        StInputData data;
        StOutputBoundary boundary;

        /**
         * The implementation of the run() method for this Thread. Performs all the tasks as specified in the
         * description for StThread.
         */
        public void run(){
            String title = data.getTitle().trim().replaceAll("\\s{2,}", " ");
            StGatewayInputDataGet inputDataGet = new StGatewayInputDataGet(data.getStoryId());
            String[] suggestedTitles = repo.getAllTitles(inputDataGet).getSuggestedTitles();
            StOutputData outputData;
            boolean isValid = titleChecker.checkValid(title);
            boolean alreadySuggested = Arrays.asList(suggestedTitles).contains(title);
            if (!isValid){
                String mess = String.format("'%1$s' is invalid", data.getTitle());
                Response res = new Response(Response.ResCode.INVALID_TITLE,mess);
                outputData = new StOutputData(data.getRequestId(), res);
            }
            else if (alreadySuggested) {
                String mess = String.format("'%1$s' was already suggested", data.getTitle());
                Response res = new Response(Response.ResCode.TITLE_ALREADY_SUGGESTED,mess);
                outputData = new StOutputData(data.getRequestId(), res);
            }
            else {
                StGatewayInputDataSuggest INPUT_DATA_SUGGEST =
                        new StGatewayInputDataSuggest(data.getStoryId(), data.getTitle());
                StGatewayOutputDataSuccess SUCCESS_DATA = repo.suggestTitle(INPUT_DATA_SUGGEST);
                if (SUCCESS_DATA.getSuccess()) {
                    String mess = String.format("'%1$s' was successfully added to suggested titles", data.getTitle());
                    Response res = new Response(Response.ResCode.SUCCESS, mess);
                    outputData = new StOutputData(data.getRequestId(), res);
                } else {
                    String mess = String.format("Sorry. '%1$s' was not added", data.getTitle());
                    Response res = new Response(Response.ResCode.FAIL, mess);
                    outputData = new StOutputData(data.getRequestId(), res);
                }
            }
            pres.suggestTitleOutput(outputData);
        }

        /**
         * The method that begins the thread for the use case interactor.
         * @param data  the input data for this use case. Contains the user-suggested title as well as the IDs to
         *              track the Story and this particular request to suggest a title for this story
         */
        public void suggestTitle(StInputData data){}
    }
}
