package usecases.get_most_liked_stories;

/**
 * Defines the abstract method for the repository to retrieve all stories
 */
public interface GmlsGateway {
    /**
     * Abstract method for the repository to retrieve all stories
     * @return all stories from the repository
     */
    GmlsGatewayOutputData getAllStories();
}
