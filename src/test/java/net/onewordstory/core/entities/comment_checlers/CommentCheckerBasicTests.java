package net.onewordstory.core.entities.comment_checlers;

import net.onewordstory.core.entities.comment_checkers.CommentCheckerBasic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommentCheckerBasicTests {

    CommentCheckerBasic checker = new CommentCheckerBasic();

    @BeforeEach
    public void setup() {}

    @AfterEach
    public void teardown() {}

    /**
     * Tests a large quantity of valid comments and asserts each one is validated by the checker
     */
    @Test
    @Timeout(1000)
    public void testValidMany() {

        String[] validComments = {"great story", "AHHHH", "7007", "Epic 20 words", "yikes!",
        "aleksey?", "gibberish:/.,;", "'a'", "\"string\"", "[help i am trapped in this box]",
        "{i am also trapped but this box is COOLER!}", "8===D", "1 + 2 = 3", "#tests", "$10",
        "me&you", "Literally (not literally)", "Oh-", "         boo.", "email@hello"};

        for (String s : validComments) {
            assertTrue(checker.checkValid(s));
        }
    }

    @Test
    @Timeout(1000)
    public void testInvalidMany() {

        String[] invalidComments = {"", " ", "         ", "This comment is going to be " +
                "preposterously long. Like, so long it's over the character limit and gets " +
                "invalidated long. Kind of sad for the comment, but it has to be done or we'll " +
                "never verify that really long comments get invalidated. I haven't been " +
                "counting characters, so I'm just going to spam the keyboard for a bit. 3, 2, " +
                "1, djhgdhgdsoigjdopjpoidfpvjdoigfldglgnlsdfigjpfdopdosfjpdsjfpdsgjdsilghisgdsi" +
                "sdkhfdsnfsdoijgcsoigfjdsofjdspgojdsgijdsigjfpiogjfiogjdsipgjsdogijdsgopidsjoijs" +
                "dshfohdsfjpfsifpodsjfpidsjgewijweopfdocbjcxlkbnlslhdioghoisdhgoidshohsdhosdoidf" +
                "sofhsdofhdsiofhsdiofhdsiofhsdofhifjpijfpojdpqwofjwdpjfsdhfodsihgoisdhgdsigi"};

        for (String s : invalidComments) {
            assertFalse(checker.checkValid(s));
        }
    }
}
