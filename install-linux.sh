#!/usr/bin/env bash

# Retrieve Latest Version
GROUP_ID="com.optum.sourcehawk"
ARTIFACT_ID="sourcehawk-dist-linux"
VERSION=$(curl -s -H "Accept: application/json" "http://search.maven.org/solrsearch/select?q=g:$GROUP_ID%20AND%20a:$ARTIFACT_ID" | grep -Po '"latestVersion":.*?[^\\]",' | cut -d '"' -f 4)

# Download the binary and make it executable
ARCH="$(uname -m)"
DOWNLOAD_URL="https://repo1.maven.org/maven2/${GROUP_ID//.//}/$ARTIFACT_ID/$VERSION/$ARTIFACT_ID-$VERSION-$ARCH.zip"
INSTALL_LOCATION="/usr/local/bin"
INSTALL_PATH="$INSTALL_LOCATION/sourcehawk"

if [ ! -w "$INSTALL_LOCATION" ]; then
  echo "You do not have write permissions to $INSTALL_LOCATION.  Try using 'sudo' to perform install"
  exit 1
fi

echo "Downloading Sourcehawk binary..."
if curl -sLk "$DOWNLOAD_URL" -o "$INSTALL_PATH"; then
  echo "Installing..."
  chmod +x "$INSTALL_PATH"
  sourcehawk -V
  sourcehawk --help
else
  echo "Sourcehawk is not yet available on your architecture: $ARCH"
  exit 1
fi
