package net.onewordstory.core.entities.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WordCountPlayerStatisticData implements FillerChecker {

    public static final List<String> FILLER_WORDS = new ArrayList<>();

    static {
        FILLER_WORDS.addAll(Arrays.asList(
                "and", "an", "a", "the", "of", "is", "are"
        ));
    }

    /**
     * The following attributes are package private since
     * the setters and getters would effectively be equivalent
     * to direct access from within the package
     */
    int totalWords;
    int lessThanThreeLetterWords;
    int lessThanFourLetterWords;
    int fillerWords;
    int moreThanFiveLetterWords;

    public WordCountPlayerStatisticData() {
        totalWords = 0;
        lessThanFourLetterWords = 0;
        fillerWords = 0;
        lessThanThreeLetterWords = 0;
        moreThanFiveLetterWords = 0;
    }

    @Override
    public boolean isFiller(String word) {
        return FILLER_WORDS.contains(word.toLowerCase(Locale.ENGLISH));
    }
}
