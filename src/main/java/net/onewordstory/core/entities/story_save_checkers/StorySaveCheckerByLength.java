package net.onewordstory.core.entities.story_save_checkers;

/**
 * Reject stories if they are below a certain length, else they are added.
 */
public class StorySaveCheckerByLength implements StorySaveChecker{

    private static final int MIN_CHARS = 3;

    /**
     * @param story String of the story to check if it must be saved.
     */
    @Override
    public FilterOutput filterStory(String story) {
        if (story.length() < MIN_CHARS) {return new FilterOutput(story, Action.REJECTED);}
        else {return new FilterOutput(story, Action.ACCEPTED);}
    }
}
