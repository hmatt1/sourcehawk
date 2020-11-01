package com.optum.sourcehawk.exec

import com.optum.sourcehawk.core.constants.SourcehawkConstants
import com.optum.sourcehawk.core.scan.OutputFormat
import com.optum.sourcehawk.core.scan.Verbosity
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Paths

class AbstractCommandSpec extends Specification {

    def "buildExecOptions - defaults"() {
        given:
        AbstractCommand command = new AbstractCommand() {
            @Override
            Integer call() throws Exception {
                return 0
            }
        }

        when:
        ExecOptions execOptions = command.buildExecOptions()

        then:
        execOptions
        execOptions.repositoryRoot == Paths.get(".")
        execOptions.outputFormat == OutputFormat.CONSOLE
        execOptions.configurationFileLocation == SourcehawkConstants.DEFAULT_CONFIG_FILE_NAME
        execOptions.verbosity == Verbosity.HIGH
        !execOptions.failOnWarnings
    }

    def "buildExecOptions - provided options"() {
        given:
        AbstractCommand command = new ScanCommand(
                repositoryRootPath: Paths.get("/abc"),
                configFile: new AbstractCommand.ConfigFileExclusiveOptions(
                        path: Paths.get(".sh.yml")
                ),
                outputFormat: OutputFormat.TEXT,
                verbosity: Verbosity.MEDIUM,
                failOnWarnings: true
        )

        when:
        ExecOptions execOptions = command.buildExecOptions()

        then:
        execOptions
        execOptions.repositoryRoot == Paths.get("/abc")
        execOptions.outputFormat == OutputFormat.TEXT
        execOptions.configurationFileLocation == ".sh.yml"
        execOptions.verbosity == Verbosity.MEDIUM
        !execOptions.failOnWarnings
    }

    @Unroll
    def "buildExecOptions - provided options (#outputFormat) - verbosity downgraded"() {
        given:
        AbstractCommand command = new FixCommand(
                repositoryRootPath: Paths.get("/abc"),
                configFile: new AbstractCommand.ConfigFileExclusiveOptions(
                        path: Paths.get(".sh.yml")
                ),
                outputFormat: outputFormat,
                verbosity: Verbosity.HIGH
        )

        when:
        ExecOptions execOptions = command.buildExecOptions()

        then:
        execOptions
        execOptions.repositoryRoot == Paths.get("/abc")
        execOptions.outputFormat == outputFormat
        execOptions.configurationFileLocation == ".sh.yml"
        execOptions.verbosity == Verbosity.ZERO

        where:
        outputFormat << [OutputFormat.JSON, OutputFormat.MARKDOWN ]
    }

}
