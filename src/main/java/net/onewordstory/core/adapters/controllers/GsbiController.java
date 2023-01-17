package net.onewordstory.core.adapters.controllers;

import net.onewordstory.core.adapters.presenters.StoryListPresenter;
import net.onewordstory.core.adapters.view_models.StoryListViewModel;
import net.onewordstory.core.usecases.get_story_by_id.GsbiInputBoundary;

public class GsbiController {

    private final GsbiInputBoundary gsbi;

    public GsbiController(GsbiInputBoundary gsbi) {
        this.gsbi = gsbi;
    }

    /**
     * @param storyId ID of story to retrieve
     * @return a view model where you can get the output response and retrieved story
     */
    public StoryListViewModel getStoryById(int storyId) {
        StoryListViewModel viewM = new StoryListViewModel();
        StoryListPresenter pres = new StoryListPresenter(viewM);
        gsbi.getStory(storyId, pres);
        return viewM;
    }
}
