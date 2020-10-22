package com.optum.sourcehawk.enforcer.file.maven

import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

class MavenPluginsSpec extends Specification {

    def "coordinates"() {
        expect:
        MavenPlugins.coordinates(["groupId:artifactId"])
        MavenPlugins.coordinates(["groupId:artifactId:1.0.0"])
    }

    @Unroll
    def "enforce - #scenario (passed)"() {
        given:
        MavenPlugins mavenPlugins = MavenPlugins.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenPlugins.enforce(fileInputStream)

        then:
        enforcerResult
        enforcerResult.passed
        !enforcerResult.messages

        where:
        scenario                    | coordinates
        "version included"          | ["com.plugins:foo-bar:1.0.0"]
        "version omitted"           | ["com.plugins:foo-bar"]
        "multiple matches"          | ["com.plugins:foo-bar:1.0.0", "com.plugins:fizz-buzz"]
        "multiple matches versions" | ["com.plugins:foo-bar:1.0.0", "com.plugins:fizz-buzz", "com.plugins:foo:1.2.[0-9]"]
        "regex string digits"       | ["com.plugins:foo:1.\\d{1}.\\d{1}"]
        "regex string wildcard"     | ["com.plugins:foo:1.*"]
    }

    @Unroll
    def "enforce - invalid coordinates format - #scenario (failed)"() {
        given:
        MavenPlugins mavenPlugins = MavenPlugins.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenPlugins.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "The expectedCoordinates is improperly formatted, should be in format groupId:artifactId[:version]"

        where:
        scenario             | coordinates
        "empty"              | [""]
        "missing artifactId" | ["com.plugins"]
    }

    def "enforce - parse error (failed)"() {
        given:
        MavenPlugins mavenPlugins = MavenPlugins.coordinates(["com.plugins:hello-world"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom-parse-error.xml")

        when:
        EnforcerResult enforcerResult = mavenPlugins.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Maven pom.xml parsing resulted in error"
    }

    def "enforce - missing parent (failed)"() {
        given:
        MavenPlugins mavenPlugins = MavenPlugins.coordinates(["com.plugins:hello-world"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom-missing-parent.xml")

        when:
        EnforcerResult enforcerResult = mavenPlugins.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Maven pom.xml parsing resulted in error"
    }

    @Unroll
    def "enforce - incorrect - #scenario (failed)"() {
        given:
        MavenPlugins mavenPlugins = MavenPlugins.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenPlugins.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == message

        where:
        scenario     | coordinates                   | message
        "groupId"    | ["org.acme:hello-world"]      | "Maven pom.xml is missing <org.acme:hello-world> declaration"
        "artifactId" | ["com.plugins:foo-baz"]       | "Maven pom.xml is missing <com.plugins:foo-baz> declaration"
        "version"    | ["com.plugins:foo-bar:3.0.0"] | "Maven <plugin> [com.plugins:foo-bar:jar:1.0.0] version [1.0.0] does not equal or match [3.0.0]"
    }

    def "enforce - all incorrect - #scenario (failed)"() {
        given:
        MavenPlugins mavenPlugins = MavenPlugins.coordinates(["org.acme:foo-baz:3.0.0"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenPlugins.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Maven pom.xml is missing <org.acme:foo-baz:3.0.0> declaration"
    }

}
