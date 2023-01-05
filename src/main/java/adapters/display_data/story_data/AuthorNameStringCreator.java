package adapters.display_data.story_data;

public interface AuthorNameStringCreator {

    /**
     * Converts an array of author names to a single string of authors
     * @param authors List of string author names
     * @return a single string which displays these authors
     */
    String createAuthorString(String[] authors);
}
