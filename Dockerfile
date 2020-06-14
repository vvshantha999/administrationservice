FROM maven:3.6.3-jdk-11
MAINTAINER  SmartShare
RUN mkdir /app
WORKDIR /app
COPY * /app
RUN mvn clean install -DskipTests
