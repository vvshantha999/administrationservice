FROM maven:3.6.3-jdk-11
MAINTAINER  SmartShare
RUN mkdir /app/
WORKDIR /app/
COPY pom.xml /app/pom.xml
COPY src /app/src
RUN mvn clean install -DskipTests
