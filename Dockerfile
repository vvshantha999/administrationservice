FROM maven:3.6.3-jdk-11
MAINTAINER  SmartShare
RUN mkdir /app/
WORKDIR /app/
COPY pom.xml /app/pom.xml
COPY src /app/src
RUN mvn clean install -DskipTests

FROM openjdk:11-jre-slim
COPY --from=build /app/target/administrationservice-0.0.1-SNAPSHOT.jar /usr/local/lib/administrationservice-0.0.1-SNAPSHOT.jar
RUN cd /usr/local/lib/ && ls -lrt && cksum administrationservice-0.0.1-SNAPSHOT.jar
# EXPOSE 80
# ENTRYPOINT ["java","-jar","/usr/local/lib/administrationservice-0.0.1-SNAPSHOT.jar"]
