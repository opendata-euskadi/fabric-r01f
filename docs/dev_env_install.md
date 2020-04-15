[PRE]
============================================================================
[1] - Create a DEVELOP folder. ie: c:\develop  (now on this folder is refered to as {develop}
	  NOTE: the final folder structure upon completing the installation should be:


			+ {develop}
			    + pci-env.cmd
				+ git
					+ git-env.cmd
				+ projects
					+ fabric-r01f
					+ fabric-r01fBusinessServices
					+ fabric-r01fCOREServices
					+ fabric-r01fUI
					+ zuzenean-panic-button
				+ java
					+ jdk8-env.cmd
					+ jdk8
				+ app-server
					+ apache-tomcat-9.0.22
				+ maven
					+ settings.xml
					+ mvn.cmd
					+ maven-libs
					+ apache-maven-3.6.1

[2] - Create a file under {develop} named [pci-env.cmd] with the following content:

			@REM ======================================================================
			@REM == SET PCI ENVIRONMENT
			@REM ======================================================================
			set DEVELOP_HOME={develop}


[A] - Install GIT for windows
============================================================================
[1] - Download git for windows PORTABLE version from:
			https://git-scm.com/download/win

[2] - Run the "installer" so it unzips the content at:
			{develop}\git\

	  *NOTE*: if you decide to unpack the archive using 7-Zip manually, you must
              run the [post-install.bat] script. Git will not run correctly
              otherwise.

[3] - Create a cmd file named [git-env.cmd] with the following content:

			set gitdir=%DEVELOP_HOME%\git\
			set path=%gitdir%\cmd;%path%
			set HOME=%DEVELOP_HOME%\projects

[4] - Modify the {develop}\pci-env.cmd to include the following lines:

			@REM Setup GIT
			call %DEVELOP_HOME%\git\git-env.cmd

[6] - Configure GIT

		a) Create a PROJECTS folder under {develop}
				c:\>cd {develop}
				{develop}>mkdir projects

		b) Open a NEW cmd window and run:
				c:\>cd {develop}
				{develop}>pci-env.cmd

		b) Set up .gitconfig at {develop}\projects
				{develop}>cd projects
				{develop}\projects>git config --global user.name "PCI"
				{develop}\projects>git config --global user.email pci@ejie.eus

[C] - INSTALL JAVA PORTABLE
==================================================================================
[1] - Download java8 from https://download.oracle.com/otn/java/jdk/8u221-b11/230deb18db3e4014bb8e3e8324f81b43/jdk-8u221-windows-x64.exe

[2] - Open the downloaded file with 7-Zip

[3] - Go inside [temp_folder]\.rsrc\1033\JAVA_CAB10
	  ...there'll be a file named 111
	  
[4] - Open the 111 file with 7-zip, it'll contain the tools.zip file

[5] - Open the tools.zip with 7-Zip and extract its contents to {develop}\java\jdk8

[6] - From within this directory, search for all .pack files and extract them into .jar files, using unpack2000.exe command line tool found in the bin subdirectory.

The following windows prompt command does the trick when executed from within the extracted directory:

		c:\>cd {develop}\java\jdk8
		for /r %i in (*.pack) do .\bin\unpack200.exe %i %~pi%~ni.jar


[7] - Create the [jdk8-env.cmd] env file at {develop}\java\

			@REM ======================================================================
			@REM == SET GLOBAL ENV
			@REM ======================================================================
			set JAVA_INSTALL_HOME=%DEVELOP_HOME%/java

			@REM to use gem behind a proxy http://stackoverflow.com/questions/4418/how-do-i-update-ruby-gems-from-behind-a-proxy-isa-ntlm
			@REM set HTTP_PROXY=http://{user}:{password}@{proxy_host}:{proxy_port}

			echo ...setting jdk env

			@REM == JDK ================================================================
			set JAVA_HOME=%JAVA_INSTALL_HOME%/jdk8
			set JRE_HOME=%JAVA_HOME%/jre
			set JAVA_VENDOR=Oracle
			set PATH=%JAVA_HOME%/bin;%PATH%

			@REM ======================================================================
			@REM == VIRTUAL MACHINE OPTIONS
			@REM ======================================================================
			@REM optimizacion ver http://middlewaremagic.com/weblogic/?page_id=1096
			set OTHER_VMOPTIONS=-Dsun.lang.ClassLoader.allowArraySyntax=true -Duser.language=en -Duser.country=US

			@REM ======================================================================
			@REM == MEMORY
			@REM ======================================================================
			set USER_MEM_ARGS=-Xms256m -Xmx512m -XX:PermSize=128m  -XX:MaxPermSize=256m

[4] - Modify the {develop}\pci-env.cmd to include the following lines:

			@REM Setup JAVA
			call %DEVELOP_HOME%\java\jdk8-env.cmd

[8] - Test opening a NEW cmd window:

		c:\>cd {develop}
		{develop}>pci-env.cmd
		{develop}\java>java -version
				 ... should print something like:
							java version "1.8.0_221"
							Java(TM) SE Runtime Environment (build 1.8.0_221-b11)
							Java HotSpot(TM) 64-Bit Server VM (build 25.221-b11, mixed mode)

[D] - INSTALL MAVEN
==================================================================================
[1] - Download maven from: https://maven.apache.org/download.cgi

