package net.onewordstory.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.onewordstory.core.adapters.controllers.GlsController;
import net.onewordstory.core.adapters.controllers.GmlsController;
import net.onewordstory.core.adapters.display_data.story_data.StoryDisplayData;
import net.onewordstory.core.adapters.view_models.StoryListViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static net.onewordstory.spring.controllers.enums.StoriesRequestOptions.LIKED;

@RestController
public class StoryRestController {

    private final GmlsController gmlsController;
    private final GlsController glsController;
    @Autowired
    public StoryRestController(GmlsController gmlsController, GlsController glsController) {
        this.gmlsController = gmlsController;
        this.glsController = glsController;
    }

    @GetMapping(path = "/stories")
    public String getStories(@RequestParam(name="get", defaultValue="latest") String storiesToGet) throws InterruptedException {
        StoryListViewModel viewM;
        if (storiesToGet.equals(LIKED.toString())) {
            viewM = gmlsController.getMostLikedStories(0, 100);
        }
        else {  // Defaults to "latest"
            viewM = glsController.getLatestStories(100);
        }
        List<StoryDisplayData> result = viewM.getStoriesAwaitable().await();
        StringWriter writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(writer, result);
        } catch (IOException ex) {
            return "";
        }
        return writer.toString();
    }

}
