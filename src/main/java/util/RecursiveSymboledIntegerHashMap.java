package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/**
 * Used to store JSON-like data where the keys are always strings
 * and the values are always SymboledInteger
 */
public class RecursiveSymboledIntegerHashMap implements Serializable {

    private final Map<String, RecursiveSymboledIntegerHashMap> map;
    private final SymboledInteger value;

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
    @Nullable
    public SymboledInteger get() {
        return value;
    }

    /**
     * Calls map.put internally, does exactly the same thing. That is,
     * old value gets overwritten if it existed
     * Note that this method will DO NOTHING is this recursive map
     * is in the base case
     * @param key a string key
     * @param value a recursive value
     */
    public void put(String key, RecursiveSymboledIntegerHashMap value) {
        if (!isBaseCase()) {
            // Cant produce null pointer because we aren't in the base case
            map.put(key, value);
        }
    }

    /**
     * Base case when (map = null) and (value != null)
     * @return If this recursive map is at its base case
     */
    public boolean isBaseCase() {
        return value != null;
    }

    /**
     * @return all the keys in the map, or null if base case
     */
    @Nullable
    public Set<String> keys() {
        return map == null ? null : map.keySet();
    }

    /**
     * @return all the values in the map, or null if base case
     */
    @Nullable
    public Collection<RecursiveSymboledIntegerHashMap> values() {
        return map == null ? null : map.values();
    }

    public ObjectNode getJsonNode() throws JsonProcessingException {
        if (isBaseCase()) {
            return value.getJsonNode();
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        for (String k: map.keySet()) {
            root.set(k, map.get(k).getJsonNode());
        }
        return root;
    }
}
