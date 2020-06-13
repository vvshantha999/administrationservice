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
sh "git clone git@github.com:vvshantha999/administrationservice.git"
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
}
    
