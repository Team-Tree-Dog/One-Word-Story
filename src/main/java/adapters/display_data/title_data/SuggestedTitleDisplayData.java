package adapters.display_data.title_data;

import org.jetbrains.annotations.NotNull;
import usecases.TitleRepoData;

/**
 * Directly displayable data for a title suggestion
 *
 * @param title Suggestion title content
 * @param numUpvotes Number of upvotes
 */
public record SuggestedTitleDisplayData(@NotNull String title,
                                        int numUpvotes) implements Comparable<SuggestedTitleDisplayData> {

    public static SuggestedTitleDisplayData fromTitleRepoData(TitleRepoData repoData) {
        return new SuggestedTitleDisplayData(repoData.getTitle(), repoData.getUpvotes());
    }

    /**
     * Compares by upvotes for descending order. That is, returns positive if
     * the other item is greater
     * @param o the object to be compared.
     */
    @Override
    public int compareTo(@NotNull SuggestedTitleDisplayData o) {
        return o.numUpvotes() - numUpvotes;
    }
}
