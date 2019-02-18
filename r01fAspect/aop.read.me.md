INSTRUCCIONES
-----------------------------------------------------------------
El fichero META-INF/aop.xml debe estar presente en el classpath con el siguiente contenido:
	<aspectj>
		<aspects>
			<aspect name="r01f.aspects.dirtytrack.ConvertToDirtyStateTrackableAspect"/>
			<aspect name="r01f.aspects.dirtytrack.DirtyStateTrackableAspect"/>
			<aspect name="r01f.aspects.dirtytrack.ChangestTrackableMapAspect"/>
			...
		</aspects>
		<weaver options="-verbose -showWeaveInfo -debug">
			<include within="r01f.aspects.interfaces.dirtytrack..*"/>		<!-- Importante!! NO quitar -->
			
	 		<!--
	 			IMPORTANTE!	NO quitar estos includes ya que son tipos que el weaver debe conocer ya que se 
	 						utilizan en los aspectos, concretamente en los fields inyectados por los interfaces
	 							- DirtyStateTrackableAspect inyecta el miembro  _trackingStatus de tipo DirtyTrackingStatus
	 							- ChangesTrackableMapAspect inyecta el miembro _changesTracker de tipo CollectionChangesTracker<K>
	 						el weaver debe conocer los tipos DirtyTrackingStatus y CollectionChangesTracker
	 						Si se quitan los includes de estos tipos, NO se crea correctamente el m�todo para establecer los miembros
	 						_trackingStatus y _changesTracker
	 						Ej: si se quita el include <include within="r01f.types.dirtytrack.interfaces..*" />
	 							se lanza el error 
	 								java.lang.NoSuchMethodError: r01f/types/dirtytrack/interfaces/ChangesTrackableMap.ajc$interFieldSet$r01f_aspects_dirtytrack_ChangestTrackableMapAspect$r01f_types_dirtytrack_interfaces_ChangesTrackableMap$_changesTracker(Lr01f/types/dirtytrack/internal/CollectionChangesTracker;) 
	 		-->
	 		<include within="r01f.aspects.interfaces.dirtytrack..*"/>	
			<include within="r01f.types.dirtytrack.interfaces..*" />
			<include within="r01f.aspects.core.util..*"/>
			<exclude within="r01f.aspects.dirtytrack..*"/>
			<exclude within="r01f.types.lazy..*"/>
			<exclude within="r01f.types.dirtytrack.internal..*"/>
			<exclude within="r01f.core.dirtytrack..*"/>
			<exclude within="r01f.aspects.freezable..*"/>
			<exclude within="r01f.aspects.lazyload..*"/>
			<exclude within="r01f.aspects.logging..*"/>
			
			<!-- Tipos incluidos -->
	 		<include within="[paquete]..*"/>
	 		
	 		<!-- excluir el resto de tipos -->
	        <exclude within="*.*"/>
		</weaver>
	</aspectj>

NOTA IMPORTANTE sobre el classLoader:
------------------------------------
Es muy importante que el classloader utilizado para cargar el fichero aop.xml sea el classLoader del EAR
ya que de otra forma habr�a que "replicar" el fichero aop.xml en todos los WAR y EJBs (cada uno tiene su classLoader)
Para esto colocar el fichero aop.xml en:
	xxxEar/EarContent/APP-INF/classes/META-INF/aop.xml


Recubrimiento (weaving) en tiempo de carga en ECLIPSE
-----------------------------------------------------
Modificar la configuraci�n de ejecuci�n de la clase que contiene el m�todo main() 
	- Run Configurations... clase que contiene el m�todo main()
- En la pesta�a (x)=Arguments a�adir a los par�metros de la m�quina virtual (VM Arguments):
	-javaagent:D:/tools_workspaces/eclipse/libs/aspectj-1.7.4/lib/aspectjweaver.jar -Daj.weaving.verbose=true
	- En la pesta�a ClassPath a�adir dos user-entries:
				D:/tools_workspaces/eclipse/libs/aspectj-1.7.4/lib/aspectjrt.jar
				D:/tools_workspaces/eclipse/libs/aspectj-1.7.4/lib/aspectjweaver.jar

Recubrimiento (weaving) en tiempo de carga en WEBLOGIC
------------------------------------------------------
Para activar el recubrimiento en tiempo de ejecuci�n (load-time weaving):
1.- Cambios en el arranque de Weblogic
		A) Si se utiliza JROCKIT
				A.1 A�adir la siguiente opci�n al arrancar la m�quina virtual:
						set ASPECTJ_VMOPTIONS_JROCKIT=-Daj.weaving.verbose=true -Xmanagement:class=org.aspectj.weaver.loadtime.JRockitAgent
				A.2 A�adir al ClassPath
						set ASPECTJ_CLASSPATH=D:/tools_workspaces/eclipse/libs/aspectj-1.7.4/lib/aspectjrt.jar
						set ASPECTJ_CLASSPATH_JROCKIT=D:/tools_workspaces/eclipse/libs/aspectj-1.7.4/lib/aspectjweaver.jar
		
		B) Si se utiliza la JVM normal
				B.1 A�adir la siguiente opci�n al arrancar la m�quina virtual:
						set ASPECTJ_VMOPTIONS=-Daj.weaving.verbose=true -javaagent:D:/tools_workspaces/eclipse/libs/aspectj-1.7.4/lib/aspectjweaver.jar
		 		B.2 A�adir al ClassPath
						set ASPECTJ_CLASSPATH=D:/tools_workspaces/eclipse/libs/aspectj-1.7.4/lib/aspectjrt.jar
 		
2.- Localizaci�n del fichero aop.xml
		Tal y como se explica en el documento organizacion_proyectos.txt, el objetivo es cargar el fichero aop.xml con el loader
		del EAR, para lo cual hay que poner el fichero aop.xml en:
			xxxEar/EarContent/APP-INF/classes/META-INF/aop.xml


Recubrimiento (weaving) en tiempo de compilacion
------------------------------------------------
	1.- Descargarse la �ltima versi�n de ant4eclipse : http://www.ant4eclipse.org/
	2.- Descomprimir el zip en d:/tools/eclipse/ant4eclipse
	3.- A�adir las siguientes entradas en Window -> Preferences -> Ant -> Runtime -> Classpath -> Ant Home Entries
			- El jar D:\tools\eclipse\ant4eclipse\org.ant4eclipse_2109
			- Todos los jar del directorio D:\tools\eclipse\ant4eclipse\libs
	
	NOTA: ant4eclipse NO funciona bien si el proyecto f�sicamente en el disco NO est� en la carpeta ra�z del workspace:
				Si el proyecto est� en: /[workspace_root]/myProject  		--> FUNCIONA
				Si el proyecto est� en: /[workspace_root]/[group]/myProject	--> NO FUNCIONA (por la carpeta group)

NOTA:
		Para lanzar con ANT o MAVEN, ver: http://denis-zhdanov.blogspot.com.es/2009/08/weaving-with-aspectj.html


NOTA:
		Para lanzar con ANT o MAVEN, ver: http://denis-zhdanov.blogspot.com.es/2009/08/weaving-with-aspectj.html