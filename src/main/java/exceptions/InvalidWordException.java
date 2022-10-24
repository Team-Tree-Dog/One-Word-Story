package exceptions;

public class InvalidWordException extends Exception {
    public InvalidWordException(String str) {
        super(str);
    }
}
