package com.optum.sourcehawk.enforcer

import spock.lang.Specification


class EnforcerResultSpec extends Specification {

    def "creators"() {
        expect:
        EnforcerResult.create([]).passed
        !EnforcerResult.create(["message"]).passed
        EnforcerResult.passed().passed
        EnforcerResult.passed().messages.isEmpty()
        !EnforcerResult.failed("failed").passed
        EnforcerResult.failed("failed").messages as List == ["failed"]
    }

    def "builder"() {
        when:
        EnforcerResult enforcerResult = EnforcerResult.builder()
                .messages(["message1", "message2"])
                .passed(false)
                .build()

        then:
        enforcerResult
        !enforcerResult.passed
        enforcerResult.messages
        enforcerResult.messages.size() == 2
        enforcerResult.messages[0] == "message1"
        enforcerResult.messages[1] == "message2"
    }

    def "reduce"() {
        given:
        EnforcerResult e1 = EnforcerResult.passed()
        EnforcerResult e2 = EnforcerResult.failed("failed")

        when:
        EnforcerResult e3 = EnforcerResult.reduce(e1, e2)

        then:
        e3.messages as List == ["failed"]
        !e3.passed
    }

}
