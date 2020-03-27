set CATALINA_HOME=%DEVELOP_HOME%/app-server/apache-tomcat-9.0.31
set "JAVA_OPTS=%JAVA_OPTS% -javaagent:%CATALINA_HOME%/lib/aspectjweaver.jar -Daj.weaving.verbose=true
@rem set "JAVA_OPTS=%JAVA_OPTS% -javaagent:D:/develop/local_libs/aspectj/lib/aspectj1.8.13/aspectjweaver.jar -Daj.weaving.verbose=true
