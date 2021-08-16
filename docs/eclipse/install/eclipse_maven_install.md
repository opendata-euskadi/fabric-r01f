# Eclipse install
=======================================

The following instructions creates an eclipse MASTER workspace that will be used as a TEMPLATE to
create any eclipse workspaces for any project

The idea is:

1 - Install eclipse and create a MASTER workspace (_a TEMPLATE workspace_)

2.- To create an eclipse workspace for a given project, just *copy* the MASTER (_or TEMPLATE_) eclipse workspace

## [1]: Create the file system structure
```
/{dev_home} = d:\develop in windows or /develop in linux
  + develop
    + eclipse
        + .eclipse <-- will contain eclipse plugin config that usually is located at {user-home}/.eclipse
      + [instance-name]
      + ...
    + eclipse_workspaces
      + master-[instance-name]
      + ...
    + maven_libs
    + local_libs
    + projects
    + DB
      + ddl_scripts
```

## [2]: Install Eclipse

a) Download the [eclipse IDE for Java Developers] from http://www.eclipse.org/downloads/eclipse-packages/

> BEWARE!! do NOT download the [eclipse IDE for Java EE Developers]

b) Extract the contents of the [eclipse] folder inside the previously downloaded eclipse ZIP to the `/{dev_home}/eclipse/[instance-name]`

c) Copy the `/{dev_home}/eclipse/[instance-name]/eclipse.ini` to eclipse.ini.original

d) Edit the `/{dev_home}/eclipse/[instance-name]/eclipse.ini` file and set this content:

> BEWARE!!!

* check that the org.eclipse.equinox.launcher versions still the SAME as those in the eclipse.ini.original file

* replace [instance-name] with it's real value

> Download lombok.jar from this site https://projectlombok.org/download, and copy to eclipse instance root dir.

NOTE: latest versions of eclipse have an embeded JRE so it is **NOT necessary** to set the JVM like (see eclipse.ini)
```
  # JDK 1.8 <<<<<<<< USE JDK8 if runninig OEPE (Oracle Enterprise Pack)
  -vm
  c_/develop/java/jdk8/jre/bin/server/jvm.dll
  
  # JDK > 9: see  https://wiki.eclipse.org/Configure_Eclipse_for_Java_9
  -vm
  c://develop/java/jdk15/bin/server/jvm.dll
```

So just **ADD the following to the `eclipse.ini` file**

```
  -clean
  -startup


  -vmargs
  --add-modules=ALL-SYSTEM
  --illegal-access=warn
  --add-opens java.base/java.lang=ALL-UNNAMED

  # by default eclipse stores some plugin config at {USER_HOME}/.eclipse (ie c:/users/{user}/.eclipse in windows)
  # this vm argument CHANGES the location of these eclipse config
  -Duser.home={dev_home}/eclipse/.eclipse

  # see [Runtime Options] http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/reference/misc/index.html
  # see http://stackoverflow.com/questions/316265/how-can-you-speed-up-eclipse/316535#316535
  -Duser.language=en
  -Duser.country=US
  -Dhelp.lucene.tokenizer=standard
  -javaagent:lombok-edge.jar
  -Xbootclasspath/a:lombok-edge.jar
  -Dosgi.requiredJavaVersion=11
```


## [3]: Launch Eclipse

Launch eclipse.
When asked for the `[workspace]` location select: `/{dev_home}/eclipse_workspaces/master_[instance-name]` (do **NOT** set use this workspace as default: don't ask again)
> **BEWARE** this workspace location will later act as a _template_ pre-configured workspace that will be copied when creating a **new** workspace


## [4]: Install plugins

a) **AJDT: AspectJ Development Tools** > 	http://download.eclipse.org/tools/ajdt/410/dev/update

