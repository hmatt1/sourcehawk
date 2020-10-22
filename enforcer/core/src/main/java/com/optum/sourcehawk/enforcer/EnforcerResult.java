package com.optum.sourcehawk.enforcer;

import com.optum.sourcehawk.core.utils.CollectionUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * The result of an enforcer enforcement
 *
 * @author Brian Wyka
 */
@Value
@Builder
public class EnforcerResult {

    /**
     * Whether or not the rules passed
     */
    @Builder.Default
    boolean passed = true;

    /**
     * Any messages generated during enforcement
     */
    @NonNull
    @Builder.Default
    Collection<String> messages = Collections.emptySet();

    /**
     * Create an instance indicating that all rules have passed
     *
     * @return the enforcer result
     */
    public static EnforcerResult passed() {
        return builder().build();
    }

    /**
     * Creates an instance indicating that rules have failed
     *
     * @param message a message to include
     * @return the enforcer result
     */
    public static EnforcerResult failed(final String message) {
        return builder()
                .passed(false)
                .messages(Collections.singleton(message))
                .build();
    }

    /**
     * Create an instance based on the content of {@code messages}
     *
     * @param messages the enforcer messages
     * @return the enforcer result
     */
    public static EnforcerResult create(final Collection<String> messages) {
        if (CollectionUtils.isNotEmpty(messages)) {
            return builder()
                    .passed(false)
                    .messages(messages)
                    .build();
        }
        return passed();
    }

    /**
     * Reduce two {@link EnforcerResult}s into one
     *
     * @param one the first enforcer result
     * @param two the second enforcer result
     * @return the reduced enforcer result
     */
    public static EnforcerResult reduce(final EnforcerResult one, final EnforcerResult two) {
        val formattedMessages = new HashSet<>(one.getMessages());
        formattedMessages.addAll(two.getMessages());
        return EnforcerResult.builder()
                .passed(one.passed && two.passed)
                .messages(formattedMessages)
                .build();
    }

}
