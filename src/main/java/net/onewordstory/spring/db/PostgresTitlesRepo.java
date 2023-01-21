package net.onewordstory.spring.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.TitleRepoData;
import net.onewordstory.core.usecases.get_all_titles.GatGatewayTitles;
import net.onewordstory.core.usecases.get_latest_stories.GlsGatewayTitles;
import net.onewordstory.core.usecases.get_most_liked_stories.GmlsGatewayTitles;
import net.onewordstory.core.usecases.get_story_by_id.GsbiGatewayTitles;
import net.onewordstory.core.usecases.suggest_title.StGatewayTitles;
import net.onewordstory.core.usecases.upvote_title.UtGatewayTitles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PostgresTitlesRepo implements GatGatewayTitles, StGatewayTitles,
        UtGatewayTitles, GlsGatewayTitles, GmlsGatewayTitles, GsbiGatewayTitles {

    /**
     * Converts a retrieved db row to title repo data
     */
    private static class TitleRepoDataMapper implements RowMapper<TitleRepoData> {
        @Override
        public TitleRepoData mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TitleRepoData(
                    rs.getInt("suggestion_id"),
                    rs.getInt("story_id"),
                    rs.getString("title"),
                    rs.getInt("upvotes")
            );
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public @NotNull Response suggestTitle(int storyId, @NotNull String titleSuggestion) {
        try {
            jdbcTemplate.update("INSERT INTO titles (story_id, title, upvotes) VALUES " +
                    "(?, ?, 0)", storyId, titleSuggestion);

            return Response.getSuccessful("Successfully added title suggestion");
        } catch (Exception e) {
            return Response.getFailure("Database error occurred: " + e + ": " + e.getMessage());
        }
    }

    @Override
    public @NotNull RepoRes<TitleRepoData> getAllTitles(int storyId) {
        RepoRes<TitleRepoData> out = new RepoRes<>();

        try {
            List<TitleRepoData> rows = jdbcTemplate.query("SELECT suggestion_id, story_id, title, upvotes FROM titles WHERE story_id = ?",
                    new TitleRepoDataMapper(), storyId);

            for (TitleRepoData row: rows) {
                out.addRow(row);
            }

            out.setResponse(Response.getSuccessful("Successfully retrieved titles"));
        } catch (Exception e) {
            out.setResponse(Response.getFailure("Database error occurred: " + e + ": " + e.getMessage()));
        }

        return out;
    }

    @Override
    public @NotNull RepoRes<String> getMostUpvotedStoryTitle(int storyId) {
        RepoRes<String> out = new RepoRes<>();

        RepoRes<TitleRepoData> allTitles = getAllTitles(storyId);

        out.setResponse(allTitles.getRes());

        // If fail means DB failed to get titles, DB fail message will return by default
        if (allTitles.getRes().getCode() == Response.ResCode.SUCCESS) {

            // Finds most upvoted title
            String maxTitle = null;
            int maxTitleUpvotes = -1;

            for (TitleRepoData titleRepoData: allTitles.getRows()) {
                if (titleRepoData.getUpvotes() > maxTitleUpvotes) {
                    maxTitleUpvotes = titleRepoData.getUpvotes();
                    maxTitle = titleRepoData.getTitle();
                }
            }

            // If no title was found for the story ID, it means either the story doesn't exist or it has no titles
            if (maxTitle == null) {
                out.setResponse(Response.getFailure("Either story doesn't exist or has no titles"));
            } else {
                out.setResponse(Response.getSuccessful("Found most upvoted title"));
                out.addRow(maxTitle);
            }
        }

        return out;
    }

    @Override
    public @NotNull Response upvoteTitle(int storyId, @NotNull String titleToUpvote) {
        try {
            int rowsAffected = jdbcTemplate.update("UPDATE titles SET upvotes = upvotes + 1 WHERE " +
                            "story_id = ? AND title = ?",
                    storyId, titleToUpvote);

            if (rowsAffected == 0) {
                return new Response(Response.ResCode.TITLE_NOT_FOUND,
                        "Title '" + titleToUpvote + "' doesn't exist for story ID " + storyId);
            }
            return Response.getSuccessful("Successfully upvoted title");
        } catch (Exception e) {
            return Response.getFailure("Database error occurred: " + e + ": " + e.getMessage());
        }
    }
}
