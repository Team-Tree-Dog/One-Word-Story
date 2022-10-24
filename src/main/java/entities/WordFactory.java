package entities;

import exceptions.InvalidWordException;

public class WordFactory {
    ValidityChecker validityChecker;
    WordFactory(ValidityChecker v) {
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
    Word create(String word, Player author) throws InvalidWordException {
        if(validityChecker.isValid(word))
            return new Word(word, author);
        else
            throw new InvalidWordException("The word is not valid!");
    }

}
