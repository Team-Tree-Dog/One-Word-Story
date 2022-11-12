package entities;

public class NaiveValidityChecker implements ValidityChecker{

    @Override
    public boolean isValid(String word) {
        return true;
    }
}
