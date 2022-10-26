package entities.exceptions;

public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException (String message) {
        super(message);
    }
}
