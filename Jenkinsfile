piepeline
{
agent any
stages
{
stage("Code Checkout")
{
steps
{
sh "cd /root/SmartShare/"
sh "rm -Rf administrationservice"
sh "git clone git@github.com:vvshantha999/administrationservice.git"
sh "cd administrationservice"
sh "git checkout administrationservice.0.2"
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
    
