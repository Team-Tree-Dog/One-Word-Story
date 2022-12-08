package adapters.presenters;

import adapters.view_models.GmlsViewModel;
import usecases.Response;
import usecases.get_most_liked_stories.GmlsOutputBoundary;
import usecases.get_most_liked_stories.GmlsOutputData;

import java.util.List;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class GmlsPresenter implements GmlsOutputBoundary {

    private final GmlsViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public GmlsPresenter(GmlsViewModel viewM) { this.viewM = viewM; }

    /**
     * Update the view model with these stories
     * @param data the output data with which to update the view model
     */
    @Override
    public void putStories(GmlsOutputData data) {
        if (data.getStories() == null) {
            viewM.setResponse(data.getRes());
        } else {
            viewM.setLatestStories(data.getStories());
            viewM.setResponse(data.getRes());
        }
    }

    @Override
    public void outputShutdownServer() {
        viewM.setResponse(new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
