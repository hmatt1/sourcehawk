package com.optum.sourcehawk.enforcer.file.common


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Pattern

class ContainsLineMatchingSpec extends Specification {

    def "equals"() {
        expect:
        ContainsLineMatching.containsMatch(Pattern.compile('^I am a line expression+$'))
    }

    @Unroll
    def "enforce - #expectedLinePattern (passed)"() {
        given:
        ContainsLineMatching containsLineMatching = ContainsLineMatching.containsMatch(expectedLinePattern)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = containsLineMatching.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages

        where:
        expectedLinePattern << [
                Pattern.compile( '^I am a text file+$'),
                Pattern.compile( '^Perhaps(.*)+$')
        ]
    }

    @Unroll
    def "enforce - #expectedLinePattern (failed)"() {
        given:
        ContainsLineMatching containsLineMatching = ContainsLineMatching.containsMatch(expectedLinePattern)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = containsLineMatching.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "File does not contain line matching pattern [${expectedLinePattern.pattern()}]"

        where:
        expectedLinePattern << [
                Pattern.compile('Here'),
                Pattern.compile('Perhaps')
        ]
    }

}
