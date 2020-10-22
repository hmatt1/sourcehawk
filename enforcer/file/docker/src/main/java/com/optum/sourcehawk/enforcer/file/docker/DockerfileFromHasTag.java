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
 * Enforce that the Dockerfile has a tag in the FROM line
 *
 * @author Brian Wyka
 */
@AllArgsConstructor(staticName = "allowLatest")
public class DockerfileFromHasTag extends AbstractFileEnforcer {

    private static final String FROM_TOKEN = "FROM ";
    private static final String MISSING_FROM_MESSAGE = "Dockerfile is missing FROM line";
    private static final String MISSING_TAG_MESSAGE = "Dockerfile FROM is missing tag";
    private static final String LATEST_TAG_MESSAGE = "Dockerfile FROM has 'latest' tag";
    private static final String TAG_LATEST = "latest";

    /**
     * Whether or not to allow the "latest" tag
     */
    private final boolean allowLatest;

    /** {@inheritDoc} */
    @Override
    protected EnforcerResult enforceInternal(@NonNull final InputStream actualFileInputStream) throws IOException {
        try (val dockerfileReader = new BufferedReader(new InputStreamReader(actualFileInputStream))) {
            String line;
            while ((line = dockerfileReader.readLine()) != null) {
                if (line.startsWith(FROM_TOKEN)) {
                    val actualFrom = line.substring(FROM_TOKEN.length());
                    if (actualFrom.contains(":")) {
                        val fromPieces = actualFrom.split(":");
                        if (fromPieces.length == 2) {
                            val tag = fromPieces[1];
                            if (StringUtils.equals(tag, TAG_LATEST) && !allowLatest) {
                                return EnforcerResult.failed(LATEST_TAG_MESSAGE);
                            }
                            return EnforcerResult.passed();
                        }
                    }
                    return EnforcerResult.failed(MISSING_TAG_MESSAGE);
                }
            }
        }
        return EnforcerResult.failed(MISSING_FROM_MESSAGE);
    }

}
