Sourcehawk
----------

![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/optum/sourcehawk) 
[![Maven Central](https://img.shields.io/maven-central/v/com.optum.sourcehawk/sourcehawk-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.optum.sourcehawk%22%20AND%20a:%22sourcehawk-core%22) 
![Docker Image Version](https://img.shields.io/docker/v/optumopensource/sourcehawk) 

[![Build Status](https://github.com/optum/sourcehawk/workflows/Maven%20CI/badge.svg)](https://github.com/optum/sourcehawk/actions) 
[![Sourcehawk Scan](https://github.com/optum/sourcehawk/workflows/Sourcehawk%20Scan/badge.svg)](https://github.com/optum/sourcehawk/actions) 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.optum.sourcehawk%3Asourcehawk&metric=coverage)](https://sonarcloud.io/dashboard?id=com.optum.sourcehawk%3Asourcehawk)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.optum.sourcehawk%3Asourcehawk&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.optum.sourcehawk%3Asourcehawk)

![OSS Lifecycle](https://img.shields.io/osslifecycle/optum/sourcehawk) 
[![Sonatype OSS Index](https://img.shields.io/badge/Sonatype%20OSS%20Index-sourcehawk--exec-informational)](https://ossindex.sonatype.org/component/pkg:maven/com.optum.sourcehawk/sourcehawk-exec)

`Sourcehawk` is an extensible compliance as code tool which allows development teams to run compliance scans on their source code.  

## Documentation

### CLI Usage
https://optum.github.io/sourcehawk

#### Installation

##### Linux
```sh
curl https://raw.githubusercontent.com/Optum/sourcehawk/main/install-linux.sh | bash
```

##### Debian
You'll need to be able to `sudo` for this one.

```sh
curl https://raw.githubusercontent.com/Optum/sourcehawk/main/install-debian.sh | bash
```

##### Mac
```sh
curl https://raw.githubusercontent.com/Optum/sourcehawk/main/install-mac.sh | bash
```

#### Manuals

* [Sourcehawk](https://optum.github.io/sourcehawk/#_sourcehawk1) - parent command
* [Scan Manual](https://optum.github.io/sourcehawk/#_scan1) - `scan` command
* [Validate Config Manual](https://optum.github.io/sourcehawk/#_validate_config1) - `validate-config` command
* [Fix Manual](https://optum.github.io/sourcehawk/#_fix1) - `fix` command

## Contributing
If you wish to contribute to the development of Sourcehawk please read [CONTRIBUTING.md](CONTRIBUTING.md) for more information.
