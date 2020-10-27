package com.optum.sourcehawk.exec.graal.nativeimage

import com.optum.sourcehawk.exec.FileBaseSpecification
import com.optum.sourcehawk.exec.Sourcehawk

import java.nio.charset.StandardCharsets

/**
 * The purpose of this spec is to generate a resources configuration file which whitelists all known
 * resources which are read at runtime, which is required for building a native image
 *
 * https://github.com/oracle/graal/blob/master/substratevm/RESOURCES.md
 */
class ResourceConfigGeneratorSpec extends FileBaseSpecification {

    String nativeImageConfigFilePathPrefix = "target/classes/META-INF/native-image"

    def "generate"() {
        given:
        String generatedResourcesConfigFilePath = "${nativeImageConfigFilePathPrefix}/sourcehawk-generated/resource-config.json"
        File generatedResourcesConfigFile = new File(generatedResourcesConfigFilePath)
        createParentDirectories(generatedResourcesConfigFile)
        String generatedResourcesConfigTemplate = ResourceConfigGeneratorSpec
                .getClassLoader()
                .getResourceAsStream("resource-config-template.json")
                .getText(StandardCharsets.UTF_8.name())
        Set<String> resources = new HashSet<>()
        resources.add(Sourcehawk.VersionProvider.PROPERTIES_LOCATION)
        resources.add("logback.xml")

        when:
        FileOutputStream generatedFileOutputStream = new FileOutputStream(generatedResourcesConfigFile, false)
        generatedFileOutputStream.write("{\n".bytes)
        generatedFileOutputStream.write("  \"resources\": [\n".bytes)
        Iterator<String> resourcesIterator = resources.iterator()
        while (resourcesIterator.hasNext()) {
            String resource = resourcesIterator.next()
            generatedFileOutputStream.write(generatedResourcesConfigTemplate.replace("_PATTERN_", resource).bytes)
            if (resourcesIterator.hasNext()) {
                generatedFileOutputStream.write(",\n".bytes)
            }
        }
        generatedFileOutputStream.write("\n  ]\n".bytes)
        generatedFileOutputStream.write("}\n".bytes)
        generatedFileOutputStream.close()

        then:
        generatedResourcesConfigFile.exists()
    }

}
