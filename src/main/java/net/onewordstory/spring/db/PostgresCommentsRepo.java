package net.onewordstory.spring.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import net.onewordstory.core.usecases.CommentRepoData;
import net.onewordstory.core.usecases.RepoRes;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.comment_as_guest.CagGatewayComments;
import net.onewordstory.core.usecases.get_story_comments.GscGatewayComments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PostgresCommentsRepo implements GscGatewayComments, CagGatewayComments {

    private static class CommentRepoDataMapper implements RowMapper<CommentRepoData> {
        @Override
        public CommentRepoData mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommentRepoData(
                    rs.getInt("comment_id"),
                    rs.getInt("story_id"),
                    rs.getString("display_name"),
                    rs.getString("content")
            );
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public @NotNull Response commentAsGuest(int storyId, @NotNull String displayName, @NotNull String comment) {
        try {
            jdbcTemplate.update("INSERT INTO comments (story_id, display_name, content) VALUES " +
                    "(?, ?, ?)", storyId, displayName, comment);

            return Response.getSuccessful("Successfully added comment");
        } catch (Exception e) {
            return Response.getFailure("Database error occurred: " + e + ": " + e.getMessage());
        }
    }

    @Override
    public @NotNull RepoRes<CommentRepoData> getAllComments(int storyId) {
        RepoRes<CommentRepoData> out = new RepoRes<>();

        try {
            List<CommentRepoData> rows = jdbcTemplate.query(
                    "SELECT comment_id, story_id, display_name, content FROM comments WHERE story_id = ?",
                    new CommentRepoDataMapper(), storyId);

            for (CommentRepoData row: rows) {
                out.addRow(row);
            }

            out.setResponse(Response.getSuccessful("Successfully retrieved comments"));
        } catch (Exception e) {
            out.setResponse(Response.getFailure("Database error occurred: " + e + ": " + e.getMessage()));
        }

        return out;
    }
}
