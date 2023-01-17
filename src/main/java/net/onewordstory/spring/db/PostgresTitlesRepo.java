package net.onewordstory.spring.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
