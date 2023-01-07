package adapters.display_data.story_data;

import org.jetbrains.annotations.NotNull;
import usecases.FullStoryDTO;

/**
 * Data necessary to seamlessly display the object. For example, if we are using Spring,
 * this data should be directly passed to the Model. All display-related manipulation
 * and computation should be taken care of and this should be directly displayable by
 * passing necessary variables
 *
 * @param id                Story id as it is in the database
 * @param title             Most upvoted suggested title, or an optional placeholder otherwise
 * @param content           The string content
 * @param authorString      A string displaying the list of all authors of this story
 * @param likes             number of likes this story has
 * @param publishDateString A string representing the publish date of this story
 */
public record StoryDisplayData(int id,
                               @NotNull String title,
                               @NotNull String content,
                               @NotNull String authorString,
                               int likes,
                               @NotNull String publishDateString) {

    /**
     * @param storyDTO DTO object
     * @param titlePlaceholder Title string to display if title doesn't exist
     * @param authorNameStringCreator method on how to concat the author list into a string
     * @param dateFormatter method on how to convert the publish date to a string
     * @return the StoryDisplayData converted from the FullStoryDTO based on provided implementations
     */
    public static StoryDisplayData fromFullStoryDTO(FullStoryDTO storyDTO,
                                                    String titlePlaceholder,
                                                    AuthorNameStringCreator authorNameStringCreator,
                                                    DateFormatter dateFormatter) {
        return new StoryDisplayData(
                storyDTO.storyData().getStoryId(),
                storyDTO.title() == null ? titlePlaceholder : storyDTO.title(),
                storyDTO.storyData().getStory(),
                authorNameStringCreator.createAuthorString(storyDTO.storyData().getAuthorNames()),
                storyDTO.storyData().getLikes(),
                dateFormatter.formatDate(storyDTO.storyData().getPublishTimeStamp())
        );
    }
}
