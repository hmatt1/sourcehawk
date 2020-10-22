package com.optum.sourcehawk.configuration


import com.optum.sourcehawk.protocol.FileProtocol
import spock.lang.Specification

class SourcehawkConfigurationSpec extends Specification {

    def "of"() {
        given:
        String apiVersion = "0.1"
        Set<String> configLocations = []
        Collection<FileProtocol> fileProtocols = Collections.emptySet()

        when:
        SourcehawkConfiguration sourceHawkConfiguration = SourcehawkConfiguration.of(apiVersion, configLocations, fileProtocols)

        then:
        sourceHawkConfiguration
        sourceHawkConfiguration.apiVersion == apiVersion
        sourceHawkConfiguration.fileProtocols == fileProtocols
        sourceHawkConfiguration.configLocations == configLocations
    }

    def "of - null apiVersion"() {
        given:
        Collection<FileProtocol> fileProtocols = Collections.emptySet()

        when:
        SourcehawkConfiguration sourceHawkConfiguration = SourcehawkConfiguration.of(null, null, fileProtocols)

        then:
        sourceHawkConfiguration
        !sourceHawkConfiguration.apiVersion
        !sourceHawkConfiguration.configLocations
        sourceHawkConfiguration.fileProtocols == fileProtocols
    }

    def "of - null null null"() {
        when:
        SourcehawkConfiguration sourceHawkConfiguration = SourcehawkConfiguration.of(null, null, null)

        then:
        sourceHawkConfiguration
        !sourceHawkConfiguration.apiVersion
        !sourceHawkConfiguration.configLocations
        !sourceHawkConfiguration.fileProtocols
    }

}
