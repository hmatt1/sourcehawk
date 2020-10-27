package com.optum.sourcehawk.exec

import org.spockframework.util.IoUtil
import spock.lang.Shared
import spock.lang.Unroll

class FixCommandSpec extends CliBaseSpecification {

    @Shared
    String updateRoot = new File(IoUtil.getResource("/marker").toURI())
            .getParentFile()
            .getAbsolutePath() + "/repo"

    @Unroll
    def "main: #helpArg"() {
        given:
        String[] args = new String[] { helpArg }

        when:
        FixCommand.main(args)

        then:
        SystemExit systemExit = thrown(SystemExit)
        systemExit.status == 0

        where:
        helpArg << ["-h", "--help" ]
    }

    def "main: dry run"() {
        given:
        String[] args = [ "--dry-run", repositoryRoot.toString() ]

        when:
        FixCommand.main(args)

        then:
        SystemExit systemExit = thrown(SystemExit)
        systemExit.status == 0
        // TODO: no files updated
    }

    def "main: configuration file not found (failed)"() {
        given:
        String[] args = ["-c", "sourcehawk.yml"]

        when:
        FixCommand.main(args)

        then:
        SystemExit systemExit = thrown(SystemExit)
        systemExit.status == 1
    }

    @Unroll
    def "main: parse error"() {
        given:
        String[] args = new String[] { arg }

        when:
        FixCommand.main(args)

        then:
        SystemExit systemExit = thrown(SystemExit)
        systemExit.status == 2

        where:
        arg << [ "-n", "--none" ]
    }

    // TODO: temporary testing directory

}
