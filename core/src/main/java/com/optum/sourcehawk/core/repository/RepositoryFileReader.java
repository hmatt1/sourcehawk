package com.optum.sourcehawk.core.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * A reader responsible for reading repository files
 *
 * @author Brian Wyka
 */
public interface RepositoryFileReader {

    /**
     * Read a file from the given repository file path
     *
     * @param repositoryFilePath the repository file path
     * @return the {@link InputStream} reference if it exists, {@link Optional#empty()} otherwise
     * @throws IOException if any error occurs obtaining the input stream
     */
    Optional<InputStream> read(final String repositoryFilePath) throws IOException;

}
