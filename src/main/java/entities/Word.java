package entities;

/**
 * A Word
 */
public class Word {
    private final String word;
    private final Player author;

    /**
     * Constructor for the Word
     * Trims the word before saving it
     * @param word string representation of the word
     * @param author the author of the word
     */
    public Word(String word, Player author) {
        this.word = word.trim();
        this.author = author;
    }

    /**
     * Gets the string representation of the word
     * @return word
     */
    public String getWord() { return this.word;}

    /**
     * Gets the author of the word
     * @return author
     */
    public Player getAuthor() { return this.author;}
}
