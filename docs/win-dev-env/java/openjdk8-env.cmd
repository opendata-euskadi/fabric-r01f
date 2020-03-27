@REM ======================================================================
@REM == BEWARE: SET DEVELOP_HOME VAR
@REM ======================================================================
echo ... set openJDK8 env: DEVELOP HOME=%DEVELOP_HOME%

@REM ======================================================================
@REM set the folder where the jdk resides BEFORE calling [pcienv_jdk_common]
@REM ======================================================================
set JDK_FOLDER=jdk8u242-b08

@REM ======================================================================
@REM == SET GENERAL ENV
@REM ======================================================================
call %DEVELOP_HOME%\java\jdk-common-env.cmd





