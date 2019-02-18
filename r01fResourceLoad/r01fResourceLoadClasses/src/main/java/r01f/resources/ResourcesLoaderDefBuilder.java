package r01f.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;
import r01f.resources.ResourcesLoaderDef.ResourcesLoaderType;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.MapEntry;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertyLocation;

/**
 * Builder for {@link ResourcesLoaderDef} objects
 * 
 * The {@link ResourcesLoaderDef} is usually loaded from a {@link XMLProperties} file, where it's defined as:
 * <ul>
 * <li>XML properties file at the classpath.
 * 		  <pre class="brush:xml">
 * 			<resourcesLoader name='myResourcesLoader' type='CLASSPATH'>
 * 				<!-- PERIODIC, BBDD, CONTENT_SERVER_FILE_LAST_MODIF_TIMESTAMP, FILE_LAST_MODIF_TIMESTAMP, VOID -->
 *				<reloadControl impl='PERIODIC' enabled='true' checkInterval='2s'/>
 * 			</resourcesLoader>
 *		  </pre>
 * </li>
 * <li>XML properties file at a BBDD reg
 * 		  <pre class="brush:xml">
 * 			<resourcesLoader name='myOtherResourcesLoader' type='BBDD'>
 * 				<props>
 * 					<conx>MyConx</conx>
 * 				</props>
 * 			</resourcesLoader>
 * 		 </pre>
 * </li>
 * </ul> 
 * If the XML as string is available:
 * <pre class='brush:java'>
 * 		ResourcesLoaderDef def = ResourcesLoaderDefBuilder.forDefinition("myLoaderDef",theXML);
 * </pre> 
 * Using a {@link XMLProperties} file:
 * <pre class='brush:java'>
 * 		ResourcesLoaderDef def = ResourcesLoaderDefBuilder.forDefinitionAt(xmlProperties,
 * 																		   AppCode.forId("r01"),AppComponent.forId("default"),
 * 																		   "/properties/resourcesLoaders/myResLoader");
 * </pre>
 * Building the object by hand:
 * <pre class='brush:java'>
 * 		ResourcesLoaderDef def = ResourcesLoaderDefBuilder.create("myResLoader")
 * 														  .usingClassPathResourcesLoader()
 * 														  .reloadingAsDefinedAt(ResourcesReloadControlDefBuilder.createForVoidReloading());
 * </pre>
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ResourcesLoaderDefBuilder 
  implements IsBuilder {
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	public static ResourcesLoaderDef from(final String defXml) {
		return ResourcesLoaderDefBuilder.from(new ByteArrayInputStream(defXml.getBytes()));
	}
	public static ResourcesLoaderDef from(final Node dexXmlNode) {
		return ResourcesLoaderDefParser.parse(dexXmlNode); 	// the marshaller "built-in" object marshaller is provided
	}
	public static ResourcesLoaderDef from(final InputStream defXmlIS) {
		return ResourcesLoaderDefParser.parse(defXmlIS);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEFAULT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the default {@link ResourcesLoaderDef}O
	 */
	public static ResourcesLoaderDef defaultLoaderDef() {
		return ResourcesLoaderDef.DEFAULT;
	}
