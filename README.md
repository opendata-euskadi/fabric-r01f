# R01F: Fabric

## Java coding standards

[Java coding standards](docs/coding_standards.md)

## development environment (no IDE)

[Installation guide (no ide)](docs/dev_env_install.md)

## Java

[Install java portable in windows](docs/java/how-to-install-jdk-portable.md)

## eclipse

[Installation guide (maven)](docs/eclipse/install/eclipse_maven_install.md)

[Installation guide (ivy)](docs/eclipse/install/eclipse_ivy_install.md)

## Tomcat hotswap in eclipse

[Tomcat hotswap](docs/java/java-hotswap.md)

## [AspectJ Projects] problem resolution in eclipse

If any of "r01fXXXAspectClasses" project does NOT compiles:

1. Ensure the `/src/main/aspect` folder is detected as a **source folder** by eclipse; if NOT just select the folder and add it as _source folder_
2. Ensure the project is detected by eclipse as an [AspectJ] project; if NOT just select project and `>> Configure >> Convert to AspectJ`
3. Usually after a full rebuild of the workspace projects, [aspectJ] projects do NOT compile: just select each [aspectj] project individually and clean (_rebuild_)

## R01F properties system

[How to use r01f XML properties](docs/r01f/r01f.properties.md)


