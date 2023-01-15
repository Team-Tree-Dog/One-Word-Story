package usecases.get_latest_stories;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.FullStoryDTO;
import usecases.Response;
import usecases.shutdown_server.SsOutputBoundary;

import java.util.List;

/**
 *  Output boundary Interface for Get Latest Stories use-case
 *  Implemented by the Presenter
 */

public interface GlsOutputBoundary extends SsOutputBoundary {
    /**
     * Use Case output for a list of stories with a response
     * @param storyDTOS list of full story DTOs, or null if res was fail
     * @param res response
     */
    void putStories (@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res);

}
