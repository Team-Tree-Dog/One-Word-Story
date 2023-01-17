package com.example.springapp.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import usecases.RepoRes;
import usecases.Response;
import usecases.TitleRepoData;
import usecases.get_all_titles.GatGatewayTitles;
import usecases.get_latest_stories.GlsGatewayTitles;
import usecases.get_most_liked_stories.GmlsGatewayTitles;
import usecases.get_story_by_id.GsbiGatewayTitles;
import usecases.suggest_title.StGatewayTitles;
import usecases.upvote_title.UtGatewayTitles;

@Repository
public class PostgresTitlesRepo implements GatGatewayTitles, StGatewayTitles,
        UtGatewayTitles, GlsGatewayTitles, GmlsGatewayTitles, GsbiGatewayTitles {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public @NotNull Response suggestTitle(int storyId, @NotNull String titleSuggestion) {
        return null;
    }

    @Override
    public RepoRes<TitleRepoData> getAllTitles(int storyId) {
        return null;
    }

    @Override
    public @NotNull RepoRes<String> getMostUpvotedStoryTitle(int storyId) {
        return null;
    }

    @Override
    public @NotNull Response upvoteTitle(int storyId, String titleToUpvote) {
        return null;
    }
}
