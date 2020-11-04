package com.optum.sourcehawk.enforcer.file.common


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Pattern

class ContainsLineMatchingAtSpec extends Specification {

    def "equals"() {
        expect:
        ContainsLineMatchingAt.containsMatchAt(Pattern.compile('^I am a line regex+$'), 3)
    }

    def "enforce - null input stream"() {
        given:
        ContainsLineMatchingAt containsLineMatchingAt = ContainsLineMatchingAt.containsMatchAt(Pattern.compile("[abc]"), 1)

        when:
        containsLineMatchingAt.enforceInternal(null)

        then:
        thrown(NullPointerException)
    }

    @Unroll
    def "enforce - #expectedLinePattern - #expectedLineNumber (passed)"() {
        given:
        ContainsLineMatchingAt containsLineMatchingAt = ContainsLineMatchingAt.containsMatchAt(expectedLinePattern, expectedLineNumber)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = containsLineMatchingAt.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages

        where:
        expectedLinePattern                    | expectedLineNumber
        Pattern.compile('^I am a text file+$') | 1
        Pattern.compile('^Perhaps(.*)+$')      | 7
    }

    @Unroll
    def "enforce - #expectedLinePattern - #expectedLineNumber (failed)"() {
        given:
        ContainsLineMatchingAt containsLineMatchingAt = ContainsLineMatchingAt.containsMatchAt(expectedLinePattern, expectedLineNumber)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = containsLineMatchingAt.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "File does not contain line matching pattern [${expectedLinePattern.pattern()}] at line number [$expectedLineNumber]"

        where:
        expectedLinePattern        | expectedLineNumber
        Pattern.compile('Here')    | 1
        Pattern.compile('Perhaps') | 7
    }

}
