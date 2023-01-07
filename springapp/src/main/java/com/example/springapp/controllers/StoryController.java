package com.example.springapp.controllers;

import adapters.display_data.story_data.StoryDisplayData;
import adapters.view_models.*;
import com.example.springapp.SpringApp;
import frameworks_drivers.views.CoreAPI;
import org.example.ANSI;
import org.example.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import usecases.CommentRepoData;
import usecases.Response;
import usecases.StoryRepoData;
import usecases.TitleRepoData;

import java.util.*;

@Controller
public class StoryController {

    private static class Comment {

        private String username;
        private String comment;

        public Comment() {

        }

        public Comment(String username, String comment) {
            this.username = username;
            this.comment = comment;
        }

        public String getUsername() {
            return username;
        }

        public String getComment() {
            return comment;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    private static class Suggestion {

        private String suggestedTitle;
        private int count;

        private Long id;

        public Suggestion(String suggestedTitle, int count, long id) {
            this.suggestedTitle = suggestedTitle;
            this.count = count;
            this.id = id;
        }

        public Suggestion() {
        }

        public String getSuggestedTitle() {
            return suggestedTitle;
        }

        public int getCount() {
            return count;
        }

        public void setSuggestedTitle(String suggestedTitle) {
            this.suggestedTitle = suggestedTitle;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name="get", defaultValue="latest") String storiesToGet )
            throws InterruptedException {

        CoreAPI v = SpringApp.coreAPI;
        System.out.println("Get /");

        StoryListViewModel viewM;
        Response res;
        List<StoryDisplayData> stories;

        if (storiesToGet.equals("liked")) {
            viewM = v.gmlsController.getMostLikedStories(0, 100);
        }
        else {  // Defaults to "latest"
            viewM = v.glsController.getLatestStories(100);
        }

        res = viewM.getResponseAwaitable().await();
        stories = viewM.getStoriesAwaitable().get();

        // TODO: Add error handling and frontend message (e.g stories failed to load) if res is a fail code

        model.addAttribute("stories", stories == null ? new ArrayList<>() : stories);

        return "index";
    }

    @GetMapping("/story-{id}")
    public String story(@PathVariable Long id, Model model,
                        @RequestParam(name="isfail", defaultValue="false") String isfail,
                        @RequestParam(name="isfailTitle", defaultValue="false") String isfailTitle,
                        @RequestParam(name="message", defaultValue=".") String mess) throws InterruptedException {
        CoreAPI v = SpringApp.coreAPI;

        StoryListViewModel gsbiViewM = v.gsbiController.getStoryById(id.intValue());
        GatViewModel gatViewM = v.gatController.getAllTitles(id.intValue());
        GscViewModel gscViewM = v.gscController.getStoryComments(id.intValue());

        Response gatRes = gatViewM.getResponseAwaitable().await();
        Response gsbiRes = gsbiViewM.getResponseAwaitable().await();
        Response gscRes = gscViewM.getResponseAwaitable().await();

        List<StoryDisplayData> stories = gsbiViewM.getStoriesAwaitable().get();
        List<TitleRepoData> titles = gatViewM.getSuggestedTitlesAwaitable().get();
        List<CommentRepoData> comments = gscViewM.getCommentsAwaitable().get();

        if (gsbiRes.getCode() == Response.ResCode.SUCCESS &&
            gatRes.getCode() == Response.ResCode.SUCCESS &&
            gscRes.getCode() == Response.ResCode.SUCCESS) {
            assert titles != null && stories != null && comments != null;

            List<Comment> newComments = new ArrayList<>();
            List<Suggestion> suggestions = new ArrayList<>();

            for (TitleRepoData t : titles) {
                suggestions.add(new Suggestion(t.getTitle(), t.getUpvotes(), t.getSuggestionId()));
            }

            for (CommentRepoData c : comments) {
                newComments.add(new Comment(c.getDisplayName(), c.getContent()));
            }

            model.addAttribute("story", stories.get(0));

            model.addAttribute("comments", newComments);
            model.addAttribute("suggestions", suggestions);
            model.addAttribute("comment", new Comment());

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
    public String comment(@RequestParam("id") String id, Comment comment) throws InterruptedException {
        CoreAPI v = SpringApp.coreAPI;

        long longId = Long.parseLong(id);
        System.out.printf("Received comment for story " + longId + '\n');
        System.out.printf("The username: " + comment.username + '\n');
        System.out.printf("The comment: " + comment.comment + '\n');

        CagViewModel viewM = v.cagController.commentAsGuest(comment.username, comment.comment,
                Integer.parseInt(id));

        Response res = viewM.getResponseAwaitable().await();

        boolean isfail = res.getCode() != Response.ResCode.SUCCESS;

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.newInstance();
        urlBuilder.queryParam("isfail", isfail);
        urlBuilder.queryParam("message", res.getMessage());
        Log.sendMessage(ANSI.YELLOW, "suggest-title/story/id", ANSI.CYAN, urlBuilder.toUriString());

        return "redirect:/story-" + id + urlBuilder.toUriString();
    }

    @PostMapping("suggest-title/story/{id}")
    public String suggestTitle(@RequestParam("id") String id,
                               @RequestParam(name="suggestedTitle") String suggestedTitle) throws InterruptedException {
        System.out.println("Received suggest title request story " + id + '\n');
        System.out.println("Suggested title: " + suggestedTitle + '\n');

        CoreAPI v = SpringApp.coreAPI;

        StViewModel viewM = v.stController.suggestTitle(Integer.parseInt(id), suggestedTitle);

        Response res = viewM.getResponseAwaitable().await();

        boolean isfailTitle = res.getCode() != Response.ResCode.SUCCESS;

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.newInstance();
        urlBuilder.queryParam("isfailTitle", isfailTitle);
        urlBuilder.queryParam("message", res.getMessage());
        Log.sendMessage(ANSI.YELLOW, "suggest-title/story/id", ANSI.CYAN, urlBuilder.toUriString());

        return "redirect:/story-" + id + urlBuilder.toUriString();
    }

    @PostMapping("upvote-suggestion/suggestion/{id}")
    public String upvoteSuggestedTitle(@RequestParam("id") String title,
                                       @RequestParam("storyId") String storyId) throws InterruptedException {
        CoreAPI v = SpringApp.coreAPI;

        UtViewModel utViewM = v.utController.upvoteTitle(Integer.parseInt(storyId), title);

        Thread.sleep(500);

        // We will ignore the response. If upvoting fails, we wont display anything

        System.out.println("Received upvote suggested title : " + title + '\n');

        return "redirect:/story-" + storyId;
    }


    @PostMapping("like/story/{id}")
    public String like(@RequestParam("id") String id) throws InterruptedException {
        System.out.println("Received like request for story " + id);

        CoreAPI v = SpringApp.coreAPI;

        LsViewModel viewM = v.lsController.likeStory(Integer.parseInt(id));

        Thread.sleep(500);

        return "redirect:/story-" + id;
    }
        /*
    ----------------
     */

    // There is a strange bug. If you replace story-{id} with story/{id}, the css will not be loaded
//    @GetMapping("/story-{id}")
//    public String story(@PathVariable Long id, Model model) {
//        model.addAttribute("storyId", id);
//        model.addAttribute("storyTitle", storyTitles.get(id.intValue()));
//        model.addAttribute("content", storyContent.get(id.intValue()));
//        List<Comment> comments = storyComments.get(id);
//        List<Suggestion> suggestions = suggestedTitles.get(id);
//        model.addAttribute("comments", comments);
//        model.addAttribute("suggestions", suggestions);
//        model.addAttribute("comment", new Comment());
//        return "story";
//    }
//
//
////    // There is a strange bug. If you replace story-{id} with story/{id}, the css will not be loaded
//    @PostMapping("comment/story/{id}")
//    public String comment(@RequestParam("id") String id, Comment comment) {
//        long longId = Long.parseLong(id);
//        System.out.printf("Received comment for story " + longId + '\n');
//        System.out.printf("The username: " + comment.username + '\n');
//        System.out.printf("The comment: " + comment.comment + '\n');
//        return "redirect:/story-" + id;
//    }
//
////    // There is a strange bug. If you replace story-{id} with story/{id}, the css will not be loaded
//
//
//    @PostMapping("suggest-title/story/{id}")
//    public String suggestTitle(@RequestParam("id") String id, @RequestBody String suggestedTitle) {
//        System.out.println("Received suggest title request story " + id + '\n');
//        System.out.println("Suggested title: " + suggestedTitle + '\n');
//        return "redirect:/story-" + id;
//    }
//
////    @PostMapping("upvote-suggestion/suggestion/{id}")
//    public String upvoteSuggestedTitle(@RequestParam("id") String id, @RequestParam("storyId") String storyId) {
//        System.out.println("Received upvote suggested title request with id: " + id + '\n');
//        return "redirect:/story-" + storyId;
//    }

}
