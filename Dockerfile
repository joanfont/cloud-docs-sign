FROM library/openjdk:8-jdk-alpine
LABEL maintainer="Joan Font<joanfont@uoc.edu>"

VOLUME /tmp
EXPOSE 8080

ADD build/libs/cloud-docs-sign-1.0.0.jar app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]