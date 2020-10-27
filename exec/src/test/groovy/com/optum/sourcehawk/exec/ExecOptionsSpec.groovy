package com.optum.sourcehawk.exec


import com.optum.sourcehawk.core.scan.Verbosity
import spock.lang.Specification

import java.nio.file.Paths

class ExecOptionsSpec extends Specification {

    def "builder - defaults"() {
        given:
        ExecOptions.ExecOptionsBuilder builder = ExecOptions.builder()

        when:
        ExecOptions execOptions = builder.build()

        then:
        execOptions
        execOptions.repositoryRoot == Paths.get(".")
        execOptions.configurationFileLocation == "sourcehawk.yml"
        execOptions.verbosity == Verbosity.HIGH

        and:
        execOptions == ExecOptions.builder().build()
        execOptions.hashCode() == ExecOptions.builder().build().hashCode()

        and:
        execOptions.toString()
    }

    def "builder - custom"() {
        given:
        ExecOptions.ExecOptionsBuilder builder = ExecOptions.builder()
                .repositoryRoot(Paths.get("/"))
                .configurationFileLocation("Sourcehawk")
                .verbosity(Verbosity.ZERO)

        when:
        ExecOptions execOptions = builder.build()

        then:
        execOptions
        execOptions.repositoryRoot == Paths.get("/")
        execOptions.configurationFileLocation == "Sourcehawk"
        execOptions.verbosity == Verbosity.ZERO
    }

    def "builder - NPE"() {
        when:
        ExecOptions.builder()
                .repositoryRoot(null)

        then:
        thrown(NullPointerException)

        when:
        ExecOptions.builder()
                .verbosity(null)

        then:
        thrown(NullPointerException)

        when:
        ExecOptions.builder()
                .configurationFileLocation(null)

        then:
        thrown(NullPointerException)

        when:
        ExecOptions.builder()
                .outputFormat(null)

        then:
        thrown(NullPointerException)
    }

}
