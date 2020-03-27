@REM ======================================================================
@REM == DEVELOP HOME
@REM ======================================================================
echo ... set jdk COMMON env using JDK_FOLDER=%JDK_FOLDER%

@REM ======================================================================
@REM == SET GENERAL ENV
@REM ======================================================================
set JAVA_INSTALL_HOME=%DEVELOP_HOME%\java
set APP_SERVER_HOME=%DEVELOP_HOME%\app-server
set LIBS_HOME=%DEVELOP_HOME%\local-libs

@REM to use gem behind a proxy http://stackoverflow.com/questions/4418/how-do-i-update-ruby-gems-from-behind-a-proxy-isa-ntlm
@REM set HTTP_PROXY=http://{user}:{passwd}@intercon:8080

@REM ======================================================================
@REM == JVMS
@REM ======================================================================
set SUNJDK_VENDOR=Sun
set ORACLEJDK_VENDOR=Oracle

set JAVA_INSTALL_HOME=%DEVELOP_HOME%\java
set JAVA_HOME=%JAVA_INSTALL_HOME%\%JDK_FOLDER%
set JRE_HOME=%JAVA_INSTALL_HOME%\%JDK_FOLDER%\jre
set JAVA_VENDOR=%ORACLEJDK_VENDOR%
set PATH=%PATH%;%JAVA_HOME%\bin

@REM ======================================================================
@REM == VIRTUAL MACHINE OPTIONS
@REM ======================================================================
@REM optimizacion ver http://middlewaremagic.com/weblogic/?page_id=1096
set OTHER_VMOPTIONS=-Dsun.lang.ClassLoader.allowArraySyntax=true -Duser.language=en -Duser.country=US

@REM ======================================================================
@REM == MEMORY
@REM ======================================================================
set USER_MEM_ARGS=-Xms256m -Xmx512m -XX:PermSize=128m  -XX:MaxPermSize=256m 