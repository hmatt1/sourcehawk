package com.optum.sourcehawk.enforcer.file.maven


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification

class MavenBannedPropertiesSpec extends Specification {

    def "banned"() {
        expect:
        MavenBannedProperties.banned(["key": "value"])
    }

    def "enforce - null input stream"() {
        when:
        MavenBannedProperties.banned(["key": "value"]).enforceInternal(null)

        then:
        thrown(NullPointerException)
    }

    def "enforce - (passed)"() {
        given:
        MavenBannedProperties mavenParentEquals = MavenBannedProperties.banned([
                "bad": "karma",
                "even": "stevens"
        ])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        enforcerResult.passed
        !enforcerResult.messages
    }

    def "enforce - parse error (failed)"() {
        given:
        MavenBannedProperties mavenParentEquals = MavenBannedProperties.banned(["key": "value"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom-parse-error.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Maven pom.xml parsing resulted in error"
    }

    def "enforce - no properties (passed)"() {
        given:
        MavenBannedProperties mavenParentEquals = MavenBannedProperties.banned(["foo": "bar"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom-no-properties.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        enforcerResult.passed
        !enforcerResult.messages
    }

    def "enforce (failed)"() {
        given:
        MavenBannedProperties mavenParentEquals = MavenBannedProperties.banned(["foo": "bar"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Banned maven property [foo = bar] found"
    }

}
