Maven: add a local artifact repository
======================================
1. Add a server (ie: a local artifactory)

	  <server>
	    <id>local_artifactory</id>
	    <configuration>
	     <httpConfiguration>
	          <put>
	             <readTimeout>5000</readTimeout> <!-- milliseconds -->
	             <connectionTimeout>5000</connectionTimeout> <!-- milliseconds -->
	          </put>
	        </httpConfiguration>
	    </configuration>
	  </server>

2. Add a profile

	   <profile>
	      <id>local_artifactory</id>

	      <activation>
	        <jdk>local_artifactory</jdk>
	      </activation>

	      <repositories>
	        <repository>
	          <id>local_artifactory</id>
	          <name>Repository for Localhost</name>
	          <url>http://localhost:9040/artifactory/local-repository/</url>
	          <layout>default</layout>
	        </repository>
	      </repositories>
	    </profile>

3. Activate the profile

	  <activeProfiles>
	    <activeProfile>local_artifactory</activeProfile>
	  </activeProfiles>