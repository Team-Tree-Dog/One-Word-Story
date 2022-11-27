package entities;

import entities.validity_checkers.ValidityCheckerFacade;
import exceptions.InvalidWordException;

/**
 * Word Factory that creates new words
 */
public class WordFactory {

    private final ValidityCheckerFacade validityChecker;

    /**
     * Constructor for the WordFactory
     * @param v ValidityCheckerFacade which checks the word for validity
     */
    public WordFactory(ValidityCheckerFacade v) {
        this.validityChecker = v;
    }

    /**
     * Checks if string representation of the word is valid using validity checker
     * @param word string representation of the word
     * @param author the author of the word
     * @return new Word if its string representation is valid
     * @throws InvalidWordException if its string representation is not valid
     */
    public Word create(String word, Player author) throws InvalidWordException {
        String checkedWord = validityChecker.isValid(word);
        if(checkedWord == null)
            throw new InvalidWordException("The word is not valid!");
        return new Word(word, author);
    }

}
