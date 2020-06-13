pipeline
{
agent any
stages
{
stage("Code Checkout")
{
steps
{
sh "echo "Code has been checked-out successfully""
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
    
