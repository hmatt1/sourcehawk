package com.optum.sourcehawk.core.utils


import spock.lang.Specification

class MapUtilsSpec extends Specification {

    def "mergeCollectionValues - combined"() {
        given:
        Map<String, Collection<String>> oneOne = Collections.singletonMap("one", Collections.singletonList("one.one"))
        Map<String, Collection<String>> oneTwo = Collections.singletonMap("one", Collections.singletonList("one.two"))

        when:
        Map<String, Collection<String>> merged = MapUtils.mergeCollectionValues(oneOne, oneTwo)

        then:
        merged
        merged.size() == 1
        merged.get("one")
        merged.get("one").size() == 2
        merged.get("one")[0] == "one.one"
        merged.get("one")[1] == "one.two"
    }

    def "mergeCollectionValues - not combined"() {
        given:
        Map<String, Collection<String>> oneOne = Collections.singletonMap("one", Collections.singletonList("one.one"))
        Map<String, Collection<String>> twoTwo = Collections.singletonMap("two", Collections.singletonList("two.two"))

        when:
        Map<String, Collection<String>> merged = MapUtils.mergeCollectionValues(oneOne, twoTwo)

        then:
        merged
        merged.size() == 2
        merged.get("one")
        merged.get("one").size() == 1
        merged.get("one")[0] == "one.one"
        merged.get("two")
        merged.get("two").size() == 1
        merged.get("two")[0] == "two.two"
    }

}
