#!/usr/bin/env bash

# Retrieve Latest Version
GROUP_ID="com.optum.sourcehawk"
ARTIFACT_ID="sourcehawk-dist-debian"
VERSION=$(curl -s -H "Accept: application/json" "http://search.maven.org/solrsearch/select?q=g:$GROUP_ID%20AND%20a:$ARTIFACT_ID" | grep -Po '"latestVersion":.*?[^\\]",' | cut -d '"' -f 4)

# Download the binary and make it executable
DOWNLOAD_URL="https://repo1.maven.org/maven2/${GROUP_ID//.//}/$ARTIFACT_ID/$VERSION/$ARTIFACT_ID-$VERSION.deb"
DEB_PACKAGE="/tmp/sourcehawk-$VERSION.deb"

echo "Downloading Sourcehawk package..."
if curl -sLk "$DOWNLOAD_URL" -o "$DEB_PACKAGE"; then
  echo "Installing..."
  sudo dpkg -i "$DEB_PACKAGE"
  sourcehawk -V
  sourcehawk --help
else
  echo "Unable to download/install deb package: $DOWNLOAD_URL"
  exit 1
fi
