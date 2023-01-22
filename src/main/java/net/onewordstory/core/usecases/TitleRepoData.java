package net.onewordstory.core.usecases;

/**
 * An object corresponding to a single suggested title from the repo. It contains the relevant data to suggest a title,
 * which are the ID corresponding to this particular suggestion request, the ID of the story for which a title is
 * suggested, the suggested title, and the number of upvotes for this title.
 */
public class TitleRepoData {
    private int suggestionId;
    private int storyId;
    private String title;
    private int upvotes;

    /**
     * The constructor for this Title Repo data object. Initializes all the attributes corresponding to the relevant
     * data to suggest a title, as outlined in the class description
     * @param suggestionId  the ID corresponding to this particular suggestion request
     * @param storyId       the ID of the story for which the title is suggested
     * @param title         the suggested title
     * @param upvotes       the number of upvotes for this title
     */
    public TitleRepoData(int suggestionId, int storyId, String title, int upvotes) {
        this.suggestionId = suggestionId;
        this.storyId = storyId;
        this.title = title;
        this.upvotes = upvotes;
    }

    public int getSuggestionId() {
        return suggestionId;
    }

    public int getStoryId() {
        return storyId;
    }

    public String getTitle() {
        return title;
    }

    public int getUpvotes() {
        return upvotes;
    }
}
