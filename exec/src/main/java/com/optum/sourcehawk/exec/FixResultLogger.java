package com.optum.sourcehawk.exec;

import com.optum.sourcehawk.core.scan.FixResult;
import com.optum.sourcehawk.core.scan.OutputFormat;
import lombok.experimental.UtilityClass;

/**
 * A logger for scan results
 *
 * @see com.optum.sourcehawk.core.scan.FixResult
 * @see OutputFormat
 *
 * @author Brian Wyka
 */
@UtilityClass
class FixResultLogger {

    private final String MESSAGE_PASSED = "Scan passed without any errors";
    private final String MESSAGE_FAILED_TEMPLATE = "Scan resulted in failure. Error(s): %d, Warning(s): %d";

    /**
     * Log the result of the fix in the specified format
     *
     * @param fixResult the fix result
     * @param execOptions the scan options
     */
    @SuppressWarnings("squid:S2629")
    void log(final FixResult fixResult, final ExecOptions execOptions) {

    }

}
