package com.optum.sourcehawk.enforcer

import spock.lang.Specification


class ResolverResultSpec extends Specification {

    def "creators"() {
        expect:
        !ResolverResult.NO_UPDATES.updatesApplied
        ResolverResult.updatesApplied("Yep, been updated alright").updatesApplied
        ResolverResult.builder().updatesApplied(true).messages(["Done!"]).build()
    }

    def "builder"() {
        given:
        ResolverResult resolverResult = ResolverResult.builder()
                .updatesApplied(true)
                .messages(["Done!"])
                .build()

        expect:
        resolverResult
        resolverResult.updatesApplied
        resolverResult.messages
        resolverResult.messages.size() == 1
        resolverResult.messages[0] == "Done!"
    }

}
