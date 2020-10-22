package com.optum.sourcehawk.exec.graal.nativeimage


import com.google.common.io.Files
import com.optum.sourcehawk.configuration.SourcehawkConfiguration
import com.optum.sourcehawk.core.scan.ScanResult
import com.optum.sourcehawk.enforcer.file.FileEnforcer
import com.optum.sourcehawk.protocol.FileProtocol
import org.reflections.Reflections
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * The purpose of this spec is to generate a reflection configuration file which whitelists
 * all known uses of reflection, which is required for building a native image
 *
 * https://github.com/oracle/graal/blob/master/substratevm/REFLECTION.md
 */
class ReflectConfigGeneratorSpec extends Specification {

    String nativeImageConfigFilePathPrefix = "target/classes/META-INF/native-image"

    def "generate"() {
        given:
        String generatedReflectionConfigFilePath = "${nativeImageConfigFilePathPrefix}/sourcehawk-generated/reflect-config.json"
        File generatedReflectionConfigFile = new File(generatedReflectionConfigFilePath)
        Files.createParentDirs(generatedReflectionConfigFile)
        String generatedReflectionConfigTemplate = ReflectConfigGeneratorSpec
                .getClassLoader()
                .getResourceAsStream("reflect-config-template.json")
                .getText(StandardCharsets.UTF_8.name())
        Reflections reflections = new Reflections("com.optum.sourcehawk.enforcer")
        Set<Class<?>> enforcerClasses = reflections.getSubTypesOf(FileEnforcer)

        when:
        // Sourcehawk (required to support Jackson Deserialization)
        Set<Class<?>> reflectionClasses = new HashSet<>(enforcerClasses)
        reflectionClasses.add(SourcehawkConfiguration)
        reflectionClasses.add(FileProtocol)
        reflectionClasses.add(FileProtocol.FileProtocolBuilder)
        reflectionClasses.add(ScanResult)
        reflectionClasses.add(ScanResult.MessageDescriptor)

        then:
        reflectionClasses
        reflectionClasses.size() == 25

        when:
        FileOutputStream generatedFileOutputStream = new FileOutputStream(generatedReflectionConfigFile, false)
        generatedFileOutputStream.write("[\n".bytes)
        Iterator<Class<?>> reflectionClassesIterator = reflectionClasses.iterator()
        while (reflectionClassesIterator.hasNext()) {
            Class<?> enforcerClass = reflectionClassesIterator.next()
            generatedFileOutputStream.write(generatedReflectionConfigTemplate.replace("_CLASS_", enforcerClass.getName()).bytes)
            if (reflectionClassesIterator.hasNext()) {
                generatedFileOutputStream.write(",\n".bytes)
            }
        }
        generatedFileOutputStream.write("\n]".bytes)
        generatedFileOutputStream.close()

        then:
        generatedReflectionConfigFile.exists()
    }

}
