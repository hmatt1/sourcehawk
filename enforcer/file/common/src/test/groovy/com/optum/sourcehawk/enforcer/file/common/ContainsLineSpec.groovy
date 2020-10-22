package com.optum.sourcehawk.enforcer.file.common


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

class ContainsLineSpec extends Specification {

    def "equals"() {
        expect:
        ContainsLine.contains('I am a line')
    }

    @Unroll
    def "enforce - #expectedLine (passed)"() {
        given:
        ContainsLine containsLine = ContainsLine.contains(expectedLine)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = containsLine.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages

        where:
        expectedLine << [
                '^ Here is a special character: $',
                'Perhaps I should include a double " and a single \' as well...'
        ]
    }

    @Unroll
    def "enforce - #expectedLine (failed)"() {
        given:
        ContainsLine containsLine = ContainsLine.contains(expectedLine)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = containsLine.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "File does not contain the line [$expectedLine]"

        where:
        expectedLine << [
                'Here is a special character: $',
                'Perhaps I should include a double " and a single \' as well'
        ]
    }

}
