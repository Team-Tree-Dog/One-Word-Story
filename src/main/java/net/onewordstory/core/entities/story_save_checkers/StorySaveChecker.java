package net.onewordstory.core.entities.story_save_checkers;


import javax.validation.constraints.NotNull;

/**
 * Check the story contents and decide whether the story should be saved to the blog (e.g based on length, profanity,
 * etc). Return the story unchanged or modified, along with an action code reflecting what was done
 */
public interface StorySaveChecker {

    /**
     * @param story String of the story to check if it must be saved.
     */
    @NotNull FilterOutput filterStory(String story);
}
