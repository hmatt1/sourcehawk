package com.optum.sourcehawk.enforcer.file.docker


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

class DockerfileFromHostEqualsSpec extends Specification {

    def "equals"() {
        expect:
        DockerfileFromHostEquals.equals("hub.docker.com")
    }

    def "enforce (passed)"() {
        given:
        DockerfileFromHostEquals dockerfileFromHostEquals = DockerfileFromHostEquals.equals('hub.docker.com')
        InputStream fileInputStream = IoUtil.getResourceAsStream('/Dockerfile-default')

        when:
        EnforcerResult result = dockerfileFromHostEquals.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages
    }

    def "enforce (failed - missing FROM line)"() {
        given:
        DockerfileFromHostEquals dockerfileFromHostEquals = DockerfileFromHostEquals.equals('hub.docker.com')
        InputStream fileInputStream = IoUtil.getResourceAsStream('/Dockerfile-noFrom')

        when:
        EnforcerResult result = dockerfileFromHostEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Dockerfile is missing FROM line"
    }

    @Unroll
    def "enforce (failed - missing FROM host)"() {
        given:
        DockerfileFromHostEquals dockerfileFromHostEquals = DockerfileFromHostEquals.equals('hub.docker.com')
        InputStream fileInputStream = IoUtil.getResourceAsStream("/Dockerfile-${fileSuffix}")

        when:
        EnforcerResult result = dockerfileFromHostEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Dockerfile FROM is missing host prefix"

        where:
        fileSuffix << [ 'noFromHost', 'scratch' ]
    }

    def "enforce (failed - incorrect FROM host)"() {
        given:
        DockerfileFromHostEquals dockerfileFromHostEquals = DockerfileFromHostEquals.equals('sub.docker.com')
        InputStream fileInputStream = IoUtil.getResourceAsStream('/Dockerfile-default')

        when:
        EnforcerResult result = dockerfileFromHostEquals.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Dockerfile FROM host [hub.docker.com] does not equal [sub.docker.com]"
    }

    def "enforce (null input stream)"() {
        given:
        DockerfileFromHostEquals dockerfileFromHostEquals = DockerfileFromHostEquals.equals('hub.docker.com')

        when:
        dockerfileFromHostEquals.enforce(null)

        then:
        thrown(NullPointerException)
    }

}
