package com.optum.sourcehawk.enforcer.file.maven


import com.optum.sourcehawk.enforcer.EnforcerResult
import org.spockframework.util.IoUtil
import spock.lang.Specification
import spock.lang.Unroll

class MavenParentEqualsSpec extends Specification {

    def "coordinates"() {
        expect:
        MavenParentEquals.coordinates("groupId:artifactId")
        MavenParentEquals.coordinates("groupId:artifactId:1.0.0")
    }

    @Unroll
    def "enforce - #scenario (passed)"() {
        given:
        MavenParentEquals mavenParentEquals = MavenParentEquals.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        enforcerResult.passed
        !enforcerResult.messages

        where:
        scenario                 | coordinates
        "version included"       | "com.example:hello-world:1.0.0"
        "version omitted"        | "com.example:hello-world"
        "version regex"          | "com.example:hello-world:1.[0-9].[0-9]"
        "version regex digits"   | "com.example:hello-world:1.\\d+.\\d+"
        "version regex wildcard" | "com.example:hello-world:1.*"
    }

    @Unroll
    def "enforce - invalid coordinates format - #scenario (failed)"() {
        given:
        MavenParentEquals mavenParentEquals = MavenParentEquals.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "The expectedCoordinates is improperly formatted, should be in format groupId:artifactId[:version]"

        where:
        scenario             | coordinates
        "empty"              | ""
        "missing artifactId" | "com.example"
    }

    def "enforce - parse error (failed)"() {
        given:
        MavenParentEquals mavenParentEquals = MavenParentEquals.coordinates("com.example:hello-world")
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

    def "enforce - missing parent (failed)"() {
        given:
        MavenParentEquals mavenParentEquals = MavenParentEquals.coordinates("com.example:hello-world")
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom-missing-parent.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == "Maven pom.xml is missing <parent> declaration"
    }

    @Unroll
    def "enforce - incorrect - #scenario (failed)"() {
        given:
        MavenParentEquals mavenParentEquals = MavenParentEquals.coordinates(coordinates)
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 1
        enforcerResult.messages[0] == message

        where:
        scenario        | coordinates                     | message
        "groupId"       | "org.acme:hello-world"          | "Maven <parent> groupId [com.example] does not equal [org.acme]"
        "artifactId"    | "com.example:foo-bar"           | "Maven <parent> artifactId [hello-world] does not equal [foo-bar]"
        "version"       | "com.example:hello-world:3.0.0" | "Maven <parent> [com.example:hello-world:pom:1.0.0] version [1.0.0] does not equal or match [3.0.0]"
        "version regex" | "com.example:hello-world:3.*"   | "Maven <parent> [com.example:hello-world:pom:1.0.0] version [1.0.0] does not equal or match [3.*]"
    }

    def "enforce - all incorrect - #scenario (failed)"() {
        given:
        MavenParentEquals mavenParentEquals = MavenParentEquals.coordinates("org.acme:foo-bar:3.0.0")
        InputStream fileInputStream = IoUtil.getResourceAsStream("/pom.xml")

        when:
        EnforcerResult enforcerResult = mavenParentEquals.enforce(fileInputStream)

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 3
        enforcerResult.messages[0] == "Maven <parent> groupId [com.example] does not equal [org.acme]"
        enforcerResult.messages[1] == "Maven <parent> artifactId [hello-world] does not equal [foo-bar]"
        enforcerResult.messages[2] == "Maven <parent> [com.example:hello-world:pom:1.0.0] version [1.0.0] does not equal or match [3.0.0]"
    }

}
