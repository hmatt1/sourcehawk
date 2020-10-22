package com.optum.sourcehawk.core.scan

import spock.lang.Specification
import spock.lang.Unroll

class SeveritySpec extends Specification {

    @Unroll
    def "parse - valid"() {
        expect:
        Severity.parse(name)

        where:
        name << ['recommendation', 'RECOMMENDATION', 'warning', 'WARNING', 'error', 'ERROR']
    }

    @Unroll
    def "parse - invalid - returns default"() {
        expect:
        Severity.parse(name) == Severity.ERROR

        where:
        name << ['', ' ', null, 'info', 'WR', 'err', 'no', 'warn']
    }

}
