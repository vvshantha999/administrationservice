pipeline
{
agent any
stages
{
stage("Build Details")
{
steps
{
sh "echo ${env.BUILD_NUMBER}"
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
    
