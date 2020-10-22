package com.optum.sourcehawk.enforcer.file.common


import com.optum.sourcehawk.enforcer.EnforcerResult
import com.optum.sourcehawk.enforcer.ResolverResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Supplier

class StringPropertyEqualsSpec extends Specification {

    def "equals"() {
        expect:
        StringPropertyEquals.equals('key', 'value')
    }

    @Unroll
    def "enforce - #propertyName - #expectedPropertyValue (passed)"() {
        given:
        StringPropertyEquals stringPropertyEquals = StringPropertyEquals.equals(propertyName, expectedPropertyValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.properties')

        when:
        EnforcerResult result = stringPropertyEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages

        where:
        propertyName | expectedPropertyValue
        'key'        | 'value'
        'foo'        | 'bar'
    }

    @Unroll
    def "enforce - parser - #propertyName - #expectedPropertyValue (passed)"() {
        given:
        StringPropertyEquals stringPropertyEquals = StringPropertyEquals.equals(propertyName, expectedPropertyValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.properties')

        when:
        EnforcerResult result = stringPropertyEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages

        where:
        propertyName | expectedPropertyValue
        'key'        | 'value'
        'foo'        | 'bar'
    }

    @Unroll
    def "enforce - #propertyName - #expectedPropertyValue (failed - missing)"() {
        given:
        StringPropertyEquals stringPropertyEquals = StringPropertyEquals.equals(propertyName, expectedPropertyValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = stringPropertyEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Property [$propertyName] is missing"

        where:
        propertyName | expectedPropertyValue
        'nope'       | 'value'
        'haha'       | 'lol'
    }

    @Unroll
    def "enforce - #propertyName - #expectedPropertyValue (failed - not equal)"() {
        given:
        StringPropertyEquals stringPropertyEquals = StringPropertyEquals.equals(propertyName, expectedPropertyValue)
        Supplier<InputStream> inputStreamSupplier = { -> IoUtil.getResourceAsStream('/file.properties') }
        InputStream fileInputStream = inputStreamSupplier.get()
        Properties properties = new Properties()
        properties.load(inputStreamSupplier.get())
        String actualValue = properties.get(propertyName)

        when:
        EnforcerResult result = stringPropertyEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Property [$propertyName] with value [${actualValue}] does not equal [$expectedPropertyValue]"

        where:
        propertyName | expectedPropertyValue
        'key'        | 'value-wrong'
        'foo'        | 'baz'
    }

    @Unroll
    def "enforce - #propertyName - #expectedPropertyValue (failed - null)"() {
        given:
        StringPropertyEquals stringPropertyEquals = StringPropertyEquals.equals(propertyName, expectedPropertyValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file-null.properties')

        when:
        EnforcerResult result = stringPropertyEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Property [$propertyName] is null"

        where:
        propertyName | expectedPropertyValue
        'key'        | 'value-wrong'
        'foo'        | 'baz'
    }

    def "resolve - updates applied (key exists)"() {
        given:
        String propertyName = 'key'
        String expectedPropertyValue = 'new_value'
        StringPropertyEquals stringPropertyEquals = StringPropertyEquals.equals(propertyName, expectedPropertyValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file-update.properties')
        Writer stringWriter = new StringWriter()

        when:
        ResolverResult result = stringPropertyEquals.resolve(fileInputStream, stringWriter)

        then:
        result
        result.updatesApplied
        result.messages
        result.messages.size() == 1
        result.messages[0] == "Property [key] with value [old_value] has been updated to value [new_value]"

        when:
        Properties newProperties = new Properties()
        newProperties.load(new StringReader(stringWriter.toString()))

        then:
        newProperties.getProperty(propertyName) == expectedPropertyValue
        newProperties.getProperty('foo') == 'bar'
    }

    def "resolve - updates applied (key does not exist)"() {
        given:
        String propertyName = 'key'
        String expectedPropertyValue = 'new_value'
        StringPropertyEquals stringPropertyEquals = StringPropertyEquals.equals(propertyName, expectedPropertyValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file-missing.properties')
        Writer stringWriter = new StringWriter()

        when:
        ResolverResult result = stringPropertyEquals.resolve(fileInputStream, stringWriter)

        then:
        result
        result.updatesApplied
        result.messages
        result.messages.size() == 1
        result.messages[0] == "Property [key] has been added with value [new_value]"

        when:
        Properties newProperties = new Properties()
        newProperties.load(new StringReader(stringWriter.toString()))

        then:
        newProperties.getProperty(propertyName) == expectedPropertyValue
    }

    def "resolve - updates not applied (not required)"() {
        given:
        String propertyName = 'foo'
        String expectedPropertyValue = 'bar'
        StringPropertyEquals stringPropertyEquals = StringPropertyEquals.equals(propertyName, expectedPropertyValue)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/file.properties')
        Writer stringWriter = new StringWriter()

        when:
        ResolverResult result = stringPropertyEquals.resolve(fileInputStream, stringWriter)

        then:
        result
        !result.updatesApplied
        !result.messages
    }

}
