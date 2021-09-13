FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/pagopa-pm-cloud-mock-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]