[Java HotSwap] (jdk11)
============================================================================

see [HotswapAgent](http://hotswapagent.org/) and [jdk11 install guide](http://hotswapagent.org/mydoc_quickstart-jdk11.html)

# [1] - Download a jdk-11-dcevm

Download a [jdk-11-dcevm](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases)

# [2] - Install (extract) the JDK to `{develop_home}\java\jdk-11.0.9+1-hotswap`

# [3] - Configure a NEW [JRE] in eclipse pointing to the patched JRE and using the [hotswap agent]

5.1) Open Eclipse

5.2) Goto `Window > Preferences > Java > Intalled JREs`

5.3) Create a *NEW* JDK named `openjdk11-hotswap` pointing to the patched JDK: `{develop_home}\java\jdk-11.0.9+1-hotswap` *(DO NOT select it as DEFAULT JRE)*

5.4) At the `Default VM args` set: `-XX:HotswapAgent=fatjar`

# [4] - Configure [Tomcat] to use the patched [JRE]

6.1) Goto `Windows > Preferences > Server > Runtime Environments`

6.2) Edit the `tomcat9` [runtime environment]

6.3) Change it's JRE to the previously created one `openjdk11-hotswap`




[Java HotSwap] (jdk8)
============================================================================

see [HotswapAgent](http://hotswapagent.org/) and [jdk8 install guide](http://hotswapagent.org/mydoc_quickstart.html)

# [1] - Download DCEVM [Dynamic Code Evolution]

Goto [DCEVM git hub site](https://github.com/dcevm/dcevm) and dowload the [latest release of DCEVM](https://github.com/dcevm/dcevm/releases)
(at 2019 Oct, the latest release is *DCEVM (light) for Java 8u181 build 2*

Note that DCEVM is for *JDK 8u181*

Copy the downloaded jar file (`DCEVM-8u181-installer-build2.jar`) to `{develop_home}\java\`

# [1] - Download the JDK with the version supported by the previously downloaded DCEVM version (JDK 8u181)

Goto [Adopt OpenJDK](https://adoptopenjdk.net/) and select *[Release archive & nightly builds]* to download an specific version of the JDK
Browse OpenJDK versions to find JDK 8u181 and download it

# [2] - Install (extract) the JDK to `{develop_home}\java\openjdk8u181_hotswap`

# [3] - Patch the JDK

Run:

  java -jar {develop_home}\java\DCEVM-8u181-installer-build2.jar

This will show a graphical UI in which all detected JVMs are shown; find the previously installed `openjdk8u181_hotswap` JVM _(if it's NOT listed just browse and find it)_

select `openjdk8u181_hotswap` JVM and click at *[Replace by DCEVM]* and *[Install DCEVM as altjvm]*

(_ensure the jdk is patched_)

# [4] - Download latest release of [hotswap agent]

4.1) Download [hotswap agent](https://github.com/HotswapProjects/HotswapAgent/releases)

4.2) Move the downloaded jar file to `{develop_home}\local_libs\hotswap-agent\`

# [5] - Configure a NEW [JRE] in eclipse pointing to the patched JRE and using the [hotswap agent]

5.1) Open Eclipse

5.2) Goto `Window > Preferences > Java > Intalled JREs`

5.3) Create a *NEW* JDK named `openjdk8u181_hotswap` pointing to the patched JDK: `{develop_home}\java\openjdk8u181_hotswap` *(DO NOT select it as DEFAULT JRE)*

5.4) At the `Default VM args` set: `-XXaltjvm=dcevm -javaagent:{develop_home}\local_libs\hotswap-agent\hotswap-agent.jar`

# [6] - Configure [Tomcat] to use the patched [JRE]

6.1) Goto `Windows > Preferences > Server > Runtime Environments`

6.2) Edit the `tomcat9` [runtime environment]

6.3) Change it's JRE to the previously created one `openjdk8u181_hotswap`


