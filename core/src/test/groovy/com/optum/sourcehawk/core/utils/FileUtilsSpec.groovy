package com.optum.sourcehawk.core.utils

import org.spockframework.util.IoUtil
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.Paths

class FileUtilsSpec extends Specification {

    @Shared
    protected Path testResourcesRoot = Paths.get(IoUtil.getResource("/marker" ).toURI())
            .getParent()

    def "find - glob pattern (found - results)"() {
        when:
        Collection<Path> paths = FileUtils.find(testResourcesRoot.toAbsolutePath().toString(), "**/glob/*.md").collect()

        then:
        paths
        paths.size() == 2
    }

    def "find - glob pattern with question mark (found - results)"() {
        when:
        Collection<Path> paths = FileUtils.find(testResourcesRoot.toAbsolutePath().toString(), "**/glob/file?.md").collect()

        then:
        paths
        paths.size() == 1
    }

    def "find - glob pattern no file extension (found - results)"() {
        when:
        Collection<Path> paths = FileUtils.find(testResourcesRoot.toAbsolutePath().toString(), "**/Dockerfile").collect()

        then:
        paths
        paths.size() == 2
    }

    def "find - glob pattern (not found - no results)"() {
        expect:
        !FileUtils.find(testResourcesRoot.toAbsolutePath().toString(), "**/glob/*.txt").collect()
    }

    def "find - glob pattern (directory matched - no results)"() {
        expect:
        !FileUtils.find(testResourcesRoot.toAbsolutePath().toString(), "**/glob/directory").collect()
    }

    @Unroll
    def "find - no pattern - #path (found - results)"() {
        when:
        Collection<Path> paths = FileUtils.find(testResourcesRoot.toAbsolutePath().toString(), path).collect()

        then:
        paths
        paths.size() == 1

        where:
        path << [ "glob/file.md", "glob/file2.md"]
    }

    @Unroll
    def "find - no pattern - #path (not found - no results)"() {
        expect:
        !FileUtils.find(testResourcesRoot.toAbsolutePath().toString(), path).collect()

        where:
        path << [ "/glob/file.md", "glob/file3.md"]
    }

    def "find - null arguments (skipped - no results)"() {
        expect:
        !FileUtils.find(null, "glob/file.md").collect()
        !FileUtils.find(testResourcesRoot.toAbsolutePath().toString(), null).collect()
    }

    def "isGlobPattern - true"() {
        expect:
        FileUtils.isGlobPattern("**/*.md")
        FileUtils.isGlobPattern("file?.md")
        FileUtils.isGlobPattern("*.md")
    }

    def "isGlobPattern - false"() {
        expect:
        !FileUtils.isGlobPattern("/dir/Dockerfile")
        !FileUtils.isGlobPattern("file.md")
        !FileUtils.isGlobPattern("dir/file.txt")
    }

}