(the LATEST build of AJDT can be found just opening https://download.eclipse.org/tools/ajdt/410/dev/update/ and looking for the ajdt-e410-2.2.4.xxx folder)

b) **Eclipse GIT plugins** (from eclipse update site https://download.eclipse.org/egit/updates) (install Git Client https://git-scm.com/download/gui/windows)

```
        Collaboration
            [X] Eclise GitHub integration with task focused interface
            [X] Git Integration for eclipse
            [X] Git Integration for eclipse - GitFlow Support
            [X] Git Integration for eclipse - Task focused interface
            [X] Java implementation of Git
            [X] Java implementation of Git - Optional Http support using Apache httpclient.
            [X] Java implementation of Git - Optional LFS support.
            [X] Java implementation of Git - Ssh support using Apache MINA sshd.

```

c) **Eclipse WTP tools** (from eclipse update site http://download.eclipse.org/releases/latest)

```
      Web, XML, Java EE and OSGi Enterprise Development
      [X] Eclipse Faceted Project Framework
      [X] Eclipse Faceted Project Framework JDT Enablement
      [X] Eclipse Java EE developer tools
      [X] Eclipse Java Web Developer Tools
      [X] Eclipse Web Developer Tools
      [X] Eclipse XML Editor and Tools
      [X] Eclipse XSL Developer Tools
      [X] JavaScript Development Tools
      [X] JavaScript Development Tools Chromium/V8 Remote Debugger
      [X] JST Server Adapters
      [X] JST Server Adapters Extensions
      [X] JST Server UI
      [X] m2e connector for mavenarchiver pom properties
      [X] m2e-wtp - JAX-RS configurator for WTP.
      [X] m2e-wtp - JPA configurator for WTP.
      [X] m2e-wtp - Maven Integration for WTP.
      [x] Wild Web Developer
      [X] WST Server Adapters

    General Purpouse Tools
      [X] m2e - slf4j over logback logging (Optional)
```
If you have compatibility problems uninstall "Eclipse XML Editors and Tools" checking the option "Update my installation to be compatible with the items being installed".

d) **[AnyEdit Tools]** either using the [eclipse marketplace] or from the update site at: http://andrei.gmxhome.de/eclipse/

    [X] Eclipse 4.10-4.14 plugins
      [X] AnyEditTools

e) Optional ** Genuitec DevStyle** (darkest dark theme: https://www.genuitec.com/products/devstyle/)

    Use update site: https://www.genuitec.com/updates/devstyle/ci/


## [5]: Clone R01F Git repository

(do NOT import projects in eclipse)

## [6]: Configure plugins

a) **General**
> `[General] > [Startup & Shutdown] > Workspaces`: [x] Prompt for workspace on startup

b) **[Maven]**

> `[Maven > Download artifact sources]` : true

> `[Maven > Hide folders of physically nested modules`: true  > this avoids duplicate search results of resources within nested projects

> `[Maven > Archetypes]` > Add a NEW remote catalog at: http://repo1.maven.org/maven2/archetype-catalog.xml

> `[Maven > User Settings] > Global Settings`: {dev_home}/projects/fabric-r01f/docs/eclipse/maven/settings_{env}.xml

> `[Maven > Error/Warnings] > Plugin execution not covered by lifecycle configuration`: change to WARNING!

By default, EGit automatically adds resources marked as "Derived" to .gitignore
... if `[Maven > Hide folders of physically nested modules` is **enabled** (true), eclipse adds nested modules' folders to .gitignore
This behavior can be disabled at `[Team] > [Git] > [Projects]` and **deselect** _"Automatically ignore derived resources by adding them to .gitignore"_

*BEWARE*:
* Ensure that `[Maven > User Settings][Local Repository] (from merged user and global settings)` is `{dev_home}/maven_libs`

* Some artifacts are NOT published at MAVEN CENTRAL; this is the case of javax.ejb / javax.servlet-api or javax.jms. The only workarround is to put all those artifacts at `{dev_home}/maven_libs/` manually (See fabric-r01f\docs\maven_local_libs\read.me file)

* create a weblogic fullclient jar to be used as external dependency:

```
  > cd \app-server\wls_10.3.6\wlserver\server\lib
  > java -jar d:\app-server\wls_10.3.6\modules\com.bea.core.jarbuilder_1.7.0.0.jar
```

**BEWARE Some maven artifacts are loaded form EJIE reports and the IZENPE certs are needed**

Import the [IZENPE CERTS] at the [jdk] used to start eclipse (seee -vm param above)
see [Maven certs install](../maven/maven_certs.read.me) for details

open the `eclipse.ini` file and locate the JRE folder, something like:

  -vm
  plugins/org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_15.0.2.v20210201-0955/jre/bin

open a terminal and using this path:

  cd {develop_home}/eclipse/{eclipse-version}/plugins/{jre-version}/jre/bin

then install the certs:

