package com.optum.sourcehawk.core.utils;

import lombok.experimental.UtilityClass;

import java.util.Collection;

/**
 * Collection Utilities
 *
 * @author Brian Wyka
 */
@UtilityClass
public class CollectionUtils {

    /**
     * Determine if the collection is empty (null or has zero elements)
     *
     * @param <T> the type of the elements in the collection
     * @param collection the collection to check
     * @return true if empty, false otherwise
     */
    public <T> boolean isEmpty(final Collection<T> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Determine if the collection is NOT empty (has at least one element)
     *
     * @param <T> the type of the elements in the collection
     * @param collection the collection to check
     * @return true if NOT empty, false otherwise
     */
    public <T> boolean isNotEmpty(final Collection<T> collection) {
        return !isEmpty(collection);
    }

}
