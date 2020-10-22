package com.optum.sourcehawk.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Map Utilities
 *
 * @author Brian Wyka
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapUtils {

    /**
     * Merge 2 maps containing collections as values
     *
     * @param one the first map
     * @param two the second map
     * @param <K> the type of the map's key
     * @param <V> the type of the map's value
     * @return the merged map
     */
    public static <K,V> Map<K, Collection<V>> mergeCollectionValues(final Map<K, Collection<V>> one, final Map<K, Collection<V>> two) {
        final Map<K, Collection<V>> merged = one.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
        for (val entry: two.entrySet()) {
            if (merged.containsKey(entry.getKey())) {
                merged.get(entry.getKey()).addAll(entry.getValue());
            } else {
                merged.put(entry.getKey(), entry.getValue());
            }
        }
        return merged;
    }

}
