package usecases.get_all_titles;

import usecases.InterruptibleThread;
import usecases.RepoRes;
import usecases.ThreadRegister;
import usecases.TitleRepoData;

/**
 * The interactor for this use case. Carries out all the processes involved in getting all suggested titles for
 * a particular story
 */
public class GatInteractor implements GatInputBoundary{
    private final GatGatewayTitles repo;
    private final ThreadRegister register;

    /**
     * Constructor for the Interactor.
     * @param repo      the repository for this use case: implements the Gateway interface method to get all titles
     *                  for a specified story in this repo
     * @param register  a ThreadRegister object that records all the threads that are running at a particular time.
     */
    public GatInteractor(GatGatewayTitles repo, ThreadRegister register) {
        this.repo = repo;
        this.register = register;
    }

    /**
     * Nested thread class in the interactor. Is a subclass of InterruptibleThread, which is the implementation of
     * the Thread interface that is used for this project. Carries out all the processes involved in getting all
     * previously suggested titles for a particular story.
     */
    public class GatThread extends InterruptibleThread{
        private final GatInputData data;
        private final GatOutputBoundary pres;

        /**
         * Constructor for the Get All Titles use case Thread
         * @param data  The input data containing the storyId of the story we want to get all titles for
         * @param pres  output boundary for this use case
         */
        public GatThread(GatInputData data, GatOutputBoundary pres) {
            super(GatInteractor.this.register, pres);
            this.data = data;
            this.pres = pres;
        }

        /**
         * Implementation of the threadLogic() method in the InterruptibleThread interface. This method carries out
         * all the processes in the Get All Titles use case, and is called from within the run() method for GatThread
         * which is inherited from InterruptibleThread. The processes carried out in this method are:
         * 1. Retrieve all suggested titles from the repository using the getAllTitles() method
         * 2. Construct an output data object that contains the response and the suggested titles (which can be
         *    null in the case of a failed request)
         * 3. Pass the output data to the presenter, which updates the view model using method putSuggestedTitles()
         */
        public void threadLogic(){
            int storyId = data.getStoryId();
            RepoRes<TitleRepoData> suggestedTitles = repo.getAllTitles(storyId);

            // Passes along Repo fail response if it failed
            if (!suggestedTitles.isSuccess()) {
                pres.putSuggestedTitles(new GatOutputData(
                        null, suggestedTitles.getRes()
                ));
            }
            // Otherwise success, so pass repo rows content
            else {
                GatOutputData gatOutputData = new GatOutputData(suggestedTitles.getRows(), suggestedTitles.getRes());
                pres.putSuggestedTitles(gatOutputData);
            }
        }
    }

    /**
     * Creates the thread for the use case interactor and registers it into the thread register
     * @param data  the input data for this use case, contains the storyId of the story we want to get all titles for
     * @param pres  output boundary for this use case
     */
    @Override
    public void getAllTitles(GatInputData data, GatOutputBoundary pres){
        InterruptibleThread thread = new GatThread(data, pres);
        boolean success = register.registerThread(thread);
        if (!success){pres.outputShutdownServer();}
    }
}
