# GraalVM Java 8 Base Container for Building
FROM oracle/graalvm-ce:20.1.0-java8

# Build Arguments
ARG NAME
ARG JAR
ARG WORKDIR

# Install native-image tool
RUN gu install --no-progress native-image

# Copy shaded native jar into build
COPY ${JAR} ${WORKDIR}/native-image.jar

# Define the working directory
WORKDIR ${WORKDIR}

# Build the native image
RUN native-image -cp native-image.jar \
    -H:+ReportExceptionStackTraces \
    --report-unsupported-elements-at-runtime \
    --no-fallback \
    --static