[2] - Create a [maven] folder under {develop}

		c:\>mkdir {develop}\maven

[3] - Create a folder for the maven libs under {develop}\maven

		c:\>mkdir {develop}\maven\maven-libs

[3] - Extract the maven downloaded ZIP under {develop}\maven (ie: {develop}\maven\apache-maven-3.6.1

[4] - Copy the file {develop}\maven\apache-maven-3.6.1\conf\settings.xml to {develop}\maven

[5] - Edit the previously copied {develop}\maven\settings.xml and change the [localRepository] path:

		  <!-- localRepository
		   | The path to the local repository maven will use to store artifacts.
		   |
		   | Default: ${user.home}/.m2/repository
		  <localRepository>/path/to/local/repo</localRepository>
		  -->
		  <localRepository>{develop}\maven\maven-libs</localRepository>

	  NOTE: If running maven BEHIND a PROXY, it should be necessary to set the proxy config:

[5] - Create a [mvn-env.cmd] file at {develop}\maven with the following content:

			set PATH=%PATH%;%DEVELOP_HOME%\maven\
			set MAVEN_HOME=%DEVELOP_HOME%\maven\apache-maven-3.6.1

[6] - Create a [mvn.cmd] file at {develop}\maven with the following content to force maven to use
	  the settings.xml file under {develop}\maven

			call %DEVELOP_HOME%\maven\apache-maven-3.6.1\bin\mvn.cmd --global-settings "%DEVELOP_HOME%\maven\settings.xml" %*


[7] - Modify the {develop}\pci-env.cmd to include the following lines:

			@REM Setup Maven
			call %DEVELOP_HOME%\maven\mvn-env.cmd

[8] - Test opening a NEW cmd window

			c:\>cd {develop}
			{develop}>pci-env.cmd
			{develop}>mvn -v  (should print version info)
			{develop}>mvn --help  (should print some help info)


[E] - INSTALL TOMCAT
==================================================================================
[1] - Download tomcat win-64 portable (https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.22/bin/apache-tomcat-9.0.22-windows-x64.zip)

[2] - Extract to {develop}\app-server\

[3] - Create a [tomcat9-env.cmd] at {develop}/app-server with the following content:

			@REM ...setting the tomcat9 env
			set CATALINA_HOME=%DEVELOP_HOME%/app-server/apache-tomcat-9.0.22

[4] - Create a [tomcat9-startup.cmd] and [tomcat9-shutdown.cmd] at {develop}/app-server with the following content:

	  [tomcat9-startup.cmd]
			call %DEVELOP_HOME%\app-server\apache-tomcat-9.0.22\bin\startup.bat

	  [tomcat9-shutdown.cmd]
			call %DEVELOP_HOME%\app-server\apache-tomcat-9.0.22\bin\shutdown.bat

[5] - Modify the {develop}\pci-env.cmd to include the following lines:

			@REM Setup Tomcat
			call %DEVELOP_HOME%\app-server\tomcat9-env.cmd

[6] - Test opening a NEW cmd window

		c:\>cd {develop}
		{develop}>pci-env.cmd
		{develop}>cd app-server\
		{develop}\app-server>tomcat9-startup.cmd  (the server should start without problems)


[F] - RECAP
==================================================================================
The FINAL {develop}\pci-env.cmd file should look like:

	@REM ======================================================================
	@REM == SET PCI ENVIRONMENT
	@REM ======================================================================
	set DEVELOP_HOME=d:\develop-public

	@REM Setup GIT
	call %DEVELOP_HOME%\git\git-env.cmd

	@REM Setup JAVA
	call %DEVELOP_HOME%\java\jdk8-env.cmd

	@REM Setup Maven
	call %DEVELOP_HOME%\maven\mvn-env.cmd

	@REM Setup Tomcat
	call %DEVELOP_HOME%\app-server\tomcat9-env.cmd


[G] - CLONE GITHUB REPOS
==================================================================================
In a NEW cmd window

	c:\>cd {develop}
	{develop}>pci-env.cmd
	{develop}>cd projects
	{develop}\projects>git clone https://github.com/opendata-euskadi/fabric-r01f.git
	{develop}\projects>git clone https://github.com/opendata-euskadi/fabric-r01fBusinessServices.git
	{develop}\projects>git clone https://github.com/opendata-euskadi/fabric-r01fCOREServices.git
	{develop}\projects>git clone https://github.com/opendata-euskadi/fabric-r01fUI.git
	...

	After this, the contents of {develop}\projects should be:
		/.gitconfig
		/[fabric-r01f]
		/[fabric-r01fBusinessServices]
		/[fabric-r01fCOREServices]
		/[fabric-r01fUI]
		...

[H] - COMPILE PROJECTS
==================================================================================
	c:\>cd {develop}\projects\fabric-r01f
	{develop}\projects\fabric-r01f>mvn clean install

	c:\>cd {develop}\projects\fabric-r01fBusinessServices
	{develop}\projects\fabric-r01fBusinessServices>mvn clean install -Dmaven.test.skip=true

	c:\>cd {develop}\projects\fabric-r01fCOREServices
	{develop}\projects\fabric-r01fCOREServices>mvn clean install -Dmaven.test.skip=true

	c:\>cd {develop}\projects\fabric-r01fUI
	{develop}\projects\fabric-r01fUI>mvn clean install -Dmaven.test.skip=true

	...