///////////////////////////////////////////////////////////////////////////////
// 	BUILDERS FROM A XMLProperties file
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads a {@link ResourcesLoaderDef} from a {@link XMLProperties} file
	 * @param id the {@link ResourcesLoaderDef} unique identifier
	 * @param defXml the {@link ResourcesLoader} XML format definition
	 * @return the {@link ResourcesLoaderDef} java object
	 * @throws IllegalArgumentException if the definition XML is NOT valid
	 */
	public static ResourcesLoaderDef forDefinition(final String id,final String defXml) {
		return ResourcesLoaderDefBuilder.forDefinition(id,new ByteArrayInputStream(defXml.getBytes()));
	}
	/**
	 * Loads a {@link ResourcesLoaderDef} from a {@link XMLProperties} file
	 * @param id the {@link ResourcesLoaderDef} unique identifier
	 * @param defXmlIS the {@link ResourcesLoader} XML format definition
	 * @return the {@link ResourcesLoaderDef} java object
	 * @throws IllegalArgumentException if the definition XML is NOT valid.
	 */
	public static ResourcesLoaderDef forDefinition(final String id,
												   final InputStream defXmlIS) throws IllegalArgumentException {
		ResourcesLoaderDef resLoaderDef = null;
		try {
			resLoaderDef = ResourcesLoaderDefParser.parse(defXmlIS);
		} catch(Throwable th) {
			throw new IllegalArgumentException("The ResourcesLoader definition could not be loaded from the provided XML file. Maybe the XML file is not well formed: " + th.getMessage(),
											   th);
		}
		if (resLoaderDef != null) resLoaderDef.setId(id);
		return resLoaderDef;
	}
	/**
	 * Loads a {@link ResourcesLoaderDef} from a {@link XMLProperties} file
	 * @param xmlProperties an instance of the XMLProperties object
	 * @param appCode the appCode
	 * @param component the app component
	 * @param resLoaderDefPath Path to the {@link ResourcesLoader} definition in the XML properties
	 * 						 <b>SIDE NOTE:</b>the resLoaderDefId=DefaultClassPathLoaderDef loads the default definition.
	 * @return The {@link ResourcesLoaderDef} object that contains the {@link ResourcesLoader} definition
	 */
	public static ResourcesLoaderDef forDefinitionAt(final XMLProperties xmlProperties,
												     final AppCode appCode,final AppComponent component,final Path resLoaderDefPath) {
		return ResourcesLoaderDefBuilder.forDefinitionAt(xmlProperties,
												  		 XMLPropertyLocation.createFor(appCode,component,resLoaderDefPath));
	}
	/**
	 * Loads a {@link ResourcesLoaderDef} from a properties file
	 * @param xmlProperties an instance of the XMLProperties object
	 * @param resourcesLoaderDefLocation Path to the {@link ResourcesLoader} definition in the XML properties
	 * 						 <b>SIDE NOTE:</b>the resLoaderDefId=DefaultClassPathLoaderDef loads the default definition.
	 * @return The {@link ResourcesLoaderDef} object that contains the {@link ResourcesLoader} definition
	 */
	public static ResourcesLoaderDef forDefinitionAt(final XMLProperties xmlProperties,
												     final XMLPropertyLocation resLoaderDefLoc) {
		// Load the ResourcesLoader definition from the XMLProperties
		ResourcesLoaderDef outDef = ResourcesLoaderDefBuilder.forDefinitionAt(xmlProperties.forApp(resLoaderDefLoc.getAppCode()),
												  					   		  resLoaderDefLoc.getComponent(),resLoaderDefLoc.getXPath());
		return outDef;
	}
	/**
	 * Loads a {@link ResourcesLoaderDef} from a properties file
	 * @param xmlPropertiesForApp an instance of the {@link XMLPropertiesForApp} object that encapsulates the properties for an app
	 * @param component the app component
	 * @param resLoaderDefPath Path to the {@link ResourcesLoader} definition in the XML properties
	 * 						 <b>SIDE NOTE:</b>the resLoaderDefId=DefaultClassPathLoaderDef loads the default definition.
	 * @return The {@link ResourcesLoaderDef} object that contains the {@link ResourcesLoader} definition
	 */
	public static ResourcesLoaderDef forDefinitionAt(final XMLPropertiesForApp xmlPropertiesForApp,
												     final AppComponent component,final Path resLoaderDefPath) {
		if (xmlPropertiesForApp == null) throw new IllegalArgumentException("XMLProperties for app cannot be null!");
		
		ResourcesLoaderDef resLoaderDef = null;
		if (resLoaderDefPath == null || resLoaderDefPath.asString().equals(ResourcesLoaderDef.DEFAULT.getId())) {
			// Return the default resources loader
			resLoaderDef = ResourcesLoaderDef.DEFAULT;		// Default classPathLoader
		} else {
			// Load the ResourcesLoader definition from the XMLProperties
			resLoaderDef = xmlPropertiesForApp.of(component)
										   	  .getResourcesLoaderDef(resLoaderDefPath);
		} 
		if (resLoaderDef == null) {
			log.warn("The resourcesLoaderDef for xml property {}/{}/{} could not be loaded; the DEFAULT value is returned instead!",
			 	     xmlPropertiesForApp.getAppCode(),component,(resLoaderDefPath != null ? resLoaderDefPath.asRelativeString() : "resLoaderDefPath NULL"));
			resLoaderDef = ResourcesLoaderDef.DEFAULT;
		}
		resLoaderDef.setId(XMLPropertyLocation.composeId(xmlPropertiesForApp.getAppCode(),
														 component,
														 resLoaderDefPath));
		return resLoaderDef;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the default classPath loader with no reload definition 
	 */
	public static ResourcesLoaderDef getDefault() {
		return ResourcesLoaderDef.DEFAULT;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Manually building
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Manually creates a {@link ResourcesLoaderDef}
	 * @param id
	 * @return
	 */
	public static ResourcesLoaderDefManualBuilderLoaderStep create(final String id) {
		ResourcesLoaderDef resLoaderDef = new ResourcesLoaderDef();
		resLoaderDef.setId(id);
		return new ResourcesLoaderDefBuilder()
						.new ResourcesLoaderDefManualBuilderLoaderStep(resLoaderDef);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderLoaderStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDefManualBuilderReloadingStep usingClassPathResourcesLoader() {
			_resLoaderDef.setLoader(ResourcesLoaderType.CLASSPATH);
			return new ResourcesLoaderDefManualBuilderReloadingStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderReloadingStep usingFileSystemResourcesLoader() {
			_resLoaderDef.setLoader(ResourcesLoaderType.FILESYSTEM);
			return new ResourcesLoaderDefManualBuilderReloadingStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesConxStep usingDataBaseResourcesLoader() {
			_resLoaderDef.setLoader(ResourcesLoaderType.BBDD);
			return new ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesConxStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderURLLoaderPropertiesProxyStep usingURLResourcesLoader() {
			_resLoaderDef.setLoader(ResourcesLoaderType.URL);
			return new ResourcesLoaderDefManualBuilderURLLoaderPropertiesProxyStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderPropertiesStep usingLoader(final ResourcesLoaderType loader) {
			_resLoaderDef.setLoader(loader);
			return new ResourcesLoaderDefManualBuilderPropertiesStep(_resLoaderDef);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesConxStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesLoadSqlStep conectingUsingDataSource(final String dataSourceName) {
			_resLoaderDef.setLoaderProps(new HashMap<String,String>(4));
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromBBDD.CLASS,"DataSource");
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromBBDD.URI,dataSourceName);
			return new ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesLoadSqlStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesLoadSqlStep conectingUsing(final String driverClass,
																							 final String conxUri,
																							 final UserCode user,final Password password) {
			_resLoaderDef.setLoaderProps(new HashMap<String,String>(6));
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromBBDD.CLASS,driverClass);
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromBBDD.URI,conxUri);
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromBBDD.USER,user.asString());
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromBBDD.PASSWORD,password.asString());
			return new ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesLoadSqlStep(_resLoaderDef);
		}		
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesLoadSqlStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesUpdateTSSqlStep sqlToLoadResource(final String sql) {
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromBBDD.LOAD_SQL,sql);
			return new ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesUpdateTSSqlStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesUpdateTSSqlStep defaultSqlToLoadResource() {
			return this.sqlToLoadResource(ResourcesLoaderFromBBDD.DEFAULT_LOAD_SQL);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderBBDDLoaderPropertiesUpdateTSSqlStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDefManualBuilderReloadingStep sqlToUpdateLastReloadTimeStamp(final String sql) {
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromBBDD.UPDATE_TS_SQL,sql);
			return new ResourcesLoaderDefManualBuilderReloadingStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderReloadingStep defaultSqlToUpdateLastReloadTimeStamp() {
			return this.sqlToUpdateLastReloadTimeStamp(ResourcesLoaderFromBBDD.DEFAULT_UPDATE_TS_SQL);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderURLLoaderPropertiesProxyStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDefManualBuilderReloadingStep usingProxy(final String proxyHost,final int proxyPort,
																	   final UserCode proxyUser,final Password proxyPassword) {
			_resLoaderDef.setLoaderProps(new HashMap<String,String>(4));
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromURL.PROXY_HOST_PROP,proxyHost);
			_resLoaderDef.getLoaderProps().put(ResourcesLoaderFromURL.PROXY_PORT_PROP,Integer.toString(proxyPort));
			if (proxyUser != null) _resLoaderDef.getLoaderProps().put(ResourcesLoaderFromURL.PROXY_USER_PROP,proxyUser.asString());
			if (proxyPassword != null) _resLoaderDef.getLoaderProps().put(ResourcesLoaderFromURL.PROXY_PASSWORD_PROP,proxyPassword.asString());
			
			return new ResourcesLoaderDefManualBuilderReloadingStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderReloadingStep notUsingProxy() {
			return new ResourcesLoaderDefManualBuilderReloadingStep(_resLoaderDef);
		}
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderPropertiesStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDefManualBuilderReloadingStep withoutProperties() {
			return new ResourcesLoaderDefManualBuilderReloadingStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderReloadingStep withProperties(final Map<String,String> props) {
			_resLoaderDef.setLoaderProps(props);
			return new ResourcesLoaderDefManualBuilderReloadingStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderReloadingStep withProperties(final MapEntry<String,String>... entries) {
			if (CollectionUtils.hasData(entries)) {
				if (_resLoaderDef.getLoaderProps() == null) _resLoaderDef.setLoaderProps(new HashMap<String,String>(entries.length));
				for (MapEntry<String,String> entry : entries) {
					_resLoaderDef.getLoaderProps()
								 .put(entry.getKey(),entry.getValue());
				}
			}
			return new ResourcesLoaderDefManualBuilderReloadingStep(_resLoaderDef);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderReloadingStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDefManualBuilderCharsetStep reloadingAsDefinedAt(final ResourcesReloadControlDef reloadControlDef) {
			_resLoaderDef.setReloadControlDef(reloadControlDef);
			return new ResourcesLoaderDefManualBuilderCharsetStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderCharsetStep notReloading() {
			_resLoaderDef.setReloadControlDef(ResourcesReloadControlDef.NO_RELOAD);
			return new ResourcesLoaderDefManualBuilderCharsetStep(_resLoaderDef);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderCharsetStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDefManualBuilderBuildStep charset(final Charset charset) {
			_resLoaderDef.setCharsetName(charset.name());
			return new ResourcesLoaderDefManualBuilderBuildStep(_resLoaderDef);
		}
		public ResourcesLoaderDefManualBuilderBuildStep defaultCharset() {
			_resLoaderDef.setCharsetName(Charset.defaultCharset().name());
			return new ResourcesLoaderDefManualBuilderBuildStep(_resLoaderDef);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ResourcesLoaderDefManualBuilderBuildStep {
		private final ResourcesLoaderDef _resLoaderDef;
		
		public ResourcesLoaderDef build() {
			return _resLoaderDef;
		}
	}
}
