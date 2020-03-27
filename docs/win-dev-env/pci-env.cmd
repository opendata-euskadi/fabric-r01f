@REM ======================================================================
@REM == SET PCI ENVIRONMENT
@REM ======================================================================

@echo off

set DEVELOP_HOME=c:\develop

echo ###### PCI DEVELOP ENVIRONMENT AT: DEVELOP HOME=%DEVELOP_HOME%

@REM Setup GIT
call %DEVELOP_HOME%\git\git-env.cmd

@REM Setup JAVA
call %DEVELOP_HOME%\java\openjdk8-env.cmd

@REM Setup Maven
call %DEVELOP_HOME%\maven-3.6.3\mvn-env.cmd

@REM Setup Tomcat
call %DEVELOP_HOME%\app-server\tomcat9-env.cmd
