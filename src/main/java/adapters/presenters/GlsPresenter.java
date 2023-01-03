package adapters.presenters;

import adapters.view_models.GlsViewModel;
import usecases.Response;
import usecases.get_latest_stories.GlsOutputBoundary;
import usecases.get_latest_stories.GlsOutputData;

import java.util.List;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class GlsPresenter implements GlsOutputBoundary {

    private final GlsViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public GlsPresenter(GlsViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model with the retrieved
     * data.numToGet(nullable) stories
     */
    @Override
    public void putStories(GlsOutputData data) {
        if (data.getStories() == null) {
            viewM.getResponseAwaitable().set(data.getRes());
        } else {
            viewM.getStoriesAwaitable().set(data.getStories());
            viewM.getResponseAwaitable().set(data.getRes());
        }
    }

    @Override
    public void outputShutdownServer() {
        viewM.getResponseAwaitable().set(
                new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
