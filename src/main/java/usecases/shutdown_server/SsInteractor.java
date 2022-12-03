package usecases.shutdown_server;

import usecases.ThreadRegister;

public class SsInteractor {

    private final ThreadRegister threadRegister;

    private final SsOutputBoundary presenter;

    public SsInteractor(ThreadRegister threadRegister, SsOutputBoundary presenter) {
        this.threadRegister = threadRegister;
        this.presenter = presenter;
    }

    public void shutdownServer() {
        threadRegister.stopThreads();
        presenter.outputShutdownServer();
    }

}
