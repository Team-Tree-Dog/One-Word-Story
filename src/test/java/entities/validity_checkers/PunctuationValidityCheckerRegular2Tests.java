package entities.validity_checkers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PunctuationValidityCheckerRegular2Tests {
    public final PunctuationValidityChecker pvc =
            new PunctuationValidityCheckerRegular2();

    @BeforeEach
    public void setUp() {}

    @AfterEach
    public void tearDown() {}

    /**
     * Test that each of "!", "?" ,"!!", "!?", "?!", "??", "!!!", "!!?", "!?!",
     * "!??", "?!!", "?!?", "??!", "???", ".", "..", "...", ";", ",", ":", "-"
     * are valid punctuations (surrounding quotes ignored). Each of the above
     * followed by quotes is also valid. Quotes along are also valid. These are not the
     * only valid combos but just a subset
     */
    @Test
    @Timeout(1000)
    public void testValidMany () {
        ArrayList<String> validCombos = new ArrayList<>(Arrays.asList(
                "!", "?" ,"!!", "!?", "?!", "??", "!!!", "!!?", "!?!",
                "!??", "?!!", "?!?", "??!", "???", ".", "..", "...", ";",
                ",", ":", "-"
        ));
        int length = validCombos.size();

        // Each combo also works with quotes at the end
        for (int i = 0; i < length; i++) {
            validCombos.add(validCombos.get(i).concat("\""));
        }
        // Quotes are valid by themselves
        validCombos.add("\"");

        for (String s : validCombos) {
            String out = pvc.isPunctuationValid(s);
            assertEquals(out, s);
        }
    }

    /**
     * Test that each of "????", "!!!!", "!?!?!", "....", "--", ".,", ",.", ":;",
     * ".!", "!.\"", "\"\"" are invalid punctuations
     */
    @Test
    @Timeout(1000)
    public void testInvalidMany () {
        ArrayList<String> invalidCombos = new ArrayList<>(Arrays.asList(
                "????", "!!!!", "!?!?!", "....", "--", ".,", ",.", ":;",
                ".!", "!.\"", "\"\""
        ));
        for (String s : invalidCombos) {
            String out = pvc.isPunctuationValid(s);
            assertNull(out);
        }
    }
}
