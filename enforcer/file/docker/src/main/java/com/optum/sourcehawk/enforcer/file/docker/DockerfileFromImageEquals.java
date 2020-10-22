package com.optum.sourcehawk.enforcer.file.docker;

import com.optum.sourcehawk.enforcer.file.AbstractFileEnforcer;
import com.optum.sourcehawk.enforcer.EnforcerResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Enforce that the Dockerfile has a specific image in the FROM line
 *
 * @author Brian Wyka
 */
@AllArgsConstructor(staticName = "equals")
public class DockerfileFromImageEquals extends AbstractFileEnforcer {

    private static final String FROM_TOKEN = "FROM ";
    private static final String INCORRECT_FROM_MESSAGE = "Dockerfile FROM image [%s] does not contain [%s]";
    private static final String MISSING_FROM_MESSAGE = "Dockerfile is missing FROM line";
    private static final String MISSING_IMAGE_MESSAGE = "Dockerfile FROM is missing image";

    /**
     * The expected host that should be included in the FROM line
     */
    private final String expectedFromImage;

    /**
     * {@inheritDoc}
     */
    @Override
    protected EnforcerResult enforceInternal(@NonNull final InputStream actualFileInputStream) throws IOException {
        try (val dockerfileReader = new BufferedReader(new InputStreamReader(actualFileInputStream))) {
            String line;
            while ((line = dockerfileReader.readLine()) != null) {
                if (line.startsWith(FROM_TOKEN)) {
                    val actualFrom = line.substring(FROM_TOKEN.length());
                    val fromPieces = actualFrom.split("/");
                    if (fromPieces.length >= 1) {
                        val piece = fromPieces.length - 1;
                        if (fromPieces[piece].contains(expectedFromImage)) {
                            return EnforcerResult.passed();
                        }
                        return EnforcerResult.failed(String.format(INCORRECT_FROM_MESSAGE, fromPieces[piece], expectedFromImage));
                    }

                    return EnforcerResult.failed(MISSING_IMAGE_MESSAGE);
                }
            }
        }
        return EnforcerResult.failed(MISSING_FROM_MESSAGE);
    }

}
