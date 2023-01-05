package adapters.presenters;

import adapters.display_data.story_data.AuthorNameStringCreatorCommas;
import adapters.display_data.story_data.DateFormatterBasic;
import adapters.display_data.story_data.StoryDisplayData;
import adapters.view_models.GlsViewModel;
import usecases.FullStoryDTO;
import usecases.Response;
import usecases.get_latest_stories.GlsOutputBoundary;
import usecases.get_latest_stories.GlsOutputData;

import java.util.ArrayList;
import java.util.List;

import static usecases.Response.ResCode.SHUTTING_DOWN;

public class GlsPresenter implements GlsOutputBoundary {

    private final GlsViewModel viewM;

    /**
     * @param viewM Instance of the view model to write to
     */
    public GlsPresenter(GlsViewModel viewM) { this.viewM = viewM; }

    /**
     * Notify the view model with the retrieved
     * data.numToGet(nullable) stories
     */
    @Override
    public void putStories(GlsOutputData data) {
        if (data.stories() == null) {
            viewM.getResponseAwaitable().set(data.res());
        } else {
            // Convert to StoryDisplayData
            List<StoryDisplayData> stories = new ArrayList<>();
            for (FullStoryDTO storyData : data.stories()) {
                stories.add(StoryDisplayData.fromFullStoryDTO(
                        storyData, "No Title", new AuthorNameStringCreatorCommas(),
                        new DateFormatterBasic()
                ));
            }

            viewM.getStoriesAwaitable().set(stories);
            viewM.getResponseAwaitable().set(data.res());
        }
    }

    @Override
    public void outputShutdownServer() {
        viewM.getResponseAwaitable().set(
                new Response(SHUTTING_DOWN, "Server shutting down"));
    }
}
