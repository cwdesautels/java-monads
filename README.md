# Java Monads

## Usage ##
See central: https://search.maven.org/artifact/io.github.cwdesautels/monads

## Manual release ##
Maven must have visibility to Sonar credentials, assume they are in Maven settings-sonar.xml keyed to server-id `ossrh`.

1. `mvn --settings ~/.m2/settings-sonar.xml release:clean release:prepare`
2. `mvn --settings ~/.m2/settings-sonar.xml release:perform`

## Release requirements ##
* Pom metadata requirements: https://central.sonatype.org/pages/requirements.html
* Maven release guide: https://central.sonatype.org/pages/apache-maven.html
* PGP guide: https://central.sonatype.org/pages/working-with-pgp-signatures.html 
