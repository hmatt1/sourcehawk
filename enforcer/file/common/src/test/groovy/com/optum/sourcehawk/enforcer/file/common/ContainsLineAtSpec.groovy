package com.optum.sourcehawk.enforcer.file.common


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

class ContainsLineAtSpec extends Specification {

    def "equals"() {
        expect:
        ContainsLineAt.containsAt('I am a line', 1)
    }

    @Unroll
    def "enforce - #expectedLine - #expectedLineNumber (passed)"() {
        given:
        ContainsLineAt containsLineAt = ContainsLineAt.containsAt(expectedLine, expectedLineNumber)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = containsLineAt.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages

        where:
        expectedLine                                                     | expectedLineNumber
        '^ Here is a special character: $'                               | 5
        'Perhaps I should include a double " and a single \' as well...' | 7
    }

    @Unroll
    def "enforce - #expectedLine (failed)"() {
        given:
        ContainsLineAt containsLineAt = ContainsLineAt.containsAt(expectedLine, expectedLineNumber)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = containsLineAt.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "File does not contain the line [$expectedLine] at line number [$expectedLineNumber]"

        where:
        expectedLine                                                     | expectedLineNumber
        '^ Here is a special character: $'                               | 2
        'Perhaps I should include a double " and a single \' as well...' | 3
    }

}
