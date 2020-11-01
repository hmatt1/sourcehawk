package com.optum.sourcehawk.exec;

import com.optum.sourcehawk.core.constants.SourcehawkConstants;
import com.optum.sourcehawk.core.scan.OutputFormat;
import com.optum.sourcehawk.core.scan.Verbosity;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Execution options to be evaluated
 *
 * @author Brian Wyka
 */
@Value
@Builder(toBuilder = true)
public class ExecOptions {

    /**
     * The root of the repository in which files will be resolved relatively to
     */
    @NonNull
    @Builder.Default
    Path repositoryRoot = Paths.get(".");

    /**
     * The output verbosity
     */
    @NonNull
    @Builder.Default
    Verbosity verbosity = Verbosity.HIGH;

    /**
     * The location of the configuration file
     */
    @NonNull
    @Builder.Default
    String configurationFileLocation = SourcehawkConstants.DEFAULT_CONFIG_FILE_NAME;

    /**
     * The scan output format
     */
    @NonNull
    @Builder.Default
    OutputFormat outputFormat = OutputFormat.CONSOLE;

    /**
     * Whether or not to fail on warnings
     */
    @Builder.Default
    boolean failOnWarnings = false;

}
