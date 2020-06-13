pipeline
{
agent any
stages
{
stage("Code Checkout")
{
steps
{
sh "cd /var/lib/jenkins/workspace/SmartShare_Admin"
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
    
