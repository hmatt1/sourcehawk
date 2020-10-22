package com.optum.sourcehawk.enforcer.file.json


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

class JsonPathEqualsSpec extends Specification {

    def "equals"() {
        expect:
        JsonPathEquals.equals('$.key', 'value')
    }

    @Unroll
    def "enforce - #query = #expectedValue (passed)"() {
        given:
        JsonPathEquals jsonPathEquals = JsonPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.json')

        when:
        EnforcerResult result = jsonPathEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages

        where:
        query                   | expectedValue
        '$.make'                | 'Raleigh'
        '$.size.value'          | 60
        '$.components[0]'       | 'handlebars'
        '$.components.length()' | 6
    }

    def "enforce - map (passed)"() {
        given:
        def map = [
                '$.make'               : 'Raleigh',
                '$.size.value'         : 60,
                '$.components[0]'      : 'handlebars',
                '$.components.length()': 6
        ]
        JsonPathEquals jsonPathEquals = JsonPathEquals.equals(map)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.json')

        when:
        EnforcerResult result = jsonPathEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages
    }

    @Unroll
    def "enforce - #query = #expectedValue (failed - incorrect value)"() {
        given:
        JsonPathEquals jsonPathEquals = JsonPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.json')

        when:
        EnforcerResult result = jsonPathEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Execution of query [$query] yielded result [$actualValue] which is not equal to [$expectedValue]"

        where:
        query                   | actualValue  | expectedValue
        '$.make'                | 'Raleigh'    | 'Schwinn'
        '$.size.value'          | 60           | 58
        '$.components[0]'       | 'handlebars' | 'brakes'
        '$.components.length()' | 6            | 2
    }

    @Unroll
    def "enforce - #query = #expectedValue (failed - missing)"() {
        given:
        JsonPathEquals jsonPathEquals = JsonPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.json')

        when:
        EnforcerResult result = jsonPathEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Execution of query [$query] yielded no result"

        where:
        query             | expectedValue
        '$.class'         | 'road'
        '$.components[8]' | 'calipers'
    }

    @Unroll
    def "enforce - #query = #expectedValue (failed - query error)"() {
        given:
        JsonPathEquals jsonPathEquals = JsonPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.json')

        when:
        EnforcerResult result = jsonPathEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0].startsWith("Execution of query [$query] yielded error")

        where:
        query | expectedValue
        '$$'  | 'road'
    }

    @Unroll
    def "enforce - #query = #expectedValue (failed - null parse)"() {
        given:
        JsonPathEquals jsonPathEquals = JsonPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle-bad.json')

        when:
        EnforcerResult result = jsonPathEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0].startsWith("Query parsing resulted in error [net.minidev.json.parser.ParseException: Unexpected character (:) at position 74.")

        where:
        query | expectedValue
        '$$'  | 'road'
    }

}
