package r01f.xmlproperties;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.resources.ResourcesLoaderDef;
import r01f.types.Path;

/**
 * <b>PROPERTY LOADING:
 * ---------------------</b></br>
 * Property loading takes place in two steps:<br/>
 * <pre>
 * 		STEP 1:	Load the the component definition which is looked after as a classpath accessible file
 * 				named [appCode].[component].xml at  /components/[appCode].[component].xml<br/>
 * 		FASE 2: Inside the component definition file the property load method is set (FileSystem, ClassPath, BBDD, etc).<br/>
 * </pre>
 * <p>
 * The XMLPropertiesManager uses the info encoded at the component definition to load the properties file<br>
 *
 * Example
 * <ul>
 * <li>If the properties file is a classpath-accessible file, the component definition will be:
 * <pre class="brush:xml">
 * 	<componentDef>
 * 		<propertiesFileURI>/config/r01fb.properties.xml</propertiesFileURI>
 * 		<numberOfPropertiesEstimation>10</numberOfPropertiesEstimation>
 * 		<resourcesLoader type='CLASSPATH'>
 * 			<reloadControl impl='PERIODIC' enabled='true' checkInterval='2s'/>
 * 		</resourcesLoader>
 *	</componentDef>
 * </pre>
 * </li>
 * <li>If the properties file is a DDBB-stored file, the component definition will be:
 * <pre class="brush:java">
 * 	<componentDef>
 * 		<propertiesFileURI>SELECT ...</propertiesFileURI>
 * 		<numberOfPropertiesEstimation>10</numberOfPropertiesEstimation>
 * 		<resourcesLoader type='BBDD'>
 * 			<props>
 * 				<conx>MyConx</conx>
 * 			</props>
 * 		</resourcesLoader>
 * 	</componentDef>
 * </pre>
 * </li>
 * </ul>
 *
 */
@MarshallType(as="componentDef")
@Accessors(prefix="_")
@NoArgsConstructor
public class XMLPropertiesComponentDef 
  implements Serializable,
		     Debuggable {
	
	private static final long serialVersionUID = 646222659405032701L;
///////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AppComponent _name;
	
	@MarshallField(as="resourcesLoader")
	@Getter @Setter private ResourcesLoaderDef _loaderDef;
	
	@MarshallField(as="propertiesFileURI")
	@Getter @Setter private Path _propertiesFileURI;
	
	@MarshallField(as="numberOfPropertiesEstimation")
	@Getter @Setter private int _numberOfPropertiesEstimation = XMLPropertiesForAppCache.DEFAULT_PROPERTIES_PER_COMPONENT;
///////////////////////////////////////////////////////////////////////////////
// 	METHODS
///////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder outSb = new StringBuilder(100);
		outSb.append(String.format("\r\n           Name: %s",_name.asString()))
			 .append(String.format("\r\npropsEstimation: %s",Integer.toString(_numberOfPropertiesEstimation)))
			 .append(String.format("\r\n        fileUri: %s",_propertiesFileURI.asString()))
			 .append(String.format("\r\n         loader: %s",_loaderDef != null ? _loaderDef.debugInfo() : ""));
		return outSb.toString();
	}
}
