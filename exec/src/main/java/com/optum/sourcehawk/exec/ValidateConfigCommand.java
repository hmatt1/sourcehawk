package com.optum.sourcehawk.exec;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.optum.sourcehawk.configuration.SourcehawkConfiguration;
import com.optum.sourcehawk.core.constants.SourcehawkConstants;
import com.optum.sourcehawk.core.utils.CollectionUtils;
import com.optum.sourcehawk.core.utils.StringUtils;
import com.optum.sourcehawk.enforcer.file.FileEnforcer;
import com.optum.sourcehawk.protocol.FileProtocol;
import lombok.val;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * CLI entry point for executing Sourcehawk validate-config command
 *
 * @author Brian Wyka
 */
@CommandLine.Command(
        name = ValidateConfigCommand.COMMAND_NAME,
        aliases = { "vc" },
        description = "Validate configuration",
        mixinStandardHelpOptions = true
)
class ValidateConfigCommand implements Callable<Integer> {

    static final String COMMAND_NAME = "validate-config";

    @CommandLine.Parameters(
            index = "0",
            arity = "1",
            description = "The path to the configuration file, use '-' to supply configuration from stdin",
            defaultValue = SourcehawkConstants.DEFAULT_CONFIG_FILE_NAME,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private Path configFilePath;

    /**
     * Bootstrap the command
     *
     * @param args the command line args
     */
    public static void main(final String... args) {
        val command = new ValidateConfigCommand();
        val status = new CommandLine(command)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .setTrimQuotes(true)
                .execute(args);
        Runtime.getRuntime()
                .halt(status);
    }

    /**
     * Validate the configuration
     *
     * @return the exit code
     */
    @Override
    public Integer call() {
        final SourcehawkConfiguration sourcehawkConfiguration;
        try {
            if (StringUtils.equals(configFilePath.toString(), "-")) {
                sourcehawkConfiguration = ConfigurationReader.parseConfiguration(System.in);
            } else if (Files.exists(configFilePath) && Files.isDirectory(configFilePath)) {
                val childConfigFilePath = this.configFilePath.resolve(SourcehawkConstants.DEFAULT_CONFIG_FILE_NAME);
                if (Files.exists(childConfigFilePath)) {
                    sourcehawkConfiguration = ConfigurationReader.parseConfiguration(childConfigFilePath);
                } else {
                    Sourcehawk.CONSOLE_RAW_LOGGER.error("Configuration file is a directory and does not contain {} file", SourcehawkConstants.DEFAULT_CONFIG_FILE_NAME);
                    return CommandLine.ExitCode.USAGE;
                }
            } else if (Files.exists(configFilePath)) {
                sourcehawkConfiguration = ConfigurationReader.parseConfiguration(configFilePath);
            } else {
                Sourcehawk.CONSOLE_RAW_LOGGER.error("Configuration not provided through stdin or via file path");
                return CommandLine.ExitCode.USAGE;
            }
        } catch (final PropertyBindingException e) {
            val context = String.format("at line %d, column %d", e.getLocation().getLineNr(), e.getLocation().getColumnNr());
            Sourcehawk.CONSOLE_RAW_LOGGER.error("* {}", deriveErrorMessage(context, e));
            return CommandLine.ExitCode.SOFTWARE;
        } catch (final Exception e) {
            Sourcehawk.CONSOLE_RAW_LOGGER.error("* {}", deriveErrorMessage("unknown", e));
            return CommandLine.ExitCode.SOFTWARE;
        }
        if (CollectionUtils.isEmpty(sourcehawkConfiguration.getConfigLocations()) && CollectionUtils.isEmpty(sourcehawkConfiguration.getFileProtocols())) {
            Sourcehawk.CONSOLE_RAW_LOGGER.warn("There are no remote configurations or file protocols in your config file, scans may produce no results");
        }
        val fileEnforcerErrors = compileFileEnforcerErrors(sourcehawkConfiguration.getFileProtocols());
        if (fileEnforcerErrors.isEmpty()) {
            Sourcehawk.CONSOLE_RAW_LOGGER.info("Congratulations, you have created a valid configuration file");
            return CommandLine.ExitCode.OK;
        }
        fileEnforcerErrors.stream()
                .map(fileEnforcerError -> String.format("* %s", fileEnforcerError))
                .forEach(Sourcehawk.CONSOLE_RAW_LOGGER::error);
        return CommandLine.ExitCode.SOFTWARE;
    }

    /**
     * Compile a collection of file enforcer errors
     *
     * @param fileProtocols the file protocols
     * @return the collection of errors
     */
    private static Collection<String> compileFileEnforcerErrors(final Collection<FileProtocol> fileProtocols) {
        if (fileProtocols == null || fileProtocols.isEmpty()) {
            return Collections.emptyList();
        }
        return fileProtocols.stream()
                .flatMap(fileProtocol -> fileProtocol.getEnforcers().stream()
                        .map(enforcer -> captureEnforcerConversionError(fileProtocol, enforcer)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Capture the enforcer conversion error if any
     *
     * @param fileProtocol the file protocol
     * @param enforcer     the enforcer to convert to file enforcer
     * @return the error if any
     */
    private static Optional<String> captureEnforcerConversionError(final FileProtocol fileProtocol, final Map<String, Object> enforcer) {
        try {
            ConfigurationReader.CONFIGURATION_DESERIALIZER.convertValue(enforcer, FileEnforcer.class);
            return Optional.empty();
        } catch (final Exception e) {
            return Optional.of(deriveErrorMessage(String.format("in file protocol '%s'", fileProtocol.getName()), e));
        }
    }

    /**
     * Derive the error message from the exception
     *
     * @param throwable the exception
     * @return the error message
     */
    private static String deriveErrorMessage(final String context, final Throwable throwable) {
        if (throwable instanceof UnrecognizedPropertyException) {
            val unrecognizedPropertyException = (UnrecognizedPropertyException) throwable;
            val referringClass = unrecognizedPropertyException.getReferringClass().getSimpleName();
            return String.format("Unrecognized property '%s' in %s %s", unrecognizedPropertyException.getPropertyName(), referringClass, context);
        } else if (throwable instanceof InvalidTypeIdException) {
            val invalidTypeIdException = (InvalidTypeIdException) throwable;
            return String.format("Unknown enforcer '%s' %s", invalidTypeIdException.getTypeId(), context);
        } else if (throwable instanceof JsonParseException) {
            return String.format("Parse error %s %s", ((JsonParseException) throwable).getLocation(), context);
        } else if (throwable.getCause() instanceof JsonProcessingException) {
            return deriveErrorMessage(context, throwable.getCause());
        }
        return throwable.getMessage();
    }

}
