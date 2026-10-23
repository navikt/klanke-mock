FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21@sha256:dab3a36c1df030f6e577f7cc8f81ad8c89b55b2c07aae95bd8d7e5508f3ce5f6
ENV TZ="Europe/Oslo"
COPY build/libs/app.jar app.jar
CMD ["-jar","app.jar"]