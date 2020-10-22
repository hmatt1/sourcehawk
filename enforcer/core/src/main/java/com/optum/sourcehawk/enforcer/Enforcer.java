package com.optum.sourcehawk.enforcer;

import lombok.NonNull;

import java.io.IOException;

/**
 * An interface for enforcers to adhere to
 *
 * @param <T> the type of the input to the enforcer
 * @author Brian Wyka
 */
public interface Enforcer<T> {

    /**
     * Enforce a certain validation on the provided input
     *
     * @param input the enforcer input
     * @return the enforcer result
     */
    EnforcerResult enforce(@NonNull final T input) throws IOException;

}
