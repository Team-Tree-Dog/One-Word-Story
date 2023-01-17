package net.onewordstory.core.usecases.get_all_titles;

import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

/**
 * Output boundary for get all titles use case.
 */
public interface GatOutputBoundary extends SsOutputBoundary {

    /**
     * Passes output data to the view model consisting of a success or fail response of getting all
     * suggested titles for a story, and the collection of suggested titles if successful
     * @param data  the output data object that contains the data specified above
     */
    void putSuggestedTitles(GatOutputData data);
}
