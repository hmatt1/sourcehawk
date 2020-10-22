package com.optum.sourcehawk.exec.graal.nativeimage

import com.optum.sourcehawk.exec.Sourcehawk
import spock.lang.Specification

/**
 * The purpose of this spec is assert that we have proper configurations in the native-image.properties file
 */
class NativeImageConfigSpec extends Specification {

    String nativeImageConfigResourcePath = "/META-INF/native-image/sourcehawk/native-image.properties"

    def "config"() {
        when:
        Properties nativeImageProperties = new Properties()
        nativeImageProperties.load(getClass().getResourceAsStream(nativeImageConfigResourcePath))

        then:
        nativeImageProperties.containsKey("Args")

        when:
        String args = nativeImageProperties.getProperty("Args")

        then:
        args.contains("-H:Class=${Sourcehawk.class.name}")
        args.contains("--enable-url-protocols=http,https")
        args.contains("--initialize-at-build-time=ch.qos.logback,com.fasterxml.jackson,org.slf4j,org.yaml.snakeyaml,javax.xml")
    }

}
