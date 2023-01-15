package usecases;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple record object to transfer story repo data with a title tacked on
 * @param title title of the story, or null if the story has no title
 * @param storyData story data from the repo
 */
public record FullStoryDTO(@Nullable String title, @NotNull StoryRepoData storyData) { }
