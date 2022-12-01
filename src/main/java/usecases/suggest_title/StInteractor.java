package usecases.suggest_title;
import entities.SuggestedTitleChecker;

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
            try {
                boolean isValid = titleChecker.checkValid(title);
            }
            catch(Exception e){

            }
            // above code is to catch the exception for invalid title
            StGatewayInputDataSuggest INPUT_DATA_SUGGEST =
                    new StGatewayInputDataSuggest(data.getStoryId(), data.getTitle());
            StGatewayOutputDataSuccess SUCCESS_DATA = repo.suggestTitle(INPUT_DATA_SUGGEST);
            //build output data here
            //call pres.suggestedtitleoutput
        }
    }
}
