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
     * @param wordAndPunct string representation of the word and punctuation
     * @param author the author of the word
     * @return new Word if its string representation is valid
     * @throws InvalidWordException if its string representation is not valid
     */
    public Word create(String wordAndPunct, Player author) throws InvalidWordException {
        String[] output = validityChecker.isValid(wordAndPunct);

        if(output == null)
            throw new InvalidWordException("The word is not valid!");

        String word = output[0];
        String punc = output.length == 2 ? output[1] : null;

        return new Word(word, author, punc);
    }

}
