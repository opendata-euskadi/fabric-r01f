CONFIG
===========================
The application config has TWO levels:
- public:                      anyone can see it

							   Project Name: [{appCode}Config]
							   		
- private by EJIE environment: only authorized people can access it

							   Project Name: [{appCode}ConfigByEnv]
							   This project has a folder for each EJIE enviornment:
							   	
										|-des
										|-sandbox
										|-prod
										
The aim of the existence of TWO separate projects for configs is that anyone at the outside (not from EJIE) do NOT have the need of  
private EJIE-dependent projects like `[{appCode}ConfigByEnv]` so just cloning the 'public' part of the code & config is
usually enough to run the app
... at the same time, inside EJIE, a `[{appCode}ConfigByEnv]` is used to store the environment-dependent or 'secret' config
	
At runtime, while loading the config:

- if an R01_ENV system property is set, config from the 'public' project (artifact) [{appCode}Config]
  is MIXED with config from the 'private' project (artifcat)  [{appCode}ConfigByEnv]
- if NO R01_ENV system property is set only config from the 'public' project (artifact) [{appCode}Config] is used

A Maven PROFILE is used so the 'private' project `[{appCode}ConfigByEnv]` is only required if `PROFILE=env_profile` 

If a Maven PROFILE is **NOT** set (the default _'public'_ case), the `[{appCode}ConfigByEnv]` dependency is NOT required

This way, the EJIE private config is 'secret' while anyone from the 'outside' (public) still can build the app without the EJIE private config artifact dependency 

The Maven profile is configured at the **WAR pom.xml* as:

		<profiles>
			<!-- Used when a profile is NOT set [mvn clean install] (public) -->
			<profile>
				<id>default_profile</id>
				<activation>
					<activeByDefault>true</activeByDefault>
				</activation>
			</profile>
			
			<!-- Used when setting a profile like [mvn clean install -Penv_profile] (ejie internal) -->
			<profile>
				<id>env_profile</id>
				<dependencies>	
					<dependency>
						<groupId>xxx</groupId>
						<artifactId>{appCode}ConfigByEnv</artifactId>
						<version>version</version>
					</dependency>
				</dependencies>
			</profile> 
		</profiles>
		
Later, while building the app with Maven:
- Inside EJIE the profile MUST be set: `mvn clean install -Penv_profile`
- Outside EJIE (public) the profile is NOT needed so just `mvn clean install`


		