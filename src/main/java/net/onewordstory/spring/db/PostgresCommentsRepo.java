package net.onewordstory.spring.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import net.onewordstory.core.usecases.CommentRepoData;
import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.comment_as_guest.CagGatewayComments;
import net.onewordstory.core.usecases.get_story_comments.GscGatewayComments;

@Repository
public class PostgresCommentsRepo implements GscGatewayComments, CagGatewayComments {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public @NotNull Response commentAsGuest(int storyId, @NotNull String displayName, @NotNull String comment) {
        return null;
    }

    @Override
    public @NotNull RepoRes<CommentRepoData> getAllComments(int storyId) {
        return null;
    }
}
