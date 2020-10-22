#!/bin/sh

set -e

# Script Arguments
DOCKER_IMAGE=${1}
OPTIONS=${2}

# Run the image (exits automatically)
docker run "$DOCKER_IMAGE" "$OPTIONS"
