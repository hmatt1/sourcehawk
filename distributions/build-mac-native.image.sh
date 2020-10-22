#!/bin/sh

####################################################################
# Script Requirements
#  - Operating System: Mac
#  - GraalVM (Java 8)
#  - GraalVM native-image tool
####################################################################

NAME=${1}

# Build the native image
native-image -cp native-image.jar \
  -H:+ReportExceptionStackTraces \
  -H:Name="$NAME" \
  --report-unsupported-elements-at-runtime \
  --no-server \
  --no-fallback