package entities.display_name_checkers;

public class DisplayNameCheckerBasic implements DisplayNameChecker {

    public static final int MAX_LENGTH = 20;

    /**
     * Evaluates display name by basic criteria. Valid if:
     * <ol>
     *     <li> Length is between 1 and {@value MAX_LENGTH} inclusive </li>
     *     <li> Contains only letters and digits </li>
     *     <li> Starts with a letter </li>
     * </ol>
     * @param displayName display name for the Player, must be trimmed first!
     * @return if the display name is valid
     */
    @Override
    public boolean checkValid(String displayName) {
        return displayName.matches("^[a-zA-Z][a-zA-Z0-9]*$") &&
                displayName.length() > 0 &&
                displayName.length() <= MAX_LENGTH;
    }
}
