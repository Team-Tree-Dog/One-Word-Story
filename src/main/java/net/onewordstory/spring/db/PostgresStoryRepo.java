package net.onewordstory.spring.db;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.StoryRepoData;
import net.onewordstory.core.usecases.get_latest_stories.GlsGatewayStory;
import net.onewordstory.core.usecases.get_most_liked_stories.GmlsGatewayStory;
import net.onewordstory.core.usecases.get_story_by_id.GsbiGatewayStories;
import net.onewordstory.core.usecases.like_story.LsGatewayStory;
import net.onewordstory.core.usecases.pull_game_ended.PgeGatewayStory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class PostgresStoryRepo implements LsGatewayStory, GlsGatewayStory,
        GmlsGatewayStory, GsbiGatewayStories, PgeGatewayStory {

    private record StoryTableRow(int storyId, String story, double publishUnixTimestamp, int likes) {}

    /**
     * Maps a query result from the story table to a story table row class object
     */
    private static class StoryTableRowMapper implements RowMapper<StoryTableRow> {
        @Override
        public StoryTableRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new StoryTableRow(
                    rs.getInt("story_id"),
                    rs.getString("story"),
                    rs.getInt("publish_unix_timestamp_utc_seconds"),
                    rs.getInt("num_likes")
            );
        }
    }

    /**
     * Maps a query result from the authors table to a string of the author's name
     */
    private static class AuthorTableRowMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("author_name");
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * query DB for author names for specified story
     * @param storyId ID of story for which to get authors
     * @return string array of author names
     */
    private String[] getAuthorListForStory(int storyId) {
        return jdbcTemplate.query(
                "SELECT author_name FROM authors WHERE story_id = ?",
                new AuthorTableRowMapper(), storyId).toArray(new String[0]);
    }

    /**
     * gets author list via additional db query, converts epoch to local date time,
     * and passes data into new StoryRepoData object.
     * @param row a story table row acquired via db query
     * @return StoryRepoData object
     */
    private StoryRepoData fromTableRow(StoryTableRow row) {
        return new StoryRepoData(row.storyId(), row.story(),
                getAuthorListForStory(row.storyId()),
                LocalDateTime.ofEpochSecond((long) row.publishUnixTimestamp(), 0, ZoneOffset.UTC),
                row.likes());
    }

    @Override
    public @NotNull RepoRes<StoryRepoData> getAllStories() {
        RepoRes<StoryRepoData> out = new RepoRes<>();

        try {
            // Get all stories
            List<StoryTableRow> rows = jdbcTemplate.query(
                    "SELECT story_id, story, publish_unix_timestamp_utc_seconds, num_likes FROM stories",
                    new StoryTableRowMapper());

            // Convert stories into story repo datas
            rows.forEach(row -> out.addRow(fromTableRow(row)));

            out.setResponse(Response.getSuccessful("Successfully retrieved stories"));

        } catch (Exception e) {
            out.setResponse(Response.getFailure("Database error occurred: " + e + ": " + e.getMessage()));
        }

        return out;
    }

    @Override
    public @NotNull RepoRes<StoryRepoData> getStoryById(int storyId) {
        RepoRes<StoryRepoData> out = new RepoRes<>();

        try {
            // Get story by ID
            List<StoryTableRow> rows = jdbcTemplate.query(
                    "SELECT story_id, story, publish_unix_timestamp_utc_seconds, num_likes FROM stories " +
                            "WHERE story_id = ?",
                    new StoryTableRowMapper(), storyId);

            if (rows.size() == 0) {
                out.setResponse(new Response(Response.ResCode.STORY_NOT_FOUND,
                        "Story with id " + storyId + " not found!"));
            } else {
                out.addRow(fromTableRow(rows.get(0)));
                out.setResponse(Response.getSuccessful("Story Found!"));
            }
        } catch (Exception e) {
            out.setResponse(Response.getFailure("Database error occurred: " + e + ": " + e.getMessage()));
        }

        return out;
    }

    @Override
    public @NotNull Response likeStory(int storyId) {
        try {
            int rowsAffected = jdbcTemplate.update("UPDATE stories SET num_likes = num_likes + 1 " +
                    "WHERE story_id = ?", storyId);

            if (rowsAffected == 0) {
                return new Response(Response.ResCode.STORY_NOT_FOUND,
                        "Story with id " + storyId + " not found!");
            } else {
                return Response.getSuccessful("Liked story successfully");
            }

        } catch (Exception e) {
            return Response.getFailure("Database error occurred: " + e + ": " + e.getMessage());
        }
    }

    @Override
    public @NotNull Response saveStory(String storyString, double publishUnixTimeStamp,
                                       @Nullable Set<String> authorDisplayNames) {
        if (authorDisplayNames == null) {
            authorDisplayNames = new HashSet<>();
        }

        try {
            // Inserts new story row and retrieves the ID of this new story
            int storyId = jdbcTemplate.query("INSERT INTO stories (story, publish_unix_timestamp_utc_seconds," +
                            "num_likes) VALUES (?, ?, 0) RETURNING story_id", (rs, rowNum) -> rs.getInt("story_id"),
                    storyString, publishUnixTimeStamp).get(0);

            for (String authorName : authorDisplayNames) {
                jdbcTemplate.update("INSERT INTO authors (story_id, author_name) VALUES (?, ?)",
                        storyId, authorName);
            }

            return Response.getSuccessful("Story saved successfully");

        } catch (Exception e) {
            return Response.getFailure("Database error occurred: " + e + ": " + e.getMessage());
        }
    }
}
