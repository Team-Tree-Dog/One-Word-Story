package net.onewordstory.spring.controllers;

import net.onewordstory.core.adapters.controllers.*;
import net.onewordstory.core.adapters.display_data.comment_data.CommentDisplayData;
import net.onewordstory.core.adapters.display_data.comment_data.CommentDisplayDataBuilder;
import net.onewordstory.core.adapters.display_data.story_data.StoryDisplayData;
import net.onewordstory.core.adapters.display_data.title_data.SuggestedTitleDisplayData;
import net.onewordstory.core.adapters.display_data.title_data.SuggestedTitleDisplayDataBuilder;
import net.onewordstory.core.adapters.view_models.*;
import org.example.ANSI;
import org.example.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import net.onewordstory.core.usecases.Response;

import java.util.*;

@Controller
public class StoryController {

    private final GmlsController gmlsController;
    private final GlsController glsController;
    private final UtController utController;
    private final LsController lsController;
    private final CagController cagController;
    private final GscController gscController;
    private final GatController gatController;
    private final StController stController;
    private final GsbiController gsbiController;

    @Autowired
    public StoryController (GmlsController gmlsController,
                            GlsController glsController,
                            GsbiController gsbiController,
                            UtController utController,
                            LsController lsController,
                            CagController cagController,
                            GscController gscController,
                            GatController gatController,
                            StController stController) {
        this.gmlsController = gmlsController;
        this.gsbiController = gsbiController;
        this.glsController = glsController;
        this.utController = utController;
        this.lsController = lsController;
        this.cagController = cagController;
        this.gscController = gscController;
        this.gatController = gatController;
        this.stController = stController;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name="get", defaultValue="latest") String storiesToGet )
            throws InterruptedException {

        System.out.println("Get /");

        StoryListViewModel viewM;
        Response res;
        List<StoryDisplayData> stories;

        if (storiesToGet.equals("liked")) {
            viewM = gmlsController.getMostLikedStories(0, 100);
        }
        else {  // Defaults to "latest"
            viewM = glsController.getLatestStories(100);
        }

        res = viewM.getResponseAwaitable().await();
        stories = viewM.getStoriesAwaitable().get();

        // TODO: Add error handling and frontend message (e.g stories failed to load) if res is a fail code

        model.addAttribute("stories", stories == null ? new ArrayList<>() : stories);

        return "index";
    }

    @GetMapping("/story-{id}")
    public String story(@PathVariable int id, Model model,
                        @RequestParam(name="isfail", defaultValue="false") String isfail,
                        @RequestParam(name="isfailTitle", defaultValue="false") String isfailTitle,
                        @RequestParam(name="message", defaultValue=".") String mess
    ) throws InterruptedException {
        System.out.println("Get /story-"+id);

        StoryListViewModel gsbiViewM = gsbiController.getStoryById(id);
        GatViewModel gatViewM = gatController.getAllTitles(id);
        GscViewModel gscViewM = gscController.getStoryComments(id);

        Response gatRes = gatViewM.getResponseAwaitable().await();
        Response gsbiRes = gsbiViewM.getResponseAwaitable().await();
        Response gscRes = gscViewM.getResponseAwaitable().await();

        List<StoryDisplayData> stories = gsbiViewM.getStoriesAwaitable().get();
        List<SuggestedTitleDisplayData> titles = gatViewM.getSuggestedTitlesAwaitable().get();
        List<CommentDisplayData> comments = gscViewM.getCommentsAwaitable().get();

        if (gsbiRes.getCode() == Response.ResCode.SUCCESS &&
            gatRes.getCode() == Response.ResCode.SUCCESS &&
            gscRes.getCode() == Response.ResCode.SUCCESS) {
            assert titles != null && stories != null && comments != null;

            model.addAttribute("story", stories.get(0));

            model.addAttribute("comments", comments);
            model.addAttribute("suggestions", titles);

            // Empty objects, will be populated by spring form submission
            model.addAttribute("commentBuilder", new CommentDisplayDataBuilder());
            model.addAttribute("titleBuilder", new SuggestedTitleDisplayDataBuilder());

            model.addAttribute("isfail", Boolean.parseBoolean(isfail));
            model.addAttribute("isfailTitle", Boolean.parseBoolean(isfailTitle));
            model.addAttribute("message", mess);
        }
        else {/* TODO: Add error handling and frontend message (e.g stories failed to load) */}

        return "story";
    }

    @GetMapping("/play")
    public String play() {
        System.out.println("Get /play");

        return "play";
    }

    @GetMapping("/game-end")
    public String gameEnd() {
        System.out.println("Get /game-end");

        return "endscreen";
    }

    @PostMapping("comment/story/{id}")
    public String comment(@PathVariable int id,
                          @ModelAttribute CommentDisplayDataBuilder commentBuilder
    ) throws InterruptedException {
        System.out.println("Post /comment/story/" + id);
        CommentDisplayData comment = commentBuilder.build();

        System.out.println("Received comment for story " + id);
        System.out.println("The username: " + comment.displayName());
        System.out.println("The comment: " + comment.content());

        CagViewModel viewM = cagController.commentAsGuest(comment.displayName(), comment.content(), id);

        Response res = viewM.getResponseAwaitable().await();

        boolean isfail = res.getCode() != Response.ResCode.SUCCESS;

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.newInstance();
        urlBuilder.queryParam("isfail", isfail);
        urlBuilder.queryParam("message", res.getMessage());
        Log.sendMessage(ANSI.YELLOW, "suggest-title/story/id", ANSI.CYAN, urlBuilder.toUriString());

        return "redirect:/story-" + id + urlBuilder.toUriString();
    }

    @PostMapping("suggest-title/story/{id}")
    public String suggestTitle(@PathVariable int id,
                               @ModelAttribute SuggestedTitleDisplayDataBuilder builder
    ) throws InterruptedException {
        System.out.println("Post suggest-title/story/" + id);
        SuggestedTitleDisplayData titleData = builder.build();

        System.out.println("Received suggest title request story " + id);
        System.out.println("Suggested title: " + titleData.title());

        StViewModel viewM = stController.suggestTitle(id, titleData.title());

        Response res = viewM.getResponseAwaitable().await();

        boolean isfailTitle = res.getCode() != Response.ResCode.SUCCESS;

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.newInstance();
        urlBuilder.queryParam("isfailTitle", isfailTitle);
        urlBuilder.queryParam("message", res.getMessage());
        Log.sendMessage(ANSI.YELLOW, "suggest-title/story/id", ANSI.CYAN, urlBuilder.toUriString());

        return "redirect:/story-" + id + urlBuilder.toUriString();
    }

    @PostMapping("upvote-suggestion/suggestion")
    public String upvoteSuggestedTitle(@RequestParam("id") String title,
                                       @RequestParam("storyId") String storyId
    ) throws InterruptedException {
        System.out.println("Post upvote-suggestion/suggestion/" + title);

        UtViewModel utViewM = utController.upvoteTitle(Integer.parseInt(storyId), title);

        utViewM.getResponseAwaitable().await();

        // We will ignore the response. If upvoting fails, we won't display anything

        System.out.println("Received upvote suggested title : " + title + '\n');

        return "redirect:/story-" + storyId;
    }

    @PostMapping("like/story/{id}")
    public String like(
            @PathVariable int id
    ) throws InterruptedException {
        System.out.println("Received like request for story " + id);
        System.out.println("Post like/story/" + id);

        LsViewModel viewM = lsController.likeStory(id);

        viewM.getResponseAwaitable().await();

        return "redirect:/story-" + id;
    }
}
