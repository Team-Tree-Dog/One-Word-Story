package adapters.controllers;

import usecases.get_all_titles.GatInputBoundary;
import usecases.get_all_titles.GatInputData;

public class GatController {
    private GatInputBoundary gat;

    public void getAllTitles(int storyId){
        GatInputData inp = new GatInputData(storyId);
        gat.getAllTitles(inp);
    }
}
