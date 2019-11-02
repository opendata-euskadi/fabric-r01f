m2eclipse executes the default phases and goals of maven into the internal Eclipse build workflow.

IDE usecase: 

edit sources, compile and run tests, NOT the complete Maven build
 
... so for example, plugin goals that publish build results to a remote repository can be ignored without any adverse side effects, while java source code generation is necessary

m2eclipse requires EXPLICIT INSTRUCTIONS about what to do with all Maven plugins bound to “interesting” phases of a project build lifecycle

These INSTRUCTUONS are called [project build lifecycle mapping] or simply [lifecycle mapping] because they define how m2e maps information from pom.xml file to:
	
- Eclipse workspace project configuration 
- behaviour during Eclipse workspace build
	
Project build [lifecycle mapping] can be configured in:

- The pom.xml

					<build>
						<pluginManagement>
							<plugins>
								<plugin>
									<groupId>org.eclipse.m2e</groupId>
									<artifactId>lifecycle-mapping</artifactId>
									<version>1.0.0</version>
									<configuration>
										<lifecycleMappingMetadata>
											... put here the lifecycle metadata ...
										</lifecycleMappingMetadata>
									</configuraction>
								</plugin>
							</plugins>
						</pluginManagement>
					</build>``
				
									
- The `[lifecycle-mapping-metadata.xml]` file (see `[window] > [preferences] > [maven] > [lifecycle mappings]`): Just create a file called `[lifecycle-mapping-metadata.xml]` at the location configured at `[window] > [preferences] > [maven] > [lifecycle mappings]`
				
The content of this file is the same as the content of the <configuration> above:

						<lifecycleMappingMetadata>
							... put here the lifecycle metadata ...
						</lifecycleMappingMetadata>			 		
			
See:
 
- https://www.eclipse.org/m2e/documentation/m2e-execution-not-covered.html
- https://stackoverflow.com/questions/30063357/how-can-i-map-maven-lifecycle-phases-not-covered-by-the-eclipse-m2e-plugin