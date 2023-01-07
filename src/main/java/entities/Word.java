package entities;

import org.jetbrains.annotations.Nullable;

/**
 * A Word
 */
public class Word {
    private final String word;
    private final Player author;
    private final String punctuation;

    /**
     * Constructor for the Word
     * Trims the word and punctuation before saving it
     * @param word string representation of the word
     * @param author the author of the word
     * @param punctuation string representing punctuation which comes before the word, or null if none
     */
    public Word(String word, Player author, @Nullable String punctuation) {
        this.word = word.trim();
        this.author = author;
        this.punctuation = punctuation == null ? null : punctuation.trim();
    }

    /**
     * Constructor for the Word
     * Trims the word before saving it
     * @param word string representation of the word
     * @param author the author of the word
     */
    public Word(String word, Player author) {
        this(word, author, null);
    }

    /**
     * Gets the string representation of the word
     * @return word
     */
    public String getWord() { return this.word; }

    /**
     * Gets the punctuation string
     * @return punctuation, or null if it doesn't exisrt
     */
    @Nullable
    public String getPunctuation() { return punctuation; }

    /**
     * Gets the author of the word
     * @return author
     */
    public Player getAuthor() { return this.author; }
}
