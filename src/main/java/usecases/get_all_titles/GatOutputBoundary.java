package usecases.get_all_titles;

import usecases.shutdown_server.SsOutputBoundary;

public interface GatOutputBoundary extends SsOutputBoundary {

    void putSuggestedTitles(GatOutputData data);
}
