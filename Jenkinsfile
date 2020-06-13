pipeline
{
agent any
stages
{
stage("Setting Up Environment")
{
steps
{
sh "export JAVA_HOME=/usr/lib/jvm/jdk-11.0.1"
sh "export MAVEN_HOME=/opt/Maven/apache-maven-3.6.3"
}
}
stage("Maven Compilation")
{
steps
{
sh "/opt/Maven/apache-maven-3.6.3/bin/mvn clean install -DskipTests"
}
}
}
}
    
