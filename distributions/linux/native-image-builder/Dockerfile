# GraalVM Java 11 Base Container for Building
FROM oracle/graalvm-ce:20.2.0-java11

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
    -H:+TraceClassInitialization \
    --report-unsupported-elements-at-runtime \
    --initialize-at-build-time=com.sun.org.apache.xerces.internal.util,com.sun.org.apache.xerces.internal.impl,jdk.xml.internal,com.sun.xml.internal.stream.util,com.sun.org.apache.xerces.internal.xni,com.sun.org.apache.xerces.internal.utils \
    --initialize-at-build-time=org.slf4j,org.yaml.snakeyaml,javax \
    --no-fallback \
    --static