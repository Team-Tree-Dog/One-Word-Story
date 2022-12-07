package usecases.shutdown_server;

import usecases.ThreadRegister;

public class SsInteractor implements SsInputBoundary {

    private final ThreadRegister threadRegister;

    public SsInteractor(ThreadRegister threadRegister) {
        this.threadRegister = threadRegister;
    }

    public void shutdownServer(SsOutputBoundary presenter) {
        threadRegister.stopThreads();
        presenter.outputShutdownServer();
    }

}
