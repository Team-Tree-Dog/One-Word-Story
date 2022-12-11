package com.example.springapp.controllers;

import adapters.view_models.*;
import com.example.springapp.SpringApp;
import frameworks_drivers.views.View;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import usecases.CommentRepoData;
import usecases.Response;
import usecases.StoryRepoData;
import usecases.TitleRepoData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private static class Story {

        private Long id;

        private String title;
        private String content;
        private int likes;
        private String[] authors;
        private LocalDateTime pub;

        public Story(Long id, String title, String content, String[] authors, int likes, LocalDateTime pub) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.authors = authors;
            this.likes = likes;
            this.pub = pub;
        }

        public Story() {
        }

        private String addZeroIfNecessary(int value) {
            String result = String.valueOf(value);
            if (value < 10) {
                result = '0' + result;
            }
            return result;
        }

        public String getDateString() {
            String output = addZeroIfNecessary(pub.getMonthValue()) + "/" +
                    addZeroIfNecessary(pub.getDayOfMonth()) + '/' +
                    pub.getYear() + ' ' +
                    addZeroIfNecessary(pub.getHour()) + ':' +
                    addZeroIfNecessary(pub.getMinute()) +
                    ':' + addZeroIfNecessary(pub.getSecond());

            return output;
        }

        public Long getId() {
            return id;
        }

        public int getLikes() {
            return likes;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthorString() {
            StringBuilder s = new StringBuilder();

            for (String author: authors) {
                s.append(author).append(", ");
            }
            return s.toString();
        }

        public String getContent() {
            return content;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }



    @GetMapping("/")
    public String index(Model model, @RequestParam(name="get", defaultValue="latest") String storiesToGet ) throws InterruptedException {
        View v = SpringApp.viewRef;

        List<Story> stories = new ArrayList<>();

        if (storiesToGet.equals("liked")) {
            GmlsViewModel gmlsViewM = v.gmlsController.getMostLikedStories(0, 100);

            // Wait for output
            Thread.sleep(500);
            //gmlsViewM.awaitChange();
            // Convert to Story object
            if (gmlsViewM.getResponseCode() == Response.ResCode.SUCCESS) {
                // TODO: Rename View model method name to GMLS
                for (StoryRepoData story: gmlsViewM.getLatestStories()) {
                    // Get most upvoted title
                    GatViewModel gatViewM = v.gatController.getAllTitles(story.getStoryId());
                    Thread.sleep(500);
                    int upvotes = -1;
                    String title = "No Title";
                    for (TitleRepoData t : gatViewM.getSuggestedTitles()) {
                        if (t.getUpvotes() > upvotes) {
                            upvotes = t.getUpvotes();
                            title = t.getTitle();
                        }
                    }

                    stories.add(new Story((long) story.getStoryId(), title, story.getStory(),
                            story.getAuthorNames(), story.getLikes(), story.getPublishTimeStamp()));
                }
            }
            else {/* TODO: Add error handling and frontend message (e.g stories failed to load) */}
        }

        else {
            // Defaults to "latest"
            GlsViewModel glsViewM = v.glsController.getLatestStories(100);

            // Wait for output
            Thread.sleep(500);
            //glsViewM.awaitChange();
            // Convert to Story object
            if (glsViewM.getResponseCode() == Response.ResCode.SUCCESS) {
                for (StoryRepoData story: glsViewM.getLatestStories()) {
                    // Get most upvoted title
                    GatViewModel gatViewM = v.gatController.getAllTitles(story.getStoryId());
                    Thread.sleep(500);
                    int upvotes = -1;
                    String title = "No Title";
                    for (TitleRepoData t : gatViewM.getSuggestedTitles()) {
                        if (t.getUpvotes() > upvotes) {
                            upvotes = t.getUpvotes();
                            title = t.getTitle();
                        }
                    }

                    stories.add(new Story((long) story.getStoryId(), title, story.getStory(),
                            story.getAuthorNames(), story.getLikes(), story.getPublishTimeStamp()));
                }
            }
            else {/* TODO: Add error handling and frontend message (e.g stories failed to load) */}
        }

        model.addAttribute("stories", stories);

        System.out.println("Get /");

        return "index";
    }

    @GetMapping("/story-{id}")
    public String story(@PathVariable Long id, Model model,
                        @RequestParam(name="isfail", defaultValue="false") String isfail,
                        @RequestParam(name="isfailTitle", defaultValue="false") String isfailTitle,
                        @RequestParam(name="message", defaultValue=".") String mess) throws InterruptedException {
        View v = SpringApp.viewRef;

        // get all stories TODO: Make 1000 get ALL instead
        GlsViewModel glsViewM = v.glsController.getLatestStories(100);
        GatViewModel gatViewM = v.gatController.getAllTitles(id.intValue());
        GscViewModel gscViewM = v.gscController.getStoryComments(id.intValue());

        // Wait for output
        Thread.sleep(500);
//        glsViewM.awaitChange();
//        gatViewM.awaitChange();
//        gscViewM.awaitChange();

        if (glsViewM.getResponseCode() == Response.ResCode.SUCCESS &&
        gatViewM.getResponseCode() == Response.ResCode.SUCCESS) {
            for (StoryRepoData story: glsViewM.getLatestStories()) {
                if (story.getStoryId() == id.intValue()) {

                    List<Comment> comments = new ArrayList<>();
                    List<Suggestion> suggestions = new ArrayList<>();

                    // Get most upvoted title
                    int upvotes = -1;
                    String title = "No Title";
                    for (TitleRepoData t : gatViewM.getSuggestedTitles()) {
                        if (t.getUpvotes() > upvotes) {
                            upvotes = t.getUpvotes();
                            title = t.getTitle();
                        }

                        suggestions.add(new Suggestion(t.getTitle(), t.getUpvotes(), t.getSuggestionId()));
                    }

                    for (CommentRepoData c : gscViewM.getStoryComments()) {
                        comments.add(new Comment(c.getDisplayName(), c.getContent()));
                    }

                    model.addAttribute("storyId", id);
                    model.addAttribute("storyTitle", title);
                    model.addAttribute("content", story.getStory());

                    model.addAttribute("comments", comments);
                    model.addAttribute("suggestions", suggestions);
                    model.addAttribute("comment", new Comment());

                    model.addAttribute("isfail", Boolean.parseBoolean(isfail));
                    model.addAttribute("isfailTitle", Boolean.parseBoolean(isfailTitle));
                    model.addAttribute("message", mess);

                    break;
                }
            }
        }
        else {/* TODO: Add error handling and frontend message (e.g stories failed to load) */}

        return "story";
    }

    @GetMapping("/play")
    public String play() {
        System.out.println("Get /play");

        return "play";
    }

    @PostMapping("comment/story/{id}")
    public String comment(@RequestParam("id") String id, Comment comment) throws InterruptedException {
        View v = SpringApp.viewRef;

        long longId = Long.parseLong(id);
        System.out.printf("Received comment for story " + longId + '\n');
        System.out.printf("The username: " + comment.username + '\n');
        System.out.printf("The comment: " + comment.comment + '\n');

        CagViewModel viewM = v.cagController.commentAsGuest(comment.username, comment.comment,
                Integer.parseInt(id));

        Thread.sleep(500);

        boolean isfail = viewM.getResponseCode() != Response.ResCode.SUCCESS;

        return "redirect:/story-" + id + "?isfail=" + isfail + "&message=" + viewM.getResponseMessage();
    }

    @PostMapping("suggest-title/story/{id}")
    public String suggestTitle(@RequestParam("id") String id,
                               @RequestParam(name="suggestedTitle") String suggestedTitle) throws InterruptedException {
        System.out.println("Received suggest title request story " + id + '\n');
        System.out.println("Suggested title: " + suggestedTitle + '\n');

        View v = SpringApp.viewRef;

        StViewModel viewM = v.stController.suggestTitle(Integer.parseInt(id), suggestedTitle);

        Thread.sleep(500);

        boolean isfailTitle = viewM.getResponseCode() != Response.ResCode.SUCCESS;

        return "redirect:/story-" + id + "?isfailTitle=" + isfailTitle + "&message=" + viewM.getResponseMessage();
    }

    @PostMapping("upvote-suggestion/suggestion/{id}")
    public String upvoteSuggestedTitle(@RequestParam("id") String title,
                                       @RequestParam("storyId") String storyId) throws InterruptedException {
        View v = SpringApp.viewRef;

        UtViewModel utViewM = v.utController.upvoteTitle(Integer.parseInt(storyId), title);

        Thread.sleep(500);

        // We will ignore the response. If upvoting fails, we wont display anything

        System.out.println("Received upvote suggested title : " + title + '\n');

        return "redirect:/story-" + storyId;
    }


    @PostMapping("like/story/{id}")
    public String like(@RequestParam("id") String id) throws InterruptedException {
        System.out.println("Received like request for story " + id);

        View v = SpringApp.viewRef;

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
