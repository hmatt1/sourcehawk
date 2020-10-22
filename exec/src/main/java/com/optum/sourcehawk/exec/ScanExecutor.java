package com.optum.sourcehawk.exec;

import com.optum.sourcehawk.configuration.SourcehawkConfiguration;
import com.optum.sourcehawk.core.repository.LocalRepositoryFileReader;
import com.optum.sourcehawk.core.repository.RepositoryFileReader;
import com.optum.sourcehawk.core.scan.ScanResult;
import com.optum.sourcehawk.enforcer.file.FileEnforcer;
import com.optum.sourcehawk.protocol.FileProtocol;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Entry point into executing scans
 *
 * @author Brian Wyka
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScanExecutor {

    /**
     * Run the scan based on the provided options
     *
     * @param execOptions the scan options
     * @return the scan result
     */
    public static ScanResult scan(final ExecOptions execOptions) {
        return ConfigurationReader.readConfiguration(execOptions.getRepositoryRoot(), execOptions.getConfigurationFileLocation())
                .map(sourcehawkConfiguration -> executeScan(execOptions, sourcehawkConfiguration))
                .orElseGet(() -> ScanResultFactory.error(execOptions.getConfigurationFileLocation().toString(), "Configuration file not found"));
    }

    /**
     * Execute the scan.  Iterate over all file protocols, and each enforcer within the file protocol and aggregate the results
     *
     * @param execOptions             the scan options
     * @param sourcehawkConfiguration the configuration
     * @return the aggregated scan result
     */
    private static ScanResult executeScan(final ExecOptions execOptions, final SourcehawkConfiguration sourcehawkConfiguration) {
        val repositoryFileReader = LocalRepositoryFileReader.create(execOptions.getRepositoryRoot());
        val filteredFileProtocols = sourcehawkConfiguration.getFileProtocols().stream()
                .filter(FileProtocol::isRequired)
                .collect(Collectors.toSet());
        val fileProtocolScanResults = new ArrayList<ScanResult>(filteredFileProtocols.size());
        for (val fileProtocol : filteredFileProtocols) {
            if (fileProtocol.getEnforcers() == null || fileProtocol.getEnforcers().isEmpty()) {
                fileProtocolScanResults.add(enforceFileExists(repositoryFileReader, fileProtocol));
                continue;
            }
            try {
                fileProtocolScanResults.add(enforceFileProtocol(repositoryFileReader, fileProtocol));
            } catch (final IOException e) {
                val message = String.format("Error enforcing file protocol: %s", e.getMessage());
                fileProtocolScanResults.add(ScanResultFactory.error(fileProtocol.getRepositoryPath(), message));
            }
        }
        return fileProtocolScanResults.stream()
                .reduce(ScanResult.passed(), ScanResult::reduce);
    }

    /**
     * Handle scenarios where there are no enforcers for a file protocol
     *
     * @param repositoryFileReader the repository file reader
     * @param fileProtocol         the file protocol
     * @return the scan result
     */
    private static ScanResult enforceFileExists(final RepositoryFileReader repositoryFileReader, final FileProtocol fileProtocol) {
        try {
            return repositoryFileReader.read(fileProtocol.getRepositoryPath())
                    .map(fis -> ScanResult.passed())
                    .orElseGet(() -> ScanResultFactory.fileNotFound(fileProtocol));
        } catch (final IOException e) {
            val message = String.format("Unable to obtain file input stream: %s", e.getMessage());
            return ScanResultFactory.error(fileProtocol.getRepositoryPath(), message);
        }
    }

    /**
     * Enforce the file protocol
     *
     * @param repositoryFileReader the repository file reader
     * @param fileProtocol         the file protocol
     * @return the scan result
     * @throws IOException if any error occurs during file processing
     */
    private static ScanResult enforceFileProtocol(final RepositoryFileReader repositoryFileReader, final FileProtocol fileProtocol) throws IOException {
        val fileProtocolScanResults = new ArrayList<ScanResult>(fileProtocol.getEnforcers().size());
        for (val enforcer : fileProtocol.getEnforcers()) {
            final FileEnforcer fileEnforcer;
            try {
                fileEnforcer = ConfigurationReader.CONFIGURATION_DESERIALIZER.convertValue(enforcer, FileEnforcer.class);
            } catch (final IllegalArgumentException e) {
                fileProtocolScanResults.add(ScanResultFactory.error(fileProtocol.getRepositoryPath(), String.format("File enforcer invalid: %s", e.getMessage())));
                continue;
            }
            val fileInputStreamOptional = repositoryFileReader.read(fileProtocol.getRepositoryPath());
            if (fileInputStreamOptional.isPresent()) {
                try (val fileInputStream = fileInputStreamOptional.get()) {
                    val enforcerResult = fileEnforcer.enforce(fileInputStream);
                    fileProtocolScanResults.add(ScanResultFactory.enforcerResult(fileProtocol, enforcerResult));
                }
            } else {
                fileProtocolScanResults.add(ScanResultFactory.fileNotFound(fileProtocol));
            }
        }
        return fileProtocolScanResults.stream()
                .reduce(ScanResult.passed(), ScanResult::reduce);
    }

}
