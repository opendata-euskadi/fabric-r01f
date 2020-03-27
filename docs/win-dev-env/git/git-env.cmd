set gitdir=%DEVELOP_HOME%\git\
set path=%gitdir%\cmd;%path%

@REM --cd-to-home param will set the user's home directory as the working directory 
@REM (as if Git for Windows was installed).
@REM 	git --cd-to-home will set C:\Users\{user} as the git home directory
@REM To set a HOME dir different than the user's home dir use:
@REM 	set HOME=%cd%/home		  (%cd% is the current dir)

@REM git --cd-to-home

set HOME=%DEVELOP_HOME%\projects

echo ... set git HOME to HOME=%HOME%


