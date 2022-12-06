package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to store JSON-like data where the keys are always strings
 * and the values are always SymboledInteger
 */
public class RecursiveSymboledIntegerHashMap {

    private Map<String, RecursiveSymboledIntegerHashMap> map;
    private SymboledInteger value;

    /**
     * Base case constructor. This map will have a base SymboledInteger value
     * and NO map. The depth here is 0, there is no further recursion
     * @param value Base case SymboledInteger value
     */
    public RecursiveSymboledIntegerHashMap(@NotNull SymboledInteger value) {
        this.value = value;
        this.map = null;
    }

    /**
     * Recursive case constructor. The SymboledInteger value will be null. There will
     * be a map which recurses into further values. The depth here is >=1
     */
    public RecursiveSymboledIntegerHashMap() {
        this.value = null;
        this.map = new HashMap<>();
    }

    /**
     * @param key the key for which to get the value in the map
     * @return the value at the key, or null if either the key doesn't exist
     * OR this recursive map is on its base case
     */
    @Nullable
    public RecursiveSymboledIntegerHashMap get(@NotNull String key) {
        return map == null ? null : map.get(key);
    }

    /**
     * @return the base case SymboledInteger value, or null if this recursive map
     * is NOT on its base case
     */
    public SymboledInteger get() {
        return value;
    }
}
