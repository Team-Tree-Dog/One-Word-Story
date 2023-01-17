package com.example.springapp.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import usecases.CommentRepoData;
import usecases.RepoRes;
import usecases.Response;
import usecases.comment_as_guest.CagGatewayComments;
import usecases.get_story_comments.GscGatewayComments;

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
