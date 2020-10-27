#!/usr/bin/env bash

# Retrieve Latest Version
GROUP_ID="com.optum.sourcehawk"
ARTIFACT_ID="sourcehawk-dist-linux"
VERSION=$(curl -s "http://search.maven.org/solrsearch/select?q=g:\"$GROUP_ID\"+AND+a:\"$ARTIFACT_ID\"" | grep -Po '"latestVersion":.*?[^\\]",' | cut -d '"' -f 4)

# Download the binary and make it executable
ARCH="$(uname -m)"
DOWNLOAD_URL="https://search.maven.org/remotecontent?filepath=${GROUP_ID//.//}/$ARTIFACT_ID/$VERSION/$ARTIFACT_ID-$VERSION-$ARCH.zip"
INSTALL_PATH="/usr/bin/sourcehawk"
if curl -s "$DOWNLOAD_URL" -o "$INSTALL_PATH"; then
  chmod +x "$INSTALL_PATH"
  sourcehawk -V
else
  echo "Sourcehawk is not yet available on your architecture: $ARCH"
  exit 1
fi
