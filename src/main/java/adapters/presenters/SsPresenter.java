package adapters.presenters;

import adapters.view_models.SsViewModel;
import usecases.shutdown_server.SsOutputBoundary;

/**
 * The presenter for the "shutdown-server" use case
 */
public class SsPresenter implements SsOutputBoundary {

    private final SsViewModel viewModel;

    /**
     * @param viewModel The view model that will be used by this presenter
     */
    public SsPresenter(SsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Notifies the view model that all threads are terminated, and the server is ready to shut down
     */
    public void outputShutdownServer() {

    }

}
