package com.example.springapp.db;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import usecases.RepoRes;
import usecases.Response;
import usecases.StoryRepoData;
import usecases.get_latest_stories.GlsGatewayStory;
import usecases.get_most_liked_stories.GmlsGatewayStory;
import usecases.get_story_by_id.GsbiGatewayStories;
import usecases.like_story.LsGatewayStory;
import usecases.pull_game_ended.PgeGatewayStory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

@Repository
public class PostgresStoryRepo implements LsGatewayStory, GlsGatewayStory,
        GmlsGatewayStory, GsbiGatewayStories, PgeGatewayStory {

    private static class StoryTableRow {

        private final int storyId;
        private final String story;
        private final double publishUnixTimestamp;
        private final String[] authors;
        private final int likes;

        public StoryTableRow (int storyId, @NotNull String story,
                              double publishUnixTimestamp, String[] authors, int likes) {
            this.storyId = storyId;
            this.story = story;
            this.publishUnixTimestamp = publishUnixTimestamp;
            this.authors = authors;
            this.likes = likes;
        }

        public int getStoryId() { return storyId; }
        public String getStory() { return story; }
        public double getPublishUnixTimestamp() { return publishUnixTimestamp; }
        public int getLikes() { return likes; }
        public String[] getAuthors() { return authors; }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public @NotNull RepoRes<StoryRepoData> getAllStories() {
        return null;
    }

    @Override
    public @NotNull RepoRes<StoryRepoData> getStoryById(int storyId) {
        StoryTableRow row = jdbcTemplate.query("SELECT * FROM stories", new RowMapper<StoryTableRow>() {
            @Override
            public StoryTableRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new StoryTableRow(
                        rs.getInt("story_id"),
                        rs.getString("story"),
                        rs.getInt("publish_unix_timestamp_utc_seconds"),
                        new String[0],
                        rs.getInt("num_likes")
                );
            }
        }).get(0);

        System.out.println(row.storyId);
        System.out.println(row.story);
        System.out.println(row.likes);
        return null;
    }

    @Override
    public @NotNull Response likeStory(int storyId) {
        return null;
    }

    @Override
    public @NotNull Response saveStory(String storyString, double publishUnixTimeStamp, @Nullable Set<String> authorDisplayNames) {
        return null;
    }
}
