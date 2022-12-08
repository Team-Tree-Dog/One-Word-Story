package adapters.presenters;

import adapters.view_models.LsViewModel;
import usecases.like_story.LsOutputBoundary;
import usecases.like_story.LsOutputData;

/**
 * The presented for the "like-story" use case
 * */
public class LsPresenter implements LsOutputBoundary {

    private final LsViewModel viewModel;

    /**
     * This constructor takes and assigns the view-model
     * @param viewModel The view-model
     * */
    public LsPresenter(LsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * @param data The output data that should be presented to the user
     * */
    public void likeOutput(LsOutputData data) {

    }

    @Override
    public void outputShutdownServer() {

    }
}
