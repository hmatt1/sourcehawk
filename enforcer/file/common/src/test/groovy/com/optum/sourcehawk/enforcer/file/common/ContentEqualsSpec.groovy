package com.optum.sourcehawk.enforcer.file.common

import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Specification

class ContentEqualsSpec extends Specification {

    def "equals"() {
        expect:
        ContentEquals.equals('file')
    }

    def "enforce (passed)"() {
        given:
        ContentEquals contentEquals = ContentEquals.equals(IoUtil.getResourceAsStream('/file.txt').text)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = contentEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages
    }

    @Ignore // FIXME
    @Issue("https://github.optum.com/sourcehawk-projects/sourcehawk/issues/51")
    def "enforce - additional empty lines (passed)"() {
        given:
        ContentEquals contentEquals = ContentEquals.equals(IoUtil.getResourceAsStream('/file_additional_empty_lines.txt').text)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = contentEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages
    }

    def "enforce (failed)"() {
        given:
        ContentEquals contentEquals = ContentEquals.equals(IoUtil.getResourceAsStream('/file.txt').text)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/checksum.txt')

        when:
        EnforcerResult result = contentEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "File contents do not equal that of the expected file contents"
    }

}
