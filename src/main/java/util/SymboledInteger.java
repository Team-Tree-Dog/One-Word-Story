package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An integer which optionally comes with a string suffix
 * <br>
 * e.g: 50s, 10%, 16px
 */
public class SymboledInteger {
    private Integer value;
    private String suffix;

    /**
     * @param value integer value
     * @param suffix suffix for integer, or null if none
     */
    public SymboledInteger(@NotNull Integer value, @Nullable String suffix) {
        this.setValue(value);
        this.setSuffix(suffix);
    }

    /**
     * @return numerical component of this symboled integer
     */
    @NotNull
    public Integer getValue() {
        return value;
    }

    /**
     * @return Suffix component of this symboled integer, or null if there is none
     */
    @Nullable
    public String getSuffix() {
        return suffix.equals("") ? null : suffix;
    }

    /**
     * @param suffix suffix to add to this integer, or null to remove
     */
    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix == null ? "" : suffix;
    }

    /**
     * @param value value to set numeric component to
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * @param valueToAdd and integer value to add
     * @return a new SymboledInteger whose value is the sum of this and other, with the suffix
     * carried over
     */
    public SymboledInteger add (Integer valueToAdd) {
        return new SymboledInteger(this.value + valueToAdd, this.suffix);
    }
}
