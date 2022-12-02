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

    public class StThread implements Runnable{
        StInputData data;
        StOutputBoundary boundary;

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
                Response res = new Response(Response.ResCode.INVALID_TITLE,mess);
                outputData = new StOutputData(data.getRequestId(), res);
            }
            else {
                StGatewayInputDataSuggest INPUT_DATA_SUGGEST =
                        new StGatewayInputDataSuggest(data.getStoryId(), data.getTitle());
                StGatewayOutputDataSuccess SUCCESS_DATA = repo.suggestTitle(INPUT_DATA_SUGGEST);
                if (SUCCESS_DATA.getSuccess()) {
                    String mess = String.format("'%1$s' was successfully added to suggested titles", data.getTitle());
                    Response res = new Response(Response.ResCode.INVALID_TITLE, mess);
                    outputData = new StOutputData(data.getRequestId(), res);
                } else {
                    String mess = String.format("Sorry. '%1$s' was not added", data.getTitle());
                    Response res = new Response(Response.ResCode.INVALID_TITLE, mess);
                    outputData = new StOutputData(data.getRequestId(), res);
                }
            }
            pres.suggestTitleOutput(outputData);
        }

        public void suggestTitle(StInputData data){}
    }
}
