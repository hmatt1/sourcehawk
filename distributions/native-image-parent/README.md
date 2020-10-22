Native Image Builds
-------------------

Native image builds are performed inside a docker container.  If you would like 
to run them outside a container during development, please follow the 
installation instructions below.

## GraalVM and Native Image Tool Installation

1. Install GraalVM - https://www.graalvm.org/getting-started/
2. Install `native-image` - https://www.graalvm.org/docs/reference-manual/native-image/
3. There may be some other dependencies required
    + https://www.graalvm.org/docs/reference-manual/languages/llvm/#llvm-toolchain