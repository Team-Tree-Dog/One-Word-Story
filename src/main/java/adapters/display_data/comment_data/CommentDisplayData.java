package adapters.display_data.comment_data;

import org.jetbrains.annotations.NotNull;
import usecases.CommentRepoData;

/**
 * Directly displayable comment object. Should contain only the info necessary for display
 * with no additional manipulation required.
 *
 * @param displayName Username of posting user
 * @param content Content string of the comment
 */
public record CommentDisplayData(@NotNull String displayName,
                                 @NotNull String content) {
    public static CommentDisplayData fromCommentRepoData(CommentRepoData repoData) {
        return new CommentDisplayData(repoData.getDisplayName(), repoData.getContent());
    }
}
