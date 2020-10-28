FROM alpine:3.12

# Setup user and group
ENV GROUP=sourcehawk USER=sourcehawk
RUN addgroup ${GROUP} && adduser -G sourcehawk -D ${USER}

# Copy the native image executable into the image
ARG NATIVE_IMAGE_PATH
COPY --chown=${GROUP}:${USER} ${NATIVE_IMAGE_PATH} /usr/bin/sourcehawk

# Give the native image executable permissions
RUN chmod +x /usr/bin/sourcehawk

# Set the user
USER ${USER}

# Set the native image as the entrypoint
ENTRYPOINT ["/usr/bin/sourcehawk"]