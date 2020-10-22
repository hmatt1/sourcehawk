# Minima Debian Base Image
FROM bitnami/minideb:jessie

# Build Arguments
ARG DEBIAN_DIRECTORY
ARG DEB_FILE

# Copy the debian package files
COPY ${DEBIAN_DIRECTORY} /debian/

# Create the debian package
RUN dpkg-deb --build /debian ${DEB_FILE}