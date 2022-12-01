package entities.validity_checkers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PunctuationValidityCheckerRegular2Tests {
    public final PunctuationValidityChecker pvc =
            new PunctuationValidityCheckerRegular2();

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Test
     */
    @Test(timeout=1000)
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
     *
     */
    @Test(timeout=1000)
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
