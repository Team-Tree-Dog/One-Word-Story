package usecases.get_story_by_id;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usecases.FullStoryDTO;
import usecases.Response;
import usecases.shutdown_server.SsOutputBoundary;

import java.util.List;

public interface GsbiOutputBoundary extends SsOutputBoundary {
    /**
     * Use Case output for a list of stories with a response
     * @param storyDTOS list of full story DTOs, or null if res was fail
     * @param res response
     */
    void putStories (@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res);
}
