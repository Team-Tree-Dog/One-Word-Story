package entities;

import exceptions.InvalidWordException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Story that is made up of words
 */
public class Story {

    private final List<Word> words;
    private final WordFactory wordFactory;

    /**
     Constructor for the Story
     * @param wordFactory factory which creates words
     */
    public Story(WordFactory wordFactory) {
        this.wordFactory = wordFactory;
        this.words = new ArrayList<>();
    }

    /**
     * Constructor for the Story
     * @param wordFactory factory which creates words
     * @param words an array of words
     */
    public Story(WordFactory wordFactory, Word[] words) {
        this(wordFactory);
        this.words.addAll(Arrays.asList(words));
    }

    /**
     * Adds the word to the story if it is valid
     * @param word the word that we need to add
     * @param author the author of the word
     * @throws InvalidWordException if the word is invalid
     */
    public void addWord(String word, Player author) throws InvalidWordException {
        Word newWord = wordFactory.create(word, author);
        this.words.add(newWord);
    }

    /**
     * @return The entire story in a single string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Word word: words) {
            if (word.getPunctuation() != null) builder.append(word.getPunctuation());
            builder.append(" ");
            builder.append(word.getWord());
        }
        return builder.toString();
    }
}
