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
import java.util.regex.Pattern;

/**
 * An enforcer which is responsible for enforcing that file contains an entire line matching a pattern at a specific line number
 *
 * @author Brian Wyka
 */
@AllArgsConstructor(staticName = "containsMatchAt")
public class ContainsLineMatchingAt extends AbstractFileEnforcer {

    private static final String MESSAGE_TEMPLATE = "File does not contain line matching pattern [%s] at line number [%d]";

    /**
     * The pattern that the line is expected to match in the file
     */
    protected final Pattern expectedLinePattern;

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
            if ((line != null) && expectedLinePattern.matcher(line).matches()) {
                return EnforcerResult.passed();
            }
        }
        return EnforcerResult.failed(String.format(MESSAGE_TEMPLATE, expectedLinePattern.pattern(), expectedLineNumber));
    }

}
