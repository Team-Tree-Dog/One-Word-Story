package net.onewordstory.core.adapters.presenters;

import net.onewordstory.core.adapters.display_data.story_data.AuthorNameStringCreatorCommas;
import net.onewordstory.core.adapters.display_data.story_data.DateFormatterBasic;
import net.onewordstory.core.adapters.display_data.story_data.StoryDisplayData;
import net.onewordstory.core.adapters.view_models.StoryListViewModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.onewordstory.core.usecases.FullStoryDTO;
import net.onewordstory.core.usecases.Response;
import net.onewordstory.core.usecases.get_latest_stories.GlsOutputBoundary;
import net.onewordstory.core.usecases.get_most_liked_stories.GmlsOutputBoundary;
import net.onewordstory.core.usecases.get_story_by_id.GsbiOutputBoundary;

import java.util.ArrayList;
import java.util.List;

import static net.onewordstory.core.usecases.Response.ResCode.SHUTTING_DOWN;

public class StoryListPresenter implements GlsOutputBoundary, GmlsOutputBoundary, GsbiOutputBoundary {

    private final StoryListViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public StoryListPresenter(StoryListViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model with the retrieved
     * data.numToGet(nullable) stories
     */
    @Override
    public void putStories(@Nullable List<FullStoryDTO> storyDTOS, @NotNull Response res) {
        if (storyDTOS == null) {
            viewM.getResponseAwaitable().set(res);
        } else {
            // Convert to StoryDisplayData
            List<StoryDisplayData> stories = new ArrayList<>();
            for (FullStoryDTO storyData : storyDTOS) {
                stories.add(StoryDisplayData.fromFullStoryDTO(
                        storyData, Constants.NO_TITLE_STORY_PLACEHOLDER,
                        new AuthorNameStringCreatorCommas(), new DateFormatterBasic()
                ));
            }

            viewM.getStoriesAwaitable().set(stories);
            viewM.getResponseAwaitable().set(res);
        }
    }

    @Override
    public void outputShutdownServer() {
        viewM.getResponseAwaitable().set(
                new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
