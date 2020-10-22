package com.optum.sourcehawk.core.repository

import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class LocalRepositoryFileReaderSpec extends Specification {

    def "create"() {
        expect:
        LocalRepositoryFileReader.create(Paths.get("/"))
    }

    def "create - NPE"() {
        when:
        LocalRepositoryFileReader.create(null)

        then:
        thrown(NullPointerException)
    }

    def "read - file found"() {
        given:
        URL resource = getClass().getClassLoader().getResource('file.txt')
        File fileResource = new File(resource.toURI())
        Path repositoryRoot = Paths.get(fileResource.getParentFile().getAbsolutePath())
        RepositoryFileReader repositoryFileReader = LocalRepositoryFileReader.create(repositoryRoot)

        when:
        Optional<InputStream> fileInputStream = repositoryFileReader.read('file.txt')

        then:
        fileInputStream
        fileInputStream.isPresent()
        fileInputStream.get()
    }

    def "read - file not found"() {
        given:
        URL resource = getClass().getClassLoader().getResource('file.txt')
        File fileResource = new File(resource.toURI())
        Path repositoryRoot = Paths.get(fileResource.getParentFile().getAbsolutePath())
        RepositoryFileReader repositoryFileReader = LocalRepositoryFileReader.create(repositoryRoot)

        when:
        Optional<InputStream> fileInputStream = repositoryFileReader.read('nope.txt')

        then:
        fileInputStream == Optional.empty()
    }

    def "read - path null - NPE"() {
        given:
        RepositoryFileReader repositoryFileReader = LocalRepositoryFileReader.create(Paths.get("/"))

        when:
        repositoryFileReader.read(null)

        then:
        thrown(NullPointerException)
    }

}
