package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * An integer which optionally comes with a string suffix
 * <br>
 * e.g: 50s, 10%, 16px
 */
public class SymboledInteger implements Serializable {
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
     * Constructor for symboled integer with no suffix
     * @param value integer value
     */
    public SymboledInteger(@NotNull Integer value) {
        this(value, null);
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
        this.suffix = (suffix == null ? "" : suffix);
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

    /**
     * @param obj object to check equality with
     * @return true if the other object is a SymboledInteger with the same value, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SymboledInteger) {
            return ((SymboledInteger) obj).getValue().equals(this.getValue());
        } return false;
    }

    @Override
    public String toString() {
        return suffix == null ? value.toString() : value + suffix;
    }

    @NotNull
    public ObjectNode getJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("value", value);
        rootNode.put("suffix", suffix);
        return rootNode;
    }
}
