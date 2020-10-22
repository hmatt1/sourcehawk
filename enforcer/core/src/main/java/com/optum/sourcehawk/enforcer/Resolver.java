package com.optum.sourcehawk.enforcer;

import lombok.NonNull;

import java.io.IOException;

/**
 * An interface for resolvers to adhere to
 *
 * @param <T> the type of the input to the resolver
 * @param <S> the type of the output of the resolver
 * @author Brian Wyka
 */
public interface Resolver<T, S> {

    /**
     * Resolve a certain issue on the provided input
     *
     * @param input the resolver input
     * @param output the resolver output
     * @return the resolver result
     */
    ResolverResult resolve(@NonNull final T input, @NonNull final S output) throws IOException;

}
