package com.optum.sourcehawk.enforcer.file.maven


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

class MavenDependenciesSpec extends Specification {

    def "coordinates"() {
        expect:
        MavenDependencies.coordinates(["groupId:artifactId"])
        MavenDependencies.coordinates(["groupId:artifactId:1.0.0"])
    }

    def "enforce - null input stream"() {
        when:
        MavenDependencies.coordinates(["org.example:acme"]).enforceInternal(null as InputStream)

        then:
        thrown(NullPointerException)
    }

    @Unroll
    def "enforce - #scenario (passed)"() {
        given:
        MavenDependencies mavenDependencies = MavenDependencies.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenDependencies.enforce(fileInputStream)

        then:
        enforcerResult
        enforcerResult.passed
        !enforcerResult.messages

        where:
        scenario                    | coordinates
        "version included"          | ["com.example:foo-bar:1.0.0"]
        "version omitted"           | ["com.example:foo-bar"]
        "multiple matches"          | ["com.example:foo-bar:1.0.0", "com.example:fizz-buzz"]
        "multiple matches versions" | ["com.example:foo-bar:1.0.0", "com.example:fizz-buzz", "com.example:foo:1.2.[0-9]"]
        "regex string digits"       | ["com.example:foo:1.\\d{1}.\\d{1}"]
        "regex string wildcard"     | ["com.example:foo:1.*"]
    }

    @Unroll
    def "enforce - invalid coordinates format - #scenario (failed)"() {
        given:
        MavenDependencies mavenDependencies = MavenDependencies.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenDependencies.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "The expectedCoordinates is improperly formatted, should be in format groupId:artifactId[:version]"

        where:
        scenario             | coordinates
        "empty"              | [""]
        "missing artifactId" | ["com.example"]
    }

    def "enforce - parse error (failed)"() {
        given:
        MavenDependencies mavenDependencies = MavenDependencies.coordinates(["com.example:hello-world"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom-parse-error.xml")

        when:
        EnforcerResult enforcerResult = mavenDependencies.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Maven pom.xml parsing resulted in error"
    }

    def "enforce - missing parent (failed)"() {
        given:
        MavenDependencies mavenDependencies = MavenDependencies.coordinates(["com.example:hello-world"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom-missing-parent.xml")

        when:
        EnforcerResult enforcerResult = mavenDependencies.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Maven pom.xml is missing <dependency> declaration"
    }

    @Unroll
    def "enforce - incorrect - #scenario (failed)"() {
        given:
        MavenDependencies mavenDependencies = MavenDependencies.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenDependencies.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == message

        where:
        scenario     | coordinates                   | message
        "groupId"    | ["org.acme:hello-world"]      | "Maven pom.xml is missing <org.acme:hello-world> declaration"
        "artifactId" | ["com.example:foo-baz"]       | "Maven pom.xml is missing <com.example:foo-baz> declaration"
        "version"    | ["com.example:foo-bar:3.0.0"] | "Maven <dependency> [com.example:foo-bar:jar:1.0.0] version [1.0.0] does not equal or match [3.0.0]"
    }

    def "enforce - all incorrect - #scenario (failed)"() {
        given:
        MavenDependencies mavenDependencies = MavenDependencies.coordinates(["org.acme:foo-baz:3.0.0"])
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenDependencies.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Maven pom.xml is missing <org.acme:foo-baz:3.0.0> declaration"
    }

}
