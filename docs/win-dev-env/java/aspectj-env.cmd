echo ... set aspectj env

@REM ======================================================================
@REM == AspectJ
@REM ======================================================================
set ASPECTJ_WEAVER_PATH=%LIBS_HOME%\aspectj\lib\aspectjweaver.jar
set ASPECTJ_PRECLASSPATH=%ASPECTJ_WEAVER_PATH%

set ASPECTJ_VMOPTIONS=-Daj.weaving.verbose=true -javaagent:%ASPECTJ_WEAVER_PATH% -Daj.weaving.verbose=true 

@REM if "%JAVA_VENDOR%" == "%ORACLEJDK_VENDOR%" (
@REM 	echo ***** ASPECTJ: JROCKIT only for Java 1.3 / 1.4
@REM 	set ASPECTJ_VMOPTIONS=-Daj.weaving.verbose=true -Xmanagement:class=org.aspectj.weaver.loadtime.JRockitAgent 
@REM ) else (
@REM 	echo ***** ASPECTJ: jdk
@REM 	set ASPECTJ_VMOPTIONS=-Daj.weaving.verbose=true -javaagent:%ASPECTJ_WEAVER_PATH% -Daj.weaving.verbose=true
@REM )

set JAVA_OPTIONS=%JAVA_OPTIONS% %ASPECTJ_VMOPTIONS%
