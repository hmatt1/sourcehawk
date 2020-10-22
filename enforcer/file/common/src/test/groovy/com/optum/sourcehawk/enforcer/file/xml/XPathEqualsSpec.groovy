package com.optum.sourcehawk.enforcer.file.xml


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

class XPathEqualsSpec extends Specification {

    def "equals"() {
        expect:
        XPathEquals.equals('//bicycles/bicycle[@id="1"]/make/text()', 'Raleigh')
    }

    @Unroll
    def "enforce - #query = #expectedValue (passed)"() {
        given:
        XPathEquals xPathEquals = XPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.xml')

        when:
        EnforcerResult result = xPathEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages

        where:
        query                                     | expectedValue
        '//bicycles/bicycle[@id="1"]/make/text()' | 'Raleigh'
        '//bicycles/bicycle[1]/model/text()'      | 'Competition GS'
        '/*/bicycle[1]/@id'                       | '1'
    }

    def "enforce - map (passed)"() {
        given:
        def map = [
                '//bicycles/bicycle[@id="1"]/make/text()': 'Raleigh',
                '//bicycles/bicycle[1]/model/text()': 'Competition GS'
        ]
        XPathEquals xPathEquals = XPathEquals.equals(map)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.xml')

        when:
        EnforcerResult result = xPathEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages
    }

    @Unroll
    def "enforce - #query = #expectedValue (failed - incorrect value)"() {
        given:
        XPathEquals xPathEquals = XPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.xml')

        when:
        EnforcerResult result = xPathEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Execution of query [$query] yielded result [$actualValue] which is not equal to [$expectedValue]"

        where:
        query                                     | expectedValue | actualValue
        '//bicycles/bicycle[@id="1"]/make/text()' | 'Schwinn'     | 'Raleigh'
        '//bicycles/bicycle[1]/model/text()'      | 'Paramount'   | 'Competition GS'
    }

    @Unroll
    def "enforce - #query = #expectedValue (failed - missing)"() {
        given:
        XPathEquals xPathEquals = XPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.xml')

        when:
        EnforcerResult result = xPathEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Execution of query [$query] yielded no result"

        where:
        query                                     | expectedValue
        '//bicycles/bicycle[@id="3"]/make/text()' | 'Schwinn'
        '//bicycles/bicycle[2]/model/text()'      | 'Paramount'
    }

    @Unroll
    def "enforce - #query = #expectedValue (failed - query error)"() {
        given:
        XPathEquals xPathEquals = XPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/bicycle.xml')

        when:
        EnforcerResult result = xPathEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0].startsWith("Execution of query [$query] yielded error")

        where:
        query               | expectedValue
        null | 'road'
    }

    def "enforce (failed - invalid XML)"() {
        given:
        XPathEquals xPathEquals = XPathEquals.equals(query, expectedValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/not.xml')

        when:
        EnforcerResult result = xPathEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages.size() == 1
        result.messages[0].startsWith("XPath")

        where:
        query               | expectedValue
        null | 'road'
    }

}
