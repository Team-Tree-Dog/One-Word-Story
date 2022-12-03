package adapters.presenters;

import adapters.ViewModel;

/**
 * The presenter for the "shutdown-server" use case
 */
public class SsPresenter {

    private final ViewModel viewModel;

    /**
     * @param viewModel The view model that will be used by this presenter
     */
    public SsPresenter(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Notifies the view model that all threads are terminated, and the server is ready to shut down
     */
    public void outputShutdownServer() {

    }

}
