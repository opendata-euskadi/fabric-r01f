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
			+ [instance_name]
			+ ...
		+ maven_libs
		+ local_libs
		+ workspaces
			+ master_[instance_name]
			+ ...
		+ projects
```

## [2]: Install Eclipse

a) Download the [eclipse IDE for Java Developers] from http://www.eclipse.org/downloads/eclipse-packages/

> BEWARE!! do NOT download the [eclipse IDE for Java EE Developers]

b) Extract the contents of the [eclipse] folder inside the previously downloaded eclipse ZIP to the `/{dev_home}/instances/[instance_name]`

c) Copy the `/{dev_home}/instances/[instance_name]/eclipse.ini` to eclipse.ini.original

d) Edit the `/{dev_home}/instances/[instance_name]/eclipse.ini` file and set this content:

> BEWARE!!!

* check that the org.eclipse.equinox.launcher versions still the SAME as those in the eclipse.ini.original file

* replace [instance_name] with it's real value

> Download lombok.jar from this site https://projectlombok.org/download, and copy to eclipse instance root dir.

```
	-clean
	-startup
	plugins/org.eclipse.equinox.launcher_1.3.200.v20160318-1642.jar
	--launcher.library
	plugins/org.eclipse.equinox.launcher.win32.win32.x86_64_1.1.400.v20160518-1444
	-product
	org.eclipse.epp.package.java.product
	--launcher.defaultAction
	openFile
	--launcher.XXMaxPermSize
	256M
	-showsplash
	org.eclipse.platform
	--launcher.defaultAction
	openFile
	--launcher.appendVmargs

	# JDK 1.8 <<<<<<<< USE JDK8 if runninig OEPE (Oracle Enterprise Pack)
	-vm
	d:/java/jdk8/jre/bin/server/jvm.dll
	-vmargs

	# JDK > 9: see  https://wiki.eclipse.org/Configure_Eclipse_for_Java_9
	--launcher.appendVmargs
	-vm
	d:/java/jdk12/bin/server/jvm.dll
	-vmargs
	--add-modules=ALL-SYSTEM


	# see [Runtime Options] http://help.eclipse.org/mars/topic/org.eclipse.platform.doc.isv/reference/misc/index.html
	# see http://stackoverflow.com/questions/316265/how-can-you-speed-up-eclipse/316535#316535
	-Dosgi.requiredJavaVersion=1.8
	-XX:+UseG1GC
	-XX:+UseStringDeduplication
	-Dosgi.requiredJavaVersion=1.8
	-Dosgi.clean=true
	-Duser.language=en
	-Duser.country=US
	-Dhelp.lucene.tokenizer=standard
	-javaagent:lombok.jar
	-Xbootclasspath/a:lombok.jar
	-Xms256m
	-Xmx1024m
	-Xverify:none
```

## [3]: Launch Eclipse

Launch eclipse.
When asked for the `[workspace]` location select: `/{dev_home}/workspaces/master_[instance_name]` (do **NOT** set use this workspace as default: don't ask again)
> **BEWARE** this workspace location will later act as a _template_ pre-configured workspace that will be copied when creating a **new** workspace


## [4]: Install plugins

a) **AJDT: AspectJ Development Tools** > 	http://download.eclipse.org/tools/ajdt/410/dev/update

b) **Eclipse GIT plugins** (from eclipse update site)

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

c) **Eclipse WTP tools** (from eclipse update site)

```
		  Web, XML, Java EE and OSGi Enterprise Development
			[X]	Eclipse Faceted Project Framework
			[X] Eclipse Faceted Project Framework JDT Enablement
			[X] Eclipse Java EE developer tools
			[X] Eclipse Java Web Developer Tools
			[X] Eclipse Web Developer Tools
			[X] Eclipse XSL Developer Tools
			[X] JavaScript Development Tools
			[X] JavaScript Development Tools Chromium/V8 Remote Debugger
			[X] m2e connector for mavenarchiver pom properties
			[X] m2e-wtp JAX-RS project configurator for Eclipse WTP.
			[X] m2e-wtp JPA project configurator for Eclipse WTP.
			[X] m2e-wtp project configurators for Eclipse WTP.
			[X] JST Server Adapters
			[X] JST Server Adapters Extensions
			[X] JST Server UI
			[X] WST Server Adapters
```
If you have compatibility problems uninstall "Eclipse XML Editors and Tools" checking the option "Update my installation to be compatible with the items being installed".

d) **[AnyEdit Tools]** either using the [eclipse marketplace] or from the update site at: http://andrei.gmxhome.de/eclipse/
		[X] Eclipse 3.8 - 4.11 plugins
			[X] AnyEditTools

## [5]: Clone R01F Git repository


## [6]: Configure plugins

a) **General**
> `[General] > [Startup & Shutdown] > Workspaces`: [x] Prompt for workspace on startup

b) **[Maven]**

> `[Maven > Download artifact sources]` : true
> `[Maven > Archetypes]` > Add a NEW remote catalog at: http://repo1.maven.org/maven2/archetype-catalog.xml 
> `[Maven > User Settings] > Global Settings`: {dev_home}/projects/fabric/r01f/docs/eclipse/maven/settings_{env}.xml

*BEWARE*: Ensure that `[Local Repository] (from merged user and global settings)` is `{dev_home}/maven_libs`


*BEWARE*: Some artifacts are NOT published at MAVEN CENTRAL; this is the case of javax.ejb / javax.servlet-api or javax.jms. The only workarround is to put all those artifacts at `{dev_home}/maven_libs/` manually


*BEWARE*: to create a weblogic fullclient jar to be used as external dependency:
```
	> cd \app-server\wls_10.3.6\wlserver\server\lib
	> java -jar d:\app-server\wls_10.3.6\modules\com.bea.core.jarbuilder_1.7.0.0.jar
```


c) **[Java]**

> Import `[compiler preferences]`: `[File] > [Import] > [Preferences]` browse filesystem and select `/{dev_home}/projects/fabric/r01f/docs/eclipse/preferences/pci_compiler_preferences.epf`

> `[Java] > [Editor] > [Templates]` add a NEW **Java** template named **_sep** with the following content

> /////////////////////////////////////////////////////////////////////////////////////////
> //	${cursor}
> /////////////////////////////////////////////////////////////////////////////////////////

> `[Java] > [Editor] > [Typing]`
>       - Automatically insert at correct position [X] semicolons [X] braces
>       - When pasting (remove check):  [-] Adjust indentation

d) **[AnyEdit Tools]**
> `[General] > [Editors] > [AnyEditTools]` Remove Trailing whitespace (DISABLE)

e) **Team Colaboration**
> `[Team] > [Ignored Resources] > add new pattern [Add Pattern] named */build/* `
This excludes .class files from synchronized files.


## [7]: Create a workspace for a project

Just copy the _template_ workspace folde: `/{dev_home}/workspaces/master_[instance_name]` with a new name id: `/{dev_home}/workspaces/my_project`
... now simply launch eclipse from  `/{dev_home}/instances/[instance_name]` as usual and when asked, select the workspace folder
