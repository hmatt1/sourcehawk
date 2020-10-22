package com.optum.sourcehawk.enforcer.file

import com.optum.sourcehawk.enforcer.EnforcerResult
import com.optum.sourcehawk.enforcer.file.AbstractFileEnforcer
import lombok.NonNull
import org.spockframework.util.IoUtil
import spock.lang.Specification

class AbstractFileEnforcerSpec extends Specification {

    AbstractFileEnforcer abstractEnforcer = new NoopFileEnforcer()

    def "null InputStream"() {
        given:
        InputStream inputStream = null

        when:
        abstractEnforcer.enforce(inputStream)

        then:
        thrown(NullPointerException)
    }

    def "error InputStream"() {
        given:
        InputStream inputStream = IoUtil.getResourceAsStream('/file.txt')
        inputStream.close()

        when:
        EnforcerResult result = abstractEnforcer.enforce(inputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Failed to read file with error [java.io.IOException: Stream closed]"
    }

    def "valid InputStream"() {
        given:
        InputStream inputStream = IoUtil.getResourceAsStream('/file.txt')

        when:
        EnforcerResult result = abstractEnforcer.enforce(inputStream)

        then:
        result
        result.passed
        result.messages.isEmpty()
    }

    def "toString - inputStream"() {
        given:
        InputStream inputStream = IoUtil.getResourceAsStream('/file.txt')
        InputStream inputStream2 = IoUtil.getResourceAsStream('/file.txt')

        expect:
        abstractEnforcer.toString(inputStream) == inputStream2.text
    }

    private static class NoopFileEnforcer extends AbstractFileEnforcer {
        @Override
        protected EnforcerResult enforceInternal(final @NonNull InputStream actualFileInputStream) throws IOException {
            return EnforcerResult.passed();
        }
    }

}
