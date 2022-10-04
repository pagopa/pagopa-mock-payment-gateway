# build with maven 3.6.3 + java 8
FROM maven:3.6.3-ibmjava-8-alpine as workspace
COPY src /build/src
COPY pom.xml /build
COPY .git /build/.git
RUN mvn -f /build/pom.xml -q clean package

FROM openjdk:8 
EXPOSE 7954
RUN mkdir /mock-pgs
COPY --from=workspace /build/target/pagopa-mock-payment-gateway-*.jar /mock-pgs/mock-pgs.jar
ENTRYPOINT ["java", "-jar", "/mock-pgs/mock-pgs.jar"]