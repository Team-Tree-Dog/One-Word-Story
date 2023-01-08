package com.example.springapp.db;

import org.jetbrains.annotations.NotNull;
import usecases.CommentRepoData;
import usecases.RepoRes;
import usecases.Response;
import usecases.comment_as_guest.CagGatewayComments;
import usecases.get_story_comments.GscGatewayComments;

public class PostgresCommentsRepo implements GscGatewayComments, CagGatewayComments {

    @Override
    public @NotNull Response commentAsGuest(int storyId, @NotNull String displayName, @NotNull String comment) {
        return null;
    }

    @Override
    public @NotNull RepoRes<CommentRepoData> getAllComments(int storyId) {
        return null;
    }
}
