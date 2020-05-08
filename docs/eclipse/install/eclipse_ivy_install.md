# Eclipse install
=======================================

The following instructions creates an eclipse MASTER workspace that will be used as a TEMPLATE to
create any eclipse workspaces for any project

The idea is:

1 - Install eclipse and create a MASTER workspace (_a TEMPLATE workspace_)

2.- To create an eclipse workspace for a given project, just *copy* the MASTER (_or TEMPLATE_) eclipse workspace

## [1]: Create the file system structure
```
/{dev_home} = d:\ in windows or /develop in linux
	+ eclipse
		+ instances
			+ [instance_name]
		+ ivy_libs
		+ local_libs
		+ workspaces
			+ master_[instance_name]
```

## [2]: Install Eclipse

a) Download the [eclipse IDE for Java Developers] from http://www.eclipse.org/downloads/eclipse-packages/

> BEWARE!! do NOT download the [eclipse IDE for Java EE Developers]

b) Extract the contents of the [eclipse] folder inside the previously downloaded eclipse ZIP to the `/{dev_home}/eclipse/instances/[instance_name]`

c) Copy the `/{dev_home}/eclipse/instances/[instance_name]/eclipse.ini` to eclipse.ini.original

d) Edit the `/{dev_home}/eclipse/instances/[instance_name]/eclipse.ini` file and set this content:

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

	# JDK9: see  https://wiki.eclipse.org/Configure_Eclipse_for_Java_9
	--launcher.appendVmargs
	-vm
	d:/java/jdk9/bin/server/jvm.dll
	-vmargs
	--add-modules=ALL-SYSTEM

    # by default eclipse stores some plugin config at {USER_HOME}/.eclipse (ie c:/users/{user}/.eclipse in windows)
    # this vm argument CHANGES the location of these eclipse config
    -Duser.home={dev_home}/eclipse/.eclipse-legacy
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
When asked for the `[workspace]` location select: `/{dev_home}/eclipse/workspaces/master_[instance_name]` (do **NOT** set use this workspace as default: don't ask again)
> **BEWARE** this workspace location will later act as a _template_ pre-configured workspace that will be copied when creating a **new** workspace


## [4]: Install plugins

a) **AJDT: AspectJ Development Tools** > 	http://download.eclipse.org/tools/ajdt/47/dev/update

b) **IvyDE** > https://builds.apache.org/job/IvyDE-updatesite/lastSuccessfulBuild/artifact/trunk/build/   or    http://www.apache.org/dist/ant/ivyde/updatesite

```
	Window > Preferences > Ivy
				Classpath container:
					[X] Resolve dependencies in the workspace
					[X] Resolve dependencies transitively
				Settings
					Ivy user dir: D:\eclipse\ivy_libs
```
c) **Eclipse WTP tools** (from eclipse update site)
For Eclipse 2018-19 version use this site http://download.eclipse.org/releases/2018-09

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
			[X] JST Server Adapters
			[X] JST Server Adapters Extensions
			[X] JST Server UI
			[X] WST Server Adapters
```
If you have compatibility problems uninstall "Eclipse XML Editors and Tools" checking the option "Update my installation to be compatible with the items being installed".

d) **Collaboration Tools: SVN**

```
					[X] Subversive SVN Connectors
					[X] Subversive SVN JDT Ignore Extensions (Optional)
					[X] Subversive SVN Team Provider
