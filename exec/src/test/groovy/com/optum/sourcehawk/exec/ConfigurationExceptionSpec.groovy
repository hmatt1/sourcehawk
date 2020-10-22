package com.optum.sourcehawk.exec

import spock.lang.Specification

class ConfigurationExceptionSpec extends Specification {

    def 'can create exception'(){
        expect:
        new ConfigurationException()
        new ConfigurationException("test").message == "test"
    }
}
