package com.optum.sourcehawk.enforcer.file.docker;

import com.optum.sourcehawk.core.utils.StringUtils;
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
 * Enforce that the Dockerfile has a specific host in the FROM line
 *
 * @author Brian Wyka
 */
@AllArgsConstructor(staticName = "equals")
public class DockerfileFromHostEquals extends AbstractFileEnforcer {

    private static final String FROM_TOKEN = "FROM ";
    private static final String INCORRECT_FROM_MESSAGE = "Dockerfile FROM host [%s] does not equal [%s]";
    private static final String MISSING_FROM_MESSAGE = "Dockerfile is missing FROM line";
    private static final String MISSING_HOST_MESSAGE = "Dockerfile FROM is missing host prefix";

    /**
     * The expected host that should be included in the FROM line
     */
    private final String expectedFromHost;

    /** {@inheritDoc} */
    @Override
    protected EnforcerResult enforceInternal(@NonNull final InputStream actualFileInputStream) throws IOException {
        try (val dockerfileReader = new BufferedReader(new InputStreamReader(actualFileInputStream))) {
            String line;
            while ((line = dockerfileReader.readLine()) != null) {
                if (line.startsWith(FROM_TOKEN)) {
                    val actualFrom = line.substring(FROM_TOKEN.length());
                    if (actualFrom.contains("/")) {
                        val fromPieces = actualFrom.split("/");
                        if (fromPieces[0].contains(".")) {
                            if (StringUtils.equals(expectedFromHost, fromPieces[0])) {
                                return EnforcerResult.passed();
                            }
                            return EnforcerResult.failed(String.format(INCORRECT_FROM_MESSAGE, fromPieces[0], expectedFromHost));
                        }
                    }
                    return EnforcerResult.failed(MISSING_HOST_MESSAGE);
                }
            }
        }
        return EnforcerResult.failed(MISSING_FROM_MESSAGE);
    }

}
