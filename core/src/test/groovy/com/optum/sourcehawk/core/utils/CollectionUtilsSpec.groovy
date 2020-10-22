package com.optum.sourcehawk.core.utils

import spock.lang.Specification

class CollectionUtilsSpec extends Specification {

    def "isEmpty - true"() {
        expect:
        CollectionUtils.isEmpty(null)
        CollectionUtils.isEmpty([])
    }

    def "isEmpty - false"() {
        expect:
        !CollectionUtils.isEmpty(["abc"])
        !CollectionUtils.isEmpty([1])
    }

    def "isNotEmpty - false"() {
        expect:
        !CollectionUtils.isNotEmpty(null)
        !CollectionUtils.isNotEmpty([])
    }

    def "isNotEmpty - true"() {
        expect:
        CollectionUtils.isNotEmpty(["abc"])
        CollectionUtils.isNotEmpty([1])
    }

}
