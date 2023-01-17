package net.onewordstory.core.entities.validity_checkers;

import java.util.regex.Pattern;

public class PunctuationValidityCheckerRegular2 implements PunctuationValidityChecker {

    private final Pattern pattern;

    /**
     * Pre-compile regex
     */
    public PunctuationValidityCheckerRegular2 () {
        // Actual regex, without java escapes: ^([!?]{1,3}|\.{1,3}|[,;:\-])?\"?$
        String regex = "^([!?]{1,3}|\\.{1,3}|[,;:\\-])?\\\"?$";
        pattern = Pattern.compile(regex);
    }

    /**
     * Punctuation checker that validates according
     * to the following regex:
     * <br>
     * <code> ^([!?]{1,3}|\.{1,3}|[,;:\-])?\"?$ </code>
     * @param punctuation the punctuation to validate (trimmed)
     * @return null if punctuation invalid, or the passed punctuation if valid. This checker does
     * not perform any corrections on the input
     */
    @Override
    public String isPunctuationValid(String punctuation) {
        return pattern.matcher(punctuation).matches() ? punctuation : null;
    }
}