```
keytool -keystore ..\lib\security\cacerts -import -file {develop_home}\projects\fabric\r01f\docs\eclipse\maven\certs\izenpe.com.cer -alias izenpe_root -storepass changeit  
keytool -keystore ..\lib\security\cacerts -import -file {develop_home}\projects\fabric\r01f\docs\eclipse\maven\certs\CAAAPPVascas.cer -alias CAAAPPVascas -storepass changeit  
keytool -keystore ..\lib\security\cacerts -import -file {develop_home}\projects\fabric\r01f\docs\eclipse\maven\certs\builds1.alm02.itbatera.euskadi.eus.cer -alias builds1.alm02.itbatera.euskadi.eus -storepass changeit  
```


c) **[Java]**

_JDK8_  
Create a new [JRE]: `[Java] > [Installed JREs] > [Add]` pointing to a JDK8 at  `{dev_home}/java/jdk8` **Make this JRE the DEFAULT one**

Create another [JRE] pointing to a JDK8 hot-deploy *patched* JDK8 at `{dev_home}/java/jdk8-hotswap`
see [how to installa a hotswap jre](../../java/java-hotswap.md to patch the JDK
set the JRE default JVM arguments: `-XXaltjvm=dcevm -javaagent:c:\develop\local-libs\hotswap-agent\hotswap-agent-1.4.0.jar`

_JDK11_  
Create a new [JRE]: `[Java] > [Installed JREs] > [Add]` pointing to a JDK8 at  `{dev_home}/java/jdk11` 

Create another [JRE] pointing to a JDK11 hot-deploy *patched* JDK11 at `{dev_home}/java/jdk11-hotswap`
see [how to installa a hotswap jre](../../java/java-hotswap.md to patch the JDK
set the JRE default JVM arguments: `-XX:HotswapAgent=fatjar`

Preferences
> Import `[compiler preferences]`: `[File] > [Import] > [Preferences]` browse filesystem and select `/{dev_home}/projects/fabric-r01f/docs/eclipse/preferences/pci_compiler_preferences.epf`

> `[Java] > [Editor] > [Templates]` add a NEW **Java** template named **_sep** with the following content

  /////////////////////////////////////////////////////////////////////////////////////////  
  //	${cursor}  
  /////////////////////////////////////////////////////////////////////////////////////////  

> `[Java] > [Editor] > [Typing]`

>       - Automatically insert at correct position [X] semicolons [X] braces
>       - When pasting (remove check):  [-] Adjust indentation

> Verify that `[Java] > [Compiler] > [Java Compliance level]` is 1.8.

d) **[AnyEdit Tools]**
> `[General] > [Editors] > [AnyEditTools]` Remove Trailing whitespace (DISABLE)

e) **Team Colaboration**
> `[Team] > [Ignored Resources] > add new pattern [Add Pattern] named */build/* `
This excludes .class files from synchronized files.


## [7]: Create a workspace for a project

Just copy the _template_ workspace folder: `/{dev_home}/eclipse_workspaces/master_[instance-name]` with a new name id: `/{dev_home}/eclipse_workspaces/my_project`

... now simply launch eclipse from  `/{dev_home}/eclipse/[instance-name]` as usual and when asked, select the workspace folder


## [8]: BEWARE antivirus! > https://www.genuitec.com/stop-slow-eclipse-myeclipse-startups/

Exclude folder at the antivirus config:

- the ´{dev_home}´ folder
- %user_home%/.eclipse
- %user_home%/.m2e
- %user_home%/.p2


## [9]: Tomcat config:

Configure a NEW java runtime `window > preferences > java > installed JREs` > see [java hotswap](/docs/java/java-hotswap.md)
Do NOT forget the JVM Default VM arg: `-XX:HotswapAgent=fatjar`

Create a NEW [run time environment] named [tomcat9] using the previously created JRE

Create a NEW [tomcat server] using the previously created [run time environment] and set:
- UNCHECK [modules auto-reload by default]
- Open [lauch configuration] and add to the [VM arguments]:  

```
-DEJIE_PROPERTIES_PATTERN=/default/[entityCode]/[entityCode].properties.xml -DEJIE_PROPERTY_LOADER=classPathLoader -Djava.endorsed.dirs="C:\develop\app-server\apache-tomcat-9.0.39\endorsed" -javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
```


	 

