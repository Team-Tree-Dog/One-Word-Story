package net.onewordstory.core.usecases.suggest_title;

import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

/**
 * Output Boundary for the Suggest Title use case.
 */
public interface StOutputBoundary extends SsOutputBoundary {
    /**
     * Notifies the view model of the success of adding the suggested title to the database.
     * @param data The output data for the use case.
     */
    void suggestTitleOutput(StOutputData data);
}
