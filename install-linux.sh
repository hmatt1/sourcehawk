#!/usr/bin/env bash

# Retrieve Latest Version
GROUP_ID="com.optum.sourcehawk"
ARTIFACT_ID="sourcehawk-dist-linux"
VERSION=$(curl -s -H "Accept: application/json" "http://search.maven.org/solrsearch/select?q=g:$GROUP_ID%20AND%20a:$ARTIFACT_ID" | grep -Po '"latestVersion":.*?[^\\]",' | cut -d '"' -f 4)

# Download the binary and make it executable
ARCH="$(uname -m)"
DOWNLOAD_URL="https://repo1.maven.org/maven2/${GROUP_ID//.//}/$ARTIFACT_ID/$VERSION/$ARTIFACT_ID-$VERSION-$ARCH.zip"
INSTALL_PATH="/usr/local/bin/sourcehawk"

echo "Downloading Sourcehawk binary..."
if sudo curl -sLk "$DOWNLOAD_URL" -o "$INSTALL_PATH"; then
  echo "Installing..."
  sudo chmod +x "$INSTALL_PATH"
  sourcehawk -V
  sourcehawk --help
else
  echo "Sourcehawk is not yet available on your architecture: $ARCH"
  exit 1
fi
