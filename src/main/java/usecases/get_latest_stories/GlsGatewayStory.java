package usecases.get_latest_stories;

<<<<<<< HEAD
import org.jetbrains.annotations.Nullable;
import usecases.StoryData;
=======
import org.jetbrains.annotations.NotNull;
import usecases.RepoRes;
import usecases.StoryRepoData;
>>>>>>> 117-view-db-core-changes-2

/**
 * Input Boundary Interface for Get Latest Stories use-case.
 * Implemented by GlsInteractor
 */

public interface GlsGatewayStory {
    /**
     * @return all stories from the repository in the RepoRes wrapper object. If repo operation
     *      * fails, RepoRes will reflect it
     */
    @NotNull
    RepoRes<StoryRepoData> getAllStories ();
}
