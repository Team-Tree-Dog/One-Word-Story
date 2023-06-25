package net.onewordstory.core.entities.story_save_checkers;

/**
 * Data to pass along when a game ends
 */
public class FilterOutput {

    private final String filteredStory;
    private final Action action;

    /**
     * @param filteredStory Story after it has been filtered.
     * @param action Action that must be taken.
     */

    public FilterOutput(String filteredStory, Action action) {
        this.action = action;
        this.filteredStory = filteredStory;
    }

    public String getFilteredStory() {return filteredStory;}

    public Action getAction() {return action;}
}
