package entities;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The implementation of Suggested Title Checker for the Suggest Title use case. Checks whether a given title is
 * a valid story title. This class has a default constructor
 */
public class SuggestedTitleCheckerBasic implements SuggestedTitleChecker {
    private static ArrayList<Character> VALID_PUNCTUATION =
            new ArrayList<>(Arrays.asList(',','.','"','\'',':',';',')','(','/','-',' '));
    private static int MINIMUM_TITLE_LENGTH = 3;
    private static int MAXIMUM_TITLE_LENGTH = 50;

    /*
     * CRITERIA FOR VALID TITLE
     * A title is valid if and only if it meets all the following requirements:
     * 1. Must contain only alphanumeric characters and/or
     *    punctuation marks (',','.','"','\'',':',';',')','(','/','-',' ')
     * 2. Must contain at least one letter.
     * 3. Cannot contain more than four consonants in a row.
     * 4. Cannot repeat a character more than 4 times in a row.
     * 5. Must be between 3 and 50 characters long, inclusive.
     */

    /**
     * The implementation checkValid method for this Title checker.
     * @param title the title which we want to check for validity
     * @return      true if and only if the title is valid according to the given criteria
     */
    @Override
    public boolean checkValid(String title) {
        return (checkValidCharacters(title) && checkCharacterRepetitions(title) && checkTitleLength(title)
                && checkAtLeastOneLetter(title) && checkFourInARowConsonants(title));
    }

    /**
     * Private helper method for checkValid(). Check if all characters in the suggested title are valid,
     * i.e. they are either alphabets, numbers, or valid punctuation marks as defined in
     * the static attribute VALID_PUNCTUATION.
     * @param title the title that we want to check has valid characters
     * @return      true if and only if all the characters in this title are valid
     */
    private boolean checkValidCharacters(String title) {
        for (int i = 0; i < title.length(); i++){
            if (!isValidCharacter(title.charAt(i))){ return false;}
        }
        return true;
    }

    /**
     * Private helper method for checkValidCharacters().Checks if this character is a valid character,
     * i.e. if it is a number, letter or a valid punctuation mark.
     * @param c the character we want to check is valid
     * @return  true if and only if this character is valid
     */
    private boolean isValidCharacter(char c){
        boolean isAlphanumeric = Character.isLetterOrDigit(c);
        boolean isPunctuation = false;
        for (char symbol: VALID_PUNCTUATION) {
            if (c == symbol) {
                isPunctuation = true;
                break;
            }
        }
        return (isAlphanumeric || isPunctuation);
    }

    /**
     * Private helper method for checkValid().Checks that the length for this title is within
     * the minimum and maximum allowed title length.
     * @param title the title whose length we want to check
     * @return      true if and only if the length of the title is between the constants MINIMUM_TITLE_LENGTH
     *              and MAXIMUM_TITLE_LENGTH inclusive
     */
    private boolean checkTitleLength(String title){
        return (MINIMUM_TITLE_LENGTH <= title.length() && title.length() <= MAXIMUM_TITLE_LENGTH);
    }

    /**
     * Private helper method for checkValid(). Checks that this title contains at least one letter.
     * @param title the title which we want to check contains at least one letter
     * @return      true if and only if this title contains at least one letter
     */
    private boolean checkAtLeastOneLetter(String title){
        for (int i = 0; i < title.length(); i++){
            if (Character.isLetter(title.charAt(i))){ return true;}
        }
        return false;
    }

    /**
     * Private helper method for checkValid(). Checks that this title does NOT contain four consonants in a row.
     * @param title the title we want to check
     * @return      false if and only if this title contains four consonants in a row
     */
    private boolean checkFourInARowConsonants(String title){
        for (int i = 0; i <= title.length() - 4; i++) {
            boolean allAreConsonants = false;
            for (int j = 0; j < 4; j++) {
                if (!isConsonant(title.charAt(i + j))) {
                    allAreConsonants = true;
                    break;
                }
            }
            if (allAreConsonants) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Private helper method for checkValid().Checks if this title does NOT contain a repetition of
     *  the same character 4 times in a row
     * @param title the title we want to check the character repetitions for
     * @return      false if and only if this title contains a repetition of the same character 4 times in a row
     */
    private boolean checkCharacterRepetitions(String title) {
        for (int i = 0; i < title.length() - 4; i++) {
            boolean characterRepeated = true;
            char character = title.charAt(i);
            for (int j = 1; j < 4; j++) {
                if (title.charAt(i + j) != character) {
                    characterRepeated = false;
                    break;
                }
            }
            if (characterRepeated) {
                return false;
            }
        }
        return true;
    }

    /**
     * Private helper method for checkCharacterRepetitions() and checkFourInARowConsonants(). Extracts the next
     * four characters of the title beginning from and including the given index.
     * Precondition: index <= title.length - 4
     * @param title the title which we want to extract 4 characters from
     * @param index the index from which we want to extract the next four characters
     * @return      an array of type char and length 4, where char[i] = title.charAt(index + i)
     */
    private char[] extractNextFourLetters(String title, int index){
        char[] toReturn = new char[4];
        for (int i = 0; i < 4; i ++){
            toReturn[i] = title.charAt(index + i);
        }
        return toReturn;
    }

    /**
     * Private helper method for checkFourInARowConsonants(). Checks whether the given character is a consonant.
     * @param c the character that we want to check is a consonant
     * @return  true if and only if c is a consonant, i.e. c is a letter and is not a vowel.
     */
    private boolean isConsonant(char c){
        return (Character.isLetter(c) && !(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u'));
    }
}
