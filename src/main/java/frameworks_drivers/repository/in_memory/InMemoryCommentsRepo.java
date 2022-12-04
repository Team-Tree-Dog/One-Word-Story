package frameworks_drivers.repository.in_memory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * In memory implementation of the database component in charge of storing
 * comments associated with stories
 */
public class InMemoryCommentsRepo implements GscGatewayComments, CagGatewayComments {

    /**
     *
     * @param storyId unique primary key ID of story to which to save a comment
     * @param displayName string guest display name, not null
     * @param comment string comment content, no
     * @return if the comment was added successfully
     */
    @Override
    public boolean commentAsGuest (int storyId, @NotNull String displayName, @NotNull String comment) {

    }

    /**
     *
     * @param storyId unique primary key ID of story for which to retrieve all comments
     * @return all comments for the requested story, or null if some failure occurs
     */
    @Override
    @Nullable
    public String[] getAllComments (int storyId) {

    }
}
