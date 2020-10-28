# Sourcehawk Alpine Docker Image

### Scanning from local directory

```shell script
docker run -v "$(pwd):/home/sourcehawk" optumopensource/sourcehawk:1.0.0-alpine
```

Or with a custom working directory:

```shell script
docker run -v "$(pwd):/tmp" -w /tmp optumopensource/sourcehawk:1.0.0-alpine
```

The volume mounting is necessary in order to give the container access to the files to scan.