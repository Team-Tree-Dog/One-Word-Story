package entities.statistics;

/**
 * Checks if a word is "filler", see method doc
 */
public interface FillerChecker {

    /**
     * While "Filler" isn’t exactly the accurate term here, we will refer to "filler" as
     * anything that doesn’t really affect the story. That is, sometimes players are trapped
     * into a position where they have to write "the", or "a". This is obviously a waste of a
     * turn and there aren’t many choices the player can make. Sometimes a player may fall into
     * a loop of not having "control" of the story and is forced to add a simple connector term.
     * The method should return true for words which generally don’t give you much control over
     * the story
     * <br><br>
     * <h3>Preconditions:</h3>
     * <ol>
     *     <li> word must be passed in with all surrounding whitespace trimmed </li>
     *     <li> word must not contain the punctuation component; only letters </li>
     *     <li> word does not have to be converted to lowercase, this method should
     *     take care of that</li>
     * </ol>
     * @param word word to check, see above preconditions
     * @return if the word is "filler" based on the method's definition of the term
     */
    boolean isFiller(String word);
}
