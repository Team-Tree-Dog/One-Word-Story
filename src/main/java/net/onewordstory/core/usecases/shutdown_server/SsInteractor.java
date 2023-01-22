package net.onewordstory.core.usecases.shutdown_server;

import net.onewordstory.core.usecases.ThreadRegister;

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
