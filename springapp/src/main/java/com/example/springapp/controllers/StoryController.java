package com.example.springapp.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    private static final List<String> storyTitles = Arrays.asList("Story-1", "Story-2", "Story-3");
    private static final List<String> storyContent = Arrays.asList("Content-1", "Content-2", "Content-3");
    private static final List<Integer> storyLikes = Arrays.asList(0, 0, 0);

    private static final Map<Long, List<Suggestion>> suggestedTitles = new HashMap<>();

    private static final Map<Long, List<Comment>> storyComments = new HashMap<>();

    static {
        ArrayList<Comment> firstStoryComment = new ArrayList<>();
        firstStoryComment.add(new Comment("Happy person", "Great story!"));
        firstStoryComment.add(new Comment("Heartbroken person", "The worst story I`ve ever read"));
        ArrayList<Comment> secondStoryComment = new ArrayList<>();
        secondStoryComment.add(new Comment("Someone", "What is this?"));
        ArrayList<Comment> thirdStoryComment = new ArrayList<>();
        storyComments.put(0L, firstStoryComment);
        storyComments.put(1L, secondStoryComment);
        storyComments.put(2L, thirdStoryComment);
        Suggestion firstSuggestionFirstStory = new Suggestion("Cool and concise", 10,0L);
        Suggestion secondSuggestionFirstStory = new Suggestion("Boring and concise", 1000, 1L);
        List<Suggestion> firstStorySuggestions = Arrays.asList(firstSuggestionFirstStory, secondSuggestionFirstStory);
        suggestedTitles.put(0L, firstStorySuggestions);
        List<Suggestion> secondStorySuggestions = Arrays.asList();
        suggestedTitles.put(1L, secondStorySuggestions);
        Suggestion firstSuggestionThirdStory = new Suggestion("Nobody likes this", 1, 2L);
        List<Suggestion> thirdStorySuggestions = Arrays.asList(firstSuggestionThirdStory);
        suggestedTitles.put(2L, thirdStorySuggestions);
    }

    @GetMapping("/story/default")
    public String defaultStory() {
        System.out.println("Navigate to /story");
        return "story";
    }

    // There is a strange bug. If you replace story-{id} with story/{id}, the css will not be loaded
    @GetMapping("/story-{id}")
    public String story(@PathVariable Long id, Model model) {
        model.addAttribute("storyId", id);
        model.addAttribute("storyTitle", storyTitles.get(id.intValue()));
        model.addAttribute("content", storyContent.get(id.intValue()));
        List<Comment> comments = storyComments.get(id);
        List<Suggestion> suggestions = suggestedTitles.get(id);
        model.addAttribute("comments", comments);
        model.addAttribute("suggestions", suggestions);
        model.addAttribute("comment", new Comment());
        return "story";
    }


    // There is a strange bug. If you replace story-{id} with story/{id}, the css will not be loaded
    @PostMapping("comment/story/{id}")
    public String comment(@RequestParam("id") String id, Comment comment) {
        long longId = Long.parseLong(id);
        System.out.printf("Received comment for story " + longId + '\n');
        System.out.printf("The username: " + comment.username + '\n');
        System.out.printf("The comment: " + comment.comment + '\n');
        return "redirect:/story-" + id;
    }

    // There is a strange bug. If you replace story-{id} with story/{id}, the css will not be loaded
    @PostMapping("like/story/{id}")
    public String like(@RequestParam("id") String id) {
        System.out.println("Received like request for story " + id);
        return "redirect:/story-" + id;
    }

    @PostMapping("suggest-title/story/{id}")
    public String suggestTitle(@RequestParam("id") String id, @RequestBody String suggestedTitle) {
        System.out.println("Received suggest title request story " + id + '\n');
        System.out.println("Suggested title: " + suggestedTitle + '\n');
        return "redirect:/story-" + id;
    }

    @PostMapping("upvote-suggestion/suggestion/{id}")
    public String upvoteSuggestedTitle(@RequestParam("id") String id, @RequestParam("storyId") String storyId) {
        System.out.println("Received upvote suggested title request with id: " + id + '\n');
        return "redirect:/story-" + storyId;
    }

}
