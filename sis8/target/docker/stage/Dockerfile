FROM openjdk:latest
WORKDIR /opt/docker
ADD --chown=daemon:daemon opt /opt
USER daemon
ENTRYPOINT ["/opt/docker/bin/akka-http-quickstart-scala"]
CMD []
