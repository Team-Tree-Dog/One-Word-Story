package net.onewordstory.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.onewordstory.core.adapters.controllers.*;
import net.onewordstory.core.adapters.display_data.comment_data.CommentDisplayData;
import net.onewordstory.core.adapters.display_data.story_data.StoryDisplayData;
import net.onewordstory.core.adapters.display_data.story_data.StoryUpdateMetadata;
import net.onewordstory.core.adapters.display_data.title_data.SuggestedTitleDisplayData;
import net.onewordstory.core.adapters.view_models.GatViewModel;
import net.onewordstory.core.adapters.view_models.GscViewModel;
import net.onewordstory.core.adapters.view_models.StoryListViewModel;
import net.onewordstory.core.usecases.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static net.onewordstory.spring.controllers.enums.StoriesRequestOptions.LIKED;

@RestController
public class StoryRestController {

    private final GmlsController gmlsController;
    private final GlsController glsController;
    private final GsbiController gsbiController;
    private final GatController gatController;

    private final GscController gscController;

    private final ObjectMapper mapper;
    @Autowired
    public StoryRestController(GmlsController gmlsController, GlsController glsController,
                               GsbiController gsbiController, GatController gatController,
                               GscController gscController) {
        this.gmlsController = gmlsController;
        this.glsController = glsController;
        this.gsbiController = gsbiController;
        this.gatController = gatController;
        this.gscController = gscController;
        mapper = new ObjectMapper();
    }

    private String mapToJson(Object object) {
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, object);
        } catch (IOException ex) {
            return "";
        }
        return writer.toString();
    }

    private StoryListViewModel getCorrespondingStories(String storiesToGet) {
        if (storiesToGet.equals(LIKED.toString()))
             return gmlsController.getMostLikedStories(0, 100);
        // Defaults to "latest"
        return glsController.getLatestStories(100);
    }

    @GetMapping(path = "/stories")
    public String getStories(@RequestParam(name="get", defaultValue="latest") String storiesToGet) throws InterruptedException {
        StoryListViewModel viewM = getCorrespondingStories(storiesToGet);
        List<StoryDisplayData> result = viewM.getStoriesAwaitable().await();
        return mapToJson(result);
    }

    @GetMapping(path="/api/story/lastStory")
    public String getLastStoryId(@RequestParam(name="get", defaultValue="latest") String storiesToGet)
            throws InterruptedException {
        StoryListViewModel viewModel = getCorrespondingStories(storiesToGet);
        StoryDisplayData result = viewModel.getStoriesAwaitable().await().get(0);
        return mapToJson(result);
    }

    @GetMapping("/api/story/metadata/{id}")
    public String storyUpdateMetadata(@PathVariable int id) throws InterruptedException {
        StoryListViewModel gsbiViewM = gsbiController.getStoryById(id);
        GatViewModel gatViewM = gatController.getAllTitles(id);
        GscViewModel gscViewM = gscController.getStoryComments(id);

        Response gatRes = gatViewM.getResponseAwaitable().await();
        Response gsbiRes = gsbiViewM.getResponseAwaitable().await();
        Response gscRes = gscViewM.getResponseAwaitable().await();

        List<StoryDisplayData> stories = gsbiViewM.getStoriesAwaitable().get();
        List<SuggestedTitleDisplayData> titles = gatViewM.getSuggestedTitlesAwaitable().get();
        List<CommentDisplayData> comments = gscViewM.getCommentsAwaitable().get();
        StoryUpdateMetadata result = null;
        if (gsbiRes.getCode() == Response.ResCode.SUCCESS &&
                gatRes.getCode() == Response.ResCode.SUCCESS &&
                gscRes.getCode() == Response.ResCode.SUCCESS) {
            assert titles != null && stories != null && comments != null;
            StoryDisplayData storyDisplayData = stories.get(0);
            result = new StoryUpdateMetadata(
                    storyDisplayData.likes(),
                    comments.size(),
                    titles.size()
                    );
        }
        else {/* TODO: Add error handling and frontend message (e.g stories failed to load) */}
        return mapToJson(result);
    }

    @GetMapping("/api/story/{id}/comments")
    public String getStoryComments(@PathVariable int id) throws InterruptedException {
        GscViewModel gscViewM = gscController.getStoryComments(id);
        Response response = gscViewM.getResponseAwaitable().await();
        List<CommentDisplayData> comments = gscViewM.getCommentsAwaitable().get();
        if (response.getCode() == Response.ResCode.SUCCESS) {
            return mapToJson(comments);
        }
        return "";
    }

    @GetMapping("/api/story/{id}/titles")
    public String getStorySuggestedTitles(@PathVariable int id) throws InterruptedException {
        GatViewModel gatViewM = gatController.getAllTitles(id);
        Response response = gatViewM.getResponseAwaitable().await();
        List<SuggestedTitleDisplayData> titles = gatViewM.getSuggestedTitlesAwaitable().get();
        if (response.getCode() == Response.ResCode.SUCCESS) {
            return mapToJson(titles);
        }
        return "";
    }

}
