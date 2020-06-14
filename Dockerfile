FROM openjdk:11-jre-slim
COPY /var/lib/jenkins/workspace/SmartShare_AdminSVC/target/administrationservice-0.0.1-SNAPSHOT.jar /usr/local/lib/administrationservice-0.0.1-SNAPSHOT.jar
RUN cd /usr/local/lib/ && ls -lrt && cksum administrationservice-0.0.1-SNAPSHOT.jar
# EXPOSE 80
# ENTRYPOINT ["java","-jar","/usr/local/lib/administrationservice-0.0.1-SNAPSHOT.jar"]
