package com.optum.sourcehawk.exec

import com.optum.sourcehawk.core.scan.OutputFormat
import com.optum.sourcehawk.core.scan.ScanResult
import com.optum.sourcehawk.core.scan.Severity
import com.optum.sourcehawk.core.scan.Verbosity
import spock.lang.Specification
import spock.lang.Unroll

class ScanResultLoggerSpec extends Specification {

    @Unroll
    def "log - #format (passed)"(OutputFormat format) {
        given:
        ScanResult scanResult = ScanResult.passed()
        ExecOptions execOptions = ExecOptions.builder()
                .outputFormat(format)
                .build()

        when:
        ScanResultLogger.log(scanResult, execOptions)

        then:
        noExceptionThrown()

        where:
        format << OutputFormat.values()
    }

    @Unroll
    def "log - #format (failed)"(OutputFormat format) {
        given:
        ScanResult.MessageDescriptor messageDescriptor = ScanResult.MessageDescriptor.builder()
                .severity(Severity.ERROR.name())
                .repositoryPath("file.ext")
                .message("WRONG!")
                .build()
        ScanResult scanResult = ScanResult.builder()
                .passed(false)
                .errorCount(1)
                .warningCount(0)
                .messages(["file.ext": [messageDescriptor]])
                .formattedMessages(["[ERROR] file.ext :: WRONG!"])
                .build()
        ExecOptions execOptions = ExecOptions.builder()
                .outputFormat(format)
                .build()

        when:
        ScanResultLogger.log(scanResult, execOptions)

        then:
        noExceptionThrown()

        where:
        format << OutputFormat.values()
    }

    def "formatJson"() {
        expect:
        ScanResultLogger.formatJson(null)
        ScanResultLogger.formatJson(ScanResult.builder().build())
    }

    def "formatMarkdown - passed (HIGH Verbosity)"() {
        given:
        ScanResult scanResult = ScanResult.passed()

        when:
        String markdown = ScanResultLogger.formatMarkdown(scanResult, Verbosity.HIGH)

        then:
        markdown
        markdown == """## Sourcehawk Scan

Scan passed without any errors"""
    }

    def "formatMarkdown - failed (HIGH Verbosity)"() {
        given:
        ScanResult.MessageDescriptor messageDescriptor = ScanResult.MessageDescriptor.builder()
                .severity(Severity.ERROR.name())
                .repositoryPath("file.ext")
                .message("WRONG!")
                .build()
        ScanResult scanResult = ScanResult.builder()
                .passed(false)
                .errorCount(1)
                .warningCount(0)
                .messages(["file.ext": [messageDescriptor]])
                .formattedMessages(["[ERROR] file.ext :: WRONG!"])
                .build()

        when:
        String markdown = ScanResultLogger.formatMarkdown(scanResult, Verbosity.HIGH)

        then:
        markdown
        markdown == """## Sourcehawk Scan

Scan resulted in failure. Error(s): 1, Warning(s): 0

### Results

[ERROR] file.ext :: WRONG!
"""
    }

}
