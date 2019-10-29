Update version
======================================
mvn versions:set -DnewVersion={new version}

Ej: 
mvn versions:set -DnewVersion=1.0.3-SNAPSHOT


Update with Flatten Pluggin
======================================
mvn clean flatten:flatten install