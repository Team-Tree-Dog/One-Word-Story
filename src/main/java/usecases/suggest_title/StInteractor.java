package usecases.suggest_title;
import entities.SuggestedTitleChecker;
import usecases.*;
import usecases.shutdown_server.SsOutputBoundary;

import java.util.Arrays;

/**
 * The interactor for this use case. Contains a thread that carries out the processes involved in suggesting a story\
 * title.
 */
public class StInteractor {
    private StOutputBoundary pres;
    private StGateway repo;
    private SuggestedTitleChecker titleChecker;

    private ThreadRegister register;

    /**
     * Constructor for the Interactor
     * @param pres
     * @param repo
     * @param titleChecker
     * @param register
     */
    public StInteractor(StOutputBoundary pres, StGateway repo, SuggestedTitleChecker titleChecker,
                        ThreadRegister register) {
        this.pres = pres;
        this.repo = repo;
        this.titleChecker = titleChecker;
        this.register =register;
    }

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
    public class StThread extends InterruptibleThread {
        private StInputData data;

        /**
         * Constructor for the thread.
         * @param data      the input data for the thread. Contains the ID of the story, the request to change title,
         *                  and the user-suggested title.
         */
        public StThread(StInputData data) {
            super(StInteractor.this.register, (SsOutputBoundary) StInteractor.this.pres);
        }

        /**
         * The implementation of the run() method for this Thread. Performs all the tasks as specified in the
         * description for StThread.
         */
        public void threadLogic(){
            /**
             * Step 1: Process title input: We retrieve the title from the input data, trim leading and trailing
             * whitespaces, and replace repeated whitespaces with a single whitespace.
             */
            String title = data.getTitle().trim().replaceAll("\\s{2,}", " ");

            int storyId = data.getStoryId();

            /**
             * Step 2: Create a Gateway object that carries out the processes of getting all previously
             * suggested titles from the repo, and pass it into repo.getAllTitles()
             * to get all previously suggested titles.
             */
            RepoRes<TitleRepoData> suggestedTitles = repo.getAllTitles(storyId);


            /* outputData is a VARIABLE that is initialized differently depending on the appropriate response
             * for each case (title is invalid, title was already suggested, success or failure of adding title) */
            StOutputData outputData;


            /**
             * Step 3: Depending on the case (title is invalid, title was already suggested,
             * success or failure of adding title), suggest the title (if not invalid or previously suggested)
             * and create the output data.
             */
            if (!titleChecker.checkValid(title)){
                // check if the title is not valid and initialize output Data accordingly
                String mess = String.format("'%1$s' is invalid", data.getTitle());
                Response res = new Response(Response.ResCode.INVALID_TITLE,mess);
                outputData = new StOutputData(data.getRequestId(), res);
            }


            else if (Arrays.asList(suggestedTitles).contains(title)) {
                // check if the title was already suggested and initialize output data accordingly
                String mess = String.format("'%1$s' was already suggested", data.getTitle());
                Response res = new Response(Response.ResCode.TITLE_ALREADY_SUGGESTED,mess);
                outputData = new StOutputData(data.getRequestId(), res);
            }
            else {
                // the body of this else block carries out the processes to suggest the title once we have ensured
                // that the title is valid and has not been already suggested.


                // StGatewayInputDataSuggest inputDataSuggest =
                // new StGatewayInputDataSuggest(data.getStoryId(), data.getTitle());
                // StGatewayOutputDataSuccess successData = repo.suggestTitle(inputDataSuggest);
                setBlockInterrupt(true);
                Response res =  repo.suggestTitle(storyId, title);
                setBlockInterrupt(false);
                outputData = new StOutputData(data.getRequestId(), res);

                // this if else statement creates the appropriate output data depending on whether the suggested title
                // was successfully added.
//                if (successData.getSuccess()) {
//                    String mess = String.format("'%1$s' was successfully added to suggested titles", data.getTitle());
//                    Response res = new Response(Response.ResCode.SUCCESS, mess);
//                    outputData = new StOutputData(data.getRequestId(), res);
//                }
//
//                else {
//                    String mess = String.format("Sorry. '%1$s' was not added", data.getTitle());
//                    Response res = new Response(Response.ResCode.FAIL, mess);
//                    outputData = new StOutputData(data.getRequestId(), res);
//                }
            }


            //passes the output data to the presenter
            /**
             * Step 4: Pass the output data to the presenter, which updates the View Model that notifies the user
             * of the outcome of their request to suggest a title.
             */
            pres.suggestTitleOutput(outputData);
        }

        /**
         * The method that begins the thread for the use case interactor.
         * @param data  the input data for this use case. Contains the user-suggested title as well as the IDs to
         *              track the Story and this particular request to suggest a title for this story
         */
        public void suggestTitle(StInputData data){
            InterruptibleThread thread = new StThread(data);
            boolean success = register.registerThread(thread);
            if (!success){
                pres.outputShutdownServer();
            }
        }
    }
}