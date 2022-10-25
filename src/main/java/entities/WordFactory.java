package entities;

import exceptions.InvalidWordException;

public class WordFactory {
    private final ValidityChecker validityChecker;
    public WordFactory(ValidityChecker v) {
        this.validityChecker = v;
    }

    /**
     * Check if word string is valid using validity checker.
     * If valid, return new Word.
     * If not, throws invalid word exception
     * @param word
     * @param author
     * @return
     */
    public Word create(String word, Player author) throws InvalidWordException {
        if(validityChecker.isValid(word))
            return new Word(word, author);
        else
            throw new InvalidWordException("The word is not valid!");
    }

}