```

**Eclipse Photon NOTE:** Eclipse Photon has discontinued the SVN support so in order to install SVN in Photon SVN MUST be installed from Oxygen update site: http://download.eclipse.org/releases/oxygen

For eclipse 2018-19 only check this:

				Collaboration
					[X] Subversive SVN Team Provider


e) **[Polarion SVN connectors]**

Goto `[Window]->[Preferences]->[Team]->[SVN]->[Connectors]`

Select AT LEAST [svnkit]

**NOTE:** If Polarion SVN connectors cannot be downloaded:

	1.- Goto https://polarion.plm.automation.siemens.com/products/svn/subversive/download

	2.- Select a polarion svn connector repository
			ie: http://community.polarion.com/projects/subversive/download/eclipse/6.0/update-site/?__hstc=2015854.4bd76f2b8b2bce1c569b50fed7cf3b42.1506603368284.1506603368284.1506603368284.1&__hssc=2015854.1.1506603368285&__hsfp=3974841547
	3.- Goto [Help] > [Install New Software]

	4.- Add a new repository and install

 If this DOES NOT WORKS, try to install from a ZIP file downloaded from: <http://community.polarion.com/projects/subversive/download/eclipse/6.0/builds/>

 If this DOES NOT EVEN WORKS try:

		1.- DELETE all org.polarion zip files from d:\eclipse\instances\{workspace_name}\plugins
		2.- DELETE all polarion ARTIFACTS from d:\eclipse\instances\{workspace_name}\artifacts.xml

f) **[AnyEdit Tools]** either using the [eclipse marketplace] or from the update site at: http://andrei.gmxhome.de/eclipse/

		[X] Eclipse 3.8 - 4.11 plugins
			[X] AnyEditTools


## [5]: Configure plugins

a) **General**
> `[General] > [Startup & Shutdown] > Workspaces`: [x] Prompt for workspace on startup

b) **[Ivy]**

> `[Classpath container] > Resolve dependencies in workspace` : true

> `[settings] > Ivy user dir`: /{dev_home}/ivy_libs

> `[settings] > Property files`: Add the file at `/{dev_home}/eclipse/projects_r01/base/r01fbDocs/ivy/r01.versions.properties}`

*BEWARE*: 
* Some artifacts are NOT published at MAVEN CENTRAL; this is the case of javax.ejb / javax.servlet-api or javax.jms. The only workarround is to put all those artifacts in {dev_home}/ivy_libs/local
just extract the file `[r01fbDocs]/ivy/artifacts-not-published-at-maven-central.zip`  at {dev_home}/ivy_libs/local

* to create a weblogic fullclient jar to be used as external dependency:

```
	> cd \app-server\wls_10.3.6\wlserver\server\lib
	> java -jar d:\app-server\wls_10.3.6\modules\com.bea.core.jarbuilder_1.7.0.0.jar
```


c) **[Java]**

> Import `[compiler preferences]`: `[File] > [Import] > [Preferences]` browse filesystem and select `/{dev_home}/eclipse/projects_r01/base/r01fbDocs/eclipse/preferences/pci_compiler_preferences.epf`

> `[Java] > [Editor] > [Templates]` add a NEW **Java** template named **_sep** with the following content

	/////////////////////////////////////////////////////////////////////////////////////////
	//	${cursor}
	/////////////////////////////////////////////////////////////////////////////////////////

> `[Java] > [Editor] > [Typing]`
>       - Automatically insert at correct position [X] semicolons [X] braces
>       - When pasting (remove check):  [-] Adjust indentation

d) **[AnyEdit Tools]**
> `[General] > [Editors] > [AnyEditTools]` Remove Trailing whitespace (DISABLE)

e) **Team Colaboration**
> `[Team] > [Ignored Resources] > add new pattern [Add Pattern] named */build/* `
This excludes .class files from synchronized files.


## [6]: Configure R01F

a) **[Java]**
> `[Java] > [Installed JREs]`: Add a NEW JRE named R01FB_JRE

b) **Local User Libraries**
> Import eclipse's local libs from `[r01fbDocs]/eclipse/libs/pci-localLibs_(linux|win)_userlibraries`
> `[Java] > [Build Path] > [User Libraries] > [Import]`

## [7]: Create a workspace for a project

Just copy the _template_ workspace folde: `/{dev_home}/eclipse/workspaces/master_[instance_name]` with a new name id: `/{dev_home}/eclipse/workspaces/my_project`

... now simply launch eclipse from  `/{dev_home}/eclipse/instances/[instance_name]` as usual and when asked, select the workspace folder

## [8]: Import SVN repos

Import preferences from [fabric/r01fEjie]/docs/eclipse/preferences

## [9]: Import SVN projects

    [r01fb]
        + code
            + trunk
                -- import ALL projects to {dev_home}/projects/legacy/r01f
        + config
            + trunk
                + r01fb
                    -- import ALL projects to {dev_home}/projects/legacy/r01f
        + docs
            + trunk
                + r01fb
                    -- import ALL projects to {dev_home}/projects/legacy/r01f
                    
## [10]: Ivy local libs

Since some internal deps are NOT published in public repos they MUST be copied manually into `{dev_home}/ivy-libs/local` from `{dev_home}/projects/legacy/r01f/r01fDocs/ivy/ivy-local.7z`

## [11]: Import eclipse workging-set

AnyEdit tools have a feature that allows importing eclipse's [workingsets]

    [preferences] > [import] > [working-sets]  and select file at `{dev_home}/projects/legacy/r01f/r01fDocs/eclipse/working-sets`
    



