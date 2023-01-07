package adapters.controllers;

import adapters.presenters.StoryListPresenter;
import adapters.view_models.StoryListViewModel;
import usecases.get_story_by_id.GsbiInputBoundary;

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
