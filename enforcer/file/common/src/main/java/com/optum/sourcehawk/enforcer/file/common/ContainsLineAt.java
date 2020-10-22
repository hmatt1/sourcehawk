package com.optum.sourcehawk.enforcer.file.common;

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
 * An enforcer which is responsible for enforcing that file contains an entire line at a specific line number.
 *
 * @author Brian Wyka
 */
@AllArgsConstructor(staticName = "containsAt")
public class ContainsLineAt extends AbstractFileEnforcer {

    private static final String MESSAGE_TEMPLATE = "File does not contain the line [%s] at line number [%d]";

    /**
     * The line that is expected to be found in the file
     */
    protected final String expectedLine;

    /**
     * The line number in which the line is expected to be found within the file
     */
    protected final int expectedLineNumber;

    /** {@inheritDoc} */
    @Override
    public EnforcerResult enforceInternal(@NonNull final InputStream actualFileInputStream) throws IOException {
        try (val bufferedFileReader = new BufferedReader(new InputStreamReader((actualFileInputStream)))) {
            String line;
            int lineNumber = 1;
            while (((line = bufferedFileReader.readLine()) != null) && (lineNumber <= expectedLineNumber)) {
                if (lineNumber == expectedLineNumber) {
                    break;
                }
                lineNumber++;
            }
            if (StringUtils.equals(StringUtils.removeNewLines(expectedLine), StringUtils.removeNewLines(line))) {
                return EnforcerResult.passed();
            }
        }
        return EnforcerResult.failed(String.format(MESSAGE_TEMPLATE, expectedLine, expectedLineNumber));
    }

}
