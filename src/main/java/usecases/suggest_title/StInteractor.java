package usecases.suggest_title;
import entities.suggested_title_checkers.SuggestedTitleChecker;
import usecases.*;

import java.util.ArrayList;

/**
 * The interactor for this use case. Contains a thread that carries out the processes involved in suggesting a story\
 * title.
 */
public class StInteractor implements StInputBoundary {

    private final StGatewayTitles repo;

    private final SuggestedTitleChecker titleChecker;

    private final ThreadRegister register;

    /**
     * Constructor for the Interactor
     * @param repo
     * @param titleChecker
     * @param register
     */
    public StInteractor(StGatewayTitles repo, SuggestedTitleChecker titleChecker, ThreadRegister register) {
        this.repo = repo;
        this.titleChecker = titleChecker;
        this.register = register;
    }

    /**
     * This nested class is a thread for the processes involved in suggesting a title for the story. The tasks done
     * by the threadLogic() method in this thread are:
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
    public class StThread extends InterruptibleThread {
        private StInputData data;
        private StOutputBoundary pres;

        /**
         * Constructor for the thread.
         * @param data      the input data for the thread. Contains the ID of the story, the request to change title,
         *                  and the user-suggested title.
         * @param pres      output boundary for this use case
         */
        public StThread(StInputData data, StOutputBoundary pres) {
            super(StInteractor.this.register, pres);
            this.data = data;
            this.pres = pres;
        }

        /**
         * The implementation of the threadLogic() method for this InterruptibleThread. Performs all the tasks
         * as specified in the description for StThread.
         */
        @Override
        public void threadLogic(){
            /*
             * Step 1: Process title input: We retrieve the title from the input data, trim leading and trailing
             * whitespaces, and replace repeated whitespaces with a single whitespace.
             */
            String title = data.getTitle().trim().replaceAll("\\s{2,}", " ");

            int storyId = data.getStoryId();

            /*
             * Step 2: Create a Gateway object that carries out the processes of getting all previously
             * suggested titles from the repo, and pass it into repo.getAllTitles()
             * to get all previously suggested titles.
             */
            RepoRes<TitleRepoData> suggestedTitles = repo.getAllTitles(storyId);


            /* outputData is a VARIABLE that is initialized differently depending on the appropriate response
             * for each case (title is invalid, title was already suggested, success or failure of adding title) */
            StOutputData outputData;


            /*
             * Step 3: Depending on the case (title is invalid, title was already suggested,
             * success or failure of adding title), suggest the title (if not invalid or previously suggested)
             * and create the output data.
             */

            if (!suggestedTitles.isSuccess()) {
                outputData = new StOutputData(suggestedTitles.getRes());
            }

            else if (!titleChecker.checkValid(title)){
                // check if the title is not valid and initialize output Data accordingly
                String mess = data.getTitle() + " is invalid";
                Response res = new Response(Response.ResCode.INVALID_TITLE,mess);
                outputData = new StOutputData(res);
            }

            else {

                ArrayList<String> suggestedTitlesList = new ArrayList<>();
                for (TitleRepoData titleData : suggestedTitles.getRows()) {
                    suggestedTitlesList.add(titleData.getTitle());
                }

                if (suggestedTitlesList.contains(title)) {
                    // check if the title was already suggested and initialize output data accordingly
                    String mess = data.getTitle() + " was already suggested";
                    Response res = new Response(Response.ResCode.TITLE_ALREADY_SUGGESTED,mess);
                    outputData = new StOutputData(res);
                } else {
                    // the body of this else block carries out the processes to suggest the title once we have ensured
                    // that the title is valid and has not been already suggested.

                    setBlockInterrupt(true);
                    Response res =  repo.suggestTitle(storyId, title);
                    setBlockInterrupt(false);
                    outputData = new StOutputData(res);
                }

            }
            //passes the output data to the presenter
            /*
             * Step 4: Pass the output data to the presenter, which updates the View Model that notifies the user
             * of the outcome of their request to suggest a title.
             */
            pres.suggestTitleOutput(outputData);
        }
    }

    /**
     * The method that begins the thread for the use case interactor.
     * @param data  the input data for this use case. Contains the user-suggested title as well as the IDs to
     *              track the Story and this particular request to suggest a title for this story
     * @param pres  output boundary for this use case
     */
    @Override
    public void suggestTitle(StInputData data, StOutputBoundary pres){
        InterruptibleThread thread = new StThread(data, pres);
        boolean success = register.registerThread(thread);
        if (!success){
            pres.outputShutdownServer();
        }
    }
}
