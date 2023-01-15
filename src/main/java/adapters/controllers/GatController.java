package adapters.controllers;

import adapters.presenters.GatPresenter;
import adapters.view_models.GatViewModel;
import usecases.get_all_titles.GatInputBoundary;
import usecases.get_all_titles.GatInputData;

public class GatController {
    private GatInputBoundary gat;

    /**
     * Constructor for GAT use case controller. Takes in and sets the input boundary for this use case.
     * @param gat   the input boundary for the get all titles use case
     */
    public GatController(GatInputBoundary gat) {
        this.gat = gat;
    }

    /**
     * Get all titles for the story with this storyID.
     * @param storyId the ID of the story for which we want to get all titles
     */
    public GatViewModel getAllTitles(int storyId){
        GatInputData inp = new GatInputData(storyId);
        GatViewModel viewM = new GatViewModel();
        GatPresenter pres = new GatPresenter(viewM);
        gat.getAllTitles(inp, pres);
        return viewM;
    }
}
