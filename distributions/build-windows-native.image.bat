Rem Script Requirements
Rem - Operating System: Windows
Rem - GraalVM (Java 8)
Rem - GraalVM native-image tool

set name=%1

Rem Build the native image
native-image -cp native-image.jar ^
    -H:+ReportExceptionStackTraces ^
    -H:Name=%name% ^
    --report-unsupported-elements-at-runtime ^
    --no-server ^
    --no-fallback