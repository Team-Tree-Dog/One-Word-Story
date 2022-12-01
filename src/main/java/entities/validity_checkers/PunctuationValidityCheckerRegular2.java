package entities.validity_checkers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PunctuationValidityCheckerRegular2 implements PunctuationValidityChecker {

    /**
     *
     * @param punctuation the punctuation to validate (trimmed)
     * @return null if punctuation invalid, or a modified string if the mistake is correctable
     */
    @Override
    public String isPunctuationValid(String punctuation) {
        // Actual regex, without java escapes: ^([!?]{1,3}|\.{1,3}|[,;:\-])?\"?$
        String regex = "^([!?]{1,3}|\\.{1,3}|[,;:\\-])?\\\"?$";
        Pattern p = Pattern.compile(regex);

        return p.matcher(punctuation).matches() ? punctuation : null;
    }
}
