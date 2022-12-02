package entities;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The implementation of Suggested Title Checker for the Suggest Title use case. Checks whether a given title is
 * a valid story title. This class has a default constructor
 */
public class SuggestedTitleCheckerBasic implements SuggestedTitleChecker {
    static ArrayList<Character> VALID_PUNCTUATION =
            new ArrayList<Character>(Arrays.asList(',','.','"','\'',':',';',')','(','/','-'));

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

    private boolean checkValidCharacters(String title) {
        for (int i = 0; i < title.length() - 1; i++){
            if (!isValidCharacter(title.charAt(i))){ return false;}
        }
        return true;
    }

    private boolean isValidCharacter(char c){
        boolean IS_ALPHANUMERIC = Character.isLetterOrDigit(c);
        boolean IS_PUNCTUATION = false;
        for (char symbol: VALID_PUNCTUATION) {
            if (c == symbol){IS_PUNCTUATION = true;}
        }
        return (IS_ALPHANUMERIC || IS_PUNCTUATION);
    }

    private boolean checkTitleLength(String title){
        return (10 <= title.length() && title.length() <= 50);
    }

    private boolean checkAtLeastOneLetter(String title){
        for (int i = 0; i < title.length() - 1; i++){
            if (Character.isLetter(title.charAt(i))){ return true;}
        }
        return false;
    }

    private boolean checkFourInARowConsonants(String title){
        for (int i = 0; i < title.length() - 1; i++){
            if (i <= title.length() - 4){
                boolean ALL_ARE_CONSONANTS = true;
                for (int j =0; j <= 4; j++){
                    if (!isConsonant(extractNextFourLetters(title,i)[j])){ALL_ARE_CONSONANTS = false;}
                }
                if (ALL_ARE_CONSONANTS){return false;}
            }
        }
        return true;
    }

    private boolean checkCharacterRepetitions(String title){
        for (int i = 0; i < title.length() - 1; i++){
            if (i <= title.length() - 4){
                char[] NEXT_FOUR_CHARACTERS = extractNextFourLetters(title, i);
                boolean CHARACTER_REPEATED = true;
                for (int j =0; j <= 4; j++){
                    if (!(NEXT_FOUR_CHARACTERS[j] == NEXT_FOUR_CHARACTERS[0])){CHARACTER_REPEATED = false;}
                }
                if (CHARACTER_REPEATED){return false;}
            }
        }
        return true;
    }

    private char[] extractNextFourLetters(String title, int index){
        char[] TO_RETURN = new char[4];
        for (int i = 0; i <= 4; i ++){
            TO_RETURN[i] = title.charAt(index + i);
        }
        return TO_RETURN;
    }

    private boolean isConsonant(char c){
        return (Character.isLetter(c) && !(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u'));
    }

    /**
     * CRITERIA FOR VALID TITLE
     * A title is valid if and only if it meets all the following requirements:
     * 1. Must contain only alphanumeric characters and/or punctuation marks (,."';:-/)()
     * 2. Must contain at least one letter.
     * 3. Cannot contain more than four consonants in a row.
     * 4. Cannot repeat a character more than 4 times in a row.
     * 5. Must be between 10 and 50 characters long, inclusive.
     */
}
