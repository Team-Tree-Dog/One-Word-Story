package net.onewordstory.core.adapters.display_data.story_data;

/**
 * Data necessary to seamlessly display the object. For example, if we are using Spring,
 * this data should be directly passed to the Model. All display-related manipulation
 * and computation should be taken care of and this should be directly displayable by
 * passing necessary variables
 *
 * @param likes                   number of likes this story has
 * @param numberOfComments        number of comments this story has
 * @param numberOfSuggestedTitles number of suggested titles
 */
public record StoryUpdateMetadata(int likes,
                                  int numberOfComments,
                                  int numberOfSuggestedTitles) {
}
