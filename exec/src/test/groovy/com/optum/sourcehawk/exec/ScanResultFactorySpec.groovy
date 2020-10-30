package com.optum.sourcehawk.exec

import com.optum.sourcehawk.core.scan.ScanResult
import com.optum.sourcehawk.core.scan.Severity
import com.optum.sourcehawk.enforcer.EnforcerResult
import com.optum.sourcehawk.enforcer.file.common.StringPropertyEquals
import com.optum.sourcehawk.protocol.FileProtocol

class ScanResultFactorySpec extends FileBaseSpecification {

    def "enforcerResult - passed"() {
        given:
        Map<String, Object> enforcers = [
                "path/file.ext": StringPropertyEquals.equals("key", "value")
        ]
        FileProtocol fileProtocol = FileProtocol.builder()
                .repositoryPath("path/file.ext")
                .name("protocol")
                .enforcers([ enforcers ])
                .build()
        EnforcerResult enforcerResult = EnforcerResult.passed()

        when:
        ScanResult scanResult = ScanResultFactory.enforcerResult(repositoryRoot.toString(), fileProtocol, enforcerResult)

        then:
        scanResult
        scanResult.passed
        scanResult.warningCount == 0
        scanResult.errorCount == 0
        scanResult.messages.size() == 0
        scanResult.formattedMessages.size() == 0
    }

    def "enforcerResult - failed (error)"() {
        given:
        Map<String, Object> enforcers = [
                "path/file.ext": StringPropertyEquals.equals("key", "value")
        ]
        FileProtocol fileProtocol = FileProtocol.builder()
                .repositoryPath("path/file.ext")
                .name("protocol")
                .enforcers([ enforcers ])
                .build()
        EnforcerResult enforcerResult = EnforcerResult.failed("Property [key] is null")

        when:
        ScanResult scanResult = ScanResultFactory.enforcerResult(fileProtocol.repositoryPath, fileProtocol, enforcerResult)

        then:
        scanResult
        !scanResult.passed
        scanResult.warningCount == 0
        scanResult.errorCount == 1
        scanResult.messages
        scanResult.messages.size() == 1
        scanResult.messages[fileProtocol.repositoryPath]
        scanResult.messages[fileProtocol.repositoryPath].size() == 1
        scanResult.formattedMessages
        scanResult.formattedMessages.size() == 1
        scanResult.formattedMessages[0] == "[ERROR] path/file.ext :: Property [key] is null"

        when:
        ScanResult.MessageDescriptor messageDescriptor = scanResult.messages[fileProtocol.repositoryPath][0]

        then:
        messageDescriptor.repositoryPath == fileProtocol.repositoryPath
        messageDescriptor.severity == fileProtocol.severity
        messageDescriptor.message == enforcerResult.messages[0]
    }

    def "enforcerResult - failed (warning)"() {
        given:
        Map<String, Object> enforcers = [
                "path/file.ext": StringPropertyEquals.equals("key", "value")
        ]
        FileProtocol fileProtocol = FileProtocol.builder()
                .repositoryPath("path/file.ext")
                .name("protocol")
                .enforcers([ enforcers ])
                .severity(Severity.WARNING.name())
                .build()
        EnforcerResult enforcerResult = EnforcerResult.builder()
                .passed(true)
                .messages(["Property [key] is null"])
                .build()

        when:
        ScanResult scanResult = ScanResultFactory.enforcerResult(fileProtocol.repositoryPath, fileProtocol, enforcerResult)

        then:
        scanResult
        scanResult.passed
        scanResult.warningCount == 1
        scanResult.errorCount == 0
        scanResult.messages.size() == 1
        scanResult.messages[fileProtocol.repositoryPath]
        scanResult.messages[fileProtocol.repositoryPath].size() == 1
        scanResult.formattedMessages
        scanResult.formattedMessages.size() == 1
        scanResult.formattedMessages[0] == "[WARNING] path/file.ext :: Property [key] is null"

        when:
        ScanResult.MessageDescriptor messageDescriptor = scanResult.messages[fileProtocol.repositoryPath][0]

        then:
        messageDescriptor.repositoryPath == fileProtocol.repositoryPath
        messageDescriptor.severity == fileProtocol.severity
        messageDescriptor.message == enforcerResult.messages[0]
    }

    def "error"() {
        given:
        String repositoryPath = "/repository/file/"
        String message = "Sourcehawk, we have a problem..."

        when:
        ScanResult scanResult = ScanResultFactory.error(repositoryPath, message)

        then:
        scanResult
        !scanResult.passed
        scanResult.errorCount == 1
        scanResult.warningCount == 0
        scanResult.formattedMessages
        scanResult.formattedMessages.size() == 1
        scanResult.formattedMessages[0] == message
        scanResult.messages
        scanResult.messages.size() == 1
        scanResult.messages[repositoryPath]
        scanResult.messages[repositoryPath].size() == 1

        when:
        ScanResult.MessageDescriptor messageDescriptor = scanResult.messages[repositoryPath][0]

        then:
        messageDescriptor
        messageDescriptor.repositoryPath == repositoryPath
        messageDescriptor.severity == Severity.ERROR.name()
        messageDescriptor.message == message
    }

    def "fileNotFound"() {
        given:
        Map<String, Object> enforcers = [
                "path/file.ext": StringPropertyEquals.equals("key", "value")
        ]
        FileProtocol fileProtocol = FileProtocol.builder()
                .repositoryPath("path/file.ext")
                .name("protocol")
                .enforcers([ enforcers ])
                .build()

        when:
        ScanResult scanResult = ScanResultFactory.fileNotFound(fileProtocol)

        then:
        scanResult
        !scanResult.passed
        scanResult.errorCount == 1
        scanResult.warningCount == 0
        scanResult.formattedMessages
        scanResult.formattedMessages.size() == 1
        scanResult.formattedMessages[0] == "[ERROR] path/file.ext :: File not found"
        scanResult.messages
        scanResult.messages.size() == 1
        scanResult.messages[fileProtocol.repositoryPath]
        scanResult.messages[fileProtocol.repositoryPath].size() == 1

        when:
        ScanResult.MessageDescriptor messageDescriptor = scanResult.messages[fileProtocol.repositoryPath][0]

        then:
        messageDescriptor
        messageDescriptor.repositoryPath == fileProtocol.repositoryPath
        messageDescriptor.severity == Severity.ERROR.name()
        messageDescriptor.message == "File not found"
    }

    def "acceptCount - RECOMMENDATION"() {
        given:
        ScanResult.ScanResultBuilder builder = ScanResult.builder()

        when:
        ScanResultFactory.acceptCount(builder, Severity.RECOMMENDATION, 5)

        then:
        noExceptionThrown()
    }

}
