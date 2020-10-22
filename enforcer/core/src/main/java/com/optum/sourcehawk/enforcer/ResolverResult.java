package com.optum.sourcehawk.enforcer;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collection;
import java.util.Collections;

/**
 * The result of a resolver resolution
 *
 * @author Brian Wyka
 */
@Value
@Builder
public class ResolverResult {

    /**
     * A "No Updates" instance of the result
     */
    public static final ResolverResult NO_UPDATES = builder().build();

    /**
     * Whether or not the updated have been applied
     */
    @Builder.Default
    boolean updatesApplied = false;

    /**
     * A message associated with the result
     */
    @NonNull
    @Builder.Default
    Collection<String> messages = Collections.emptySet();

    /**
     * Creates an "updates applied" instance of the result with the single provided message
     *
     * @param message the message pertaining to fixes applied
     * @return the resolver result
     */
    public static ResolverResult updatesApplied(final String message) {
        return builder().updatesApplied(true).messages(Collections.singleton(message)).build();
    }

}
