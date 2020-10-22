package com.optum.sourcehawk.enforcer.file.common;

import com.optum.sourcehawk.enforcer.file.AbstractFileEnforcer;
import com.optum.sourcehawk.enforcer.EnforcerResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * An enforcer which is responsible for enforcing that file contents match exactly
 *
 * @author Brian Wyka
 */
@AllArgsConstructor(staticName = "equals")
public class ContentEquals extends AbstractFileEnforcer {

    private static final String DEFAULT_MESSAGE = "File contents do not equal that of the expected file contents";

    /**
     * The expected file contents
     */
    private final String expectedFileContents;

    /** {@inheritDoc} */
    @Override
    public EnforcerResult enforceInternal(@NonNull final InputStream actualFileInputStream) throws IOException {
        try (val expectedFileContentsReader = new BufferedReader(new StringReader(expectedFileContents));
             val actualFileContentsReader = new BufferedReader(new InputStreamReader(actualFileInputStream))) {
            if (equals(expectedFileContentsReader, actualFileContentsReader)) {
                return EnforcerResult.passed();
            }
        }
        return EnforcerResult.failed(DEFAULT_MESSAGE);
    }

    /**
     * Determine if the two buffered readers have identical contents
     *
     * @param expectedReader the expected buffered reader
     * @param actualReader the actual buffered reader
     * @return true if the identica, false otherwise
     * @throws IOException if any error occurs reading files
     */
    private static boolean equals(final BufferedReader expectedReader, final BufferedReader actualReader) throws IOException {
        if (expectedReader == actualReader) {
            return true;
        }
        String line1 = expectedReader.readLine();
        String line2 = actualReader.readLine();
        while (line1 != null && line1.equals(line2)) {
            line1 = expectedReader.readLine();
            line2 = actualReader.readLine();
        }
        return line1 == null && line2 == null;
    }

}
