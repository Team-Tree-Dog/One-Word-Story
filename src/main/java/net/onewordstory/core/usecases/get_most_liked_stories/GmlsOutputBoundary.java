package net.onewordstory.core.usecases.get_most_liked_stories;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.onewordstory.core.usecases.FullStoryDTO;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.shutdown_server.SsOutputBoundary;

import java.util.List;

/**
 * Defines the abstract method to update the View Model in the Get Most Liked Stories use case
 */
public interface GmlsOutputBoundary extends SsOutputBoundary {
    /**
     * Use Case output for a list of stories with a response
     * @param storyDTOS list of full story DTOs, or null if res was fail
     * @param res response
     */
    void putStories (@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res);
}
