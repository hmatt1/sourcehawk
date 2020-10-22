package com.optum.sourcehawk.enforcer.file.docker


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification

class DockerfileFromHasTagSpec extends Specification {

    def "equals"() {
        expect:
        DockerfileFromHasTag.allowLatest(true)
    }

    def "enforce (passed)"() {
        given:
        DockerfileFromHasTag dockerfileFromHasTag = DockerfileFromHasTag.allowLatest(false)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/Dockerfile-default')

        when:
        EnforcerResult result = dockerfileFromHasTag.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages
    }

    def "enforce (passed - allowLatest = true, latest tag found)"() {
        given:
        DockerfileFromHasTag dockerfileFromHasTag = DockerfileFromHasTag.allowLatest(true)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/Dockerfile-fromLatestTag')

        when:
        EnforcerResult result = dockerfileFromHasTag.enforce(fileInputStream)

        then:
        result
        result.passed
        !result.messages
    }

    def "enforce (failed - missing FROM line)"() {
        given:
        DockerfileFromHasTag dockerfileFromHasTag = DockerfileFromHasTag.allowLatest(true)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/Dockerfile-noFrom')

        when:
        EnforcerResult result = dockerfileFromHasTag.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Dockerfile is missing FROM line"
    }

    def "enforce (failed - missing tag in FROM)"() {
        given:
        DockerfileFromHasTag dockerfileFromHasTag = DockerfileFromHasTag.allowLatest(true)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/Dockerfile-fromNoTag')

        when:
        EnforcerResult result = dockerfileFromHasTag.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Dockerfile FROM is missing tag"
    }

    def "enforce (failed - allowLatest = false, latest tag found)"() {
        given:
        DockerfileFromHasTag dockerfileFromHasTag = DockerfileFromHasTag.allowLatest(false)
        InputStream fileInputStream = IoUtil.getResourceAsStream('/Dockerfile-fromLatestTag')

        when:
        EnforcerResult result = dockerfileFromHasTag.enforce(fileInputStream)

        then:
        result
        !result.passed
        result.messages
        result.messages[0] == "Dockerfile FROM has 'latest' tag"
    }

    def "enforce (null input stream)"() {
        given:
        DockerfileFromHasTag dockerfileFromHasTag = DockerfileFromHasTag.allowLatest(false)

        when:
        dockerfileFromHasTag.enforce(null)

        then:
        thrown(NullPointerException)
    }

}
