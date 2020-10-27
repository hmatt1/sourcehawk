package com.optum.sourcehawk.core.utils

import org.spockframework.util.IoUtil
import spock.lang.Specification

class ModifiablePropertiesSpec extends Specification {

    def "constructor"() {
        expect:
        new ModifiableProperties() instanceof Properties
    }

    def "load"() {
        given:
        InputStream fileInputStream = IoUtil.getResourceAsStream("/file.properties")
        Properties modifiableProperties = new ModifiableProperties()

        when:
        modifiableProperties.load(fileInputStream)

        then:
        noExceptionThrown()
    }

    def "store"() {
        given:
        InputStream fileInputStream = IoUtil.getResourceAsStream("/file.properties")
        Properties modifiableProperties = new ModifiableProperties()
        Writer stringwriter = new StringWriter()

        when:
        modifiableProperties.load(fileInputStream)

        then:
        noExceptionThrown()

        when:
        modifiableProperties.setProperty("test", "passed")
        modifiableProperties.store(stringwriter, null)

        then:
        stringwriter
    }

}
