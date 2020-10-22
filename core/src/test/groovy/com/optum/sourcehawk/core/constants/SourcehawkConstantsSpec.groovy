package com.optum.sourcehawk.core.constants

import spock.lang.Specification

class SourcehawkConstantsSpec extends Specification {

    def "private constructor"() {
        expect:
        new SourcehawkConstants()
    }

}
