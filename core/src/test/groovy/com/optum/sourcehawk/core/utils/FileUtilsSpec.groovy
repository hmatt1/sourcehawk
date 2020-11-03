package com.optum.sourcehawk.core.utils

import org.spockframework.util.IoUtil
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import sun.nio.fs.UnixFileAttributes
import sun.nio.fs.UnixPath

import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Stream

class FileUtilsSpec extends Specification {

    @Shared
    protected Path testResourcesRoot = Paths.get(IoUtil.getResource("/marker" ).toURI())
            .getParent()

    def "private constructor"() {
        expect:
        new FileUtils()
    }

    def "deriveRelativePath"() {
        given:
        String root = "/home/user/code"
        String absolutePath = "/home/user/code/path/to/dir"

        when:
        String relativePath = FileUtils.deriveRelativePath(root, absolutePath)

        then:
        relativePath == "path/to/dir"
    }

    def "deriveRelativePath - root ends with /"() {
        given:
        String root = "/home/user/code/"
        String absolutePath = "/home/user/code/path/to/dir"

        when:
        String relativePath = FileUtils.deriveRelativePath(root, absolutePath)

        then:
        relativePath == "path/to/dir"
    }

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
        FileUtils.isGlobPattern("[ab].md")
    }

    def "isGlobPattern - false"() {
        expect:
        !FileUtils.isGlobPattern("/dir/Dockerfile")
        !FileUtils.isGlobPattern("file.md")
        !FileUtils.isGlobPattern("dir/file.txt")
    }

    def "PathMatcherFileVisitor - file"() {
        given:
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(String.format("glob:/tmp/**"))
        Stream.Builder<Paths> streamBuilder = Stream.builder()
        FileUtils.PathMatcherFileVisitor fileVisitor = new FileUtils.PathMatcherFileVisitor(pathMatcher, streamBuilder)
        Path file = Paths.get("/tmp/file.txt")
        BasicFileAttributes mockBasicFileAttributes = Mock()

        when:
        FileVisitResult fileVisitResult = fileVisitor.visitFile(file, mockBasicFileAttributes)

        then:
        1 * mockBasicFileAttributes.isDirectory() >> false
        0 * _

        and:
        fileVisitResult == FileVisitResult.CONTINUE
    }

    def "PathMatcherFileVisitor - directory"() {
        given:
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(String.format("glob:/tmp/**"))
        Stream.Builder<Paths> streamBuilder = Stream.builder()
        FileUtils.PathMatcherFileVisitor fileVisitor = new FileUtils.PathMatcherFileVisitor(pathMatcher, streamBuilder)
        Path file = Paths.get("/tmp/dir/")
        BasicFileAttributes mockBasicFileAttributes = Mock()

        when:
        FileVisitResult fileVisitResult = fileVisitor.visitFile(file, mockBasicFileAttributes)

        then:
        1 * mockBasicFileAttributes.isDirectory() >> true
        0 * _

        and:
        fileVisitResult == FileVisitResult.CONTINUE
    }

}
