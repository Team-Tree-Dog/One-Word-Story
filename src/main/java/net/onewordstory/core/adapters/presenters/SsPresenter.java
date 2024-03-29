package net.onewordstory.core.adapters.presenters;

import net.onewordstory.core.adapters.view_models.SsViewModel;
import org.example.ANSI;
import org.example.Log;
import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

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
        Log.sendMessage(ANSI.BLUE, "SS", ANSI.LIGHT_BLUE,
                "Presenter outputShutdownServer");
        viewModel.getIsShutdownAwaitable().set(true);
    }

}
