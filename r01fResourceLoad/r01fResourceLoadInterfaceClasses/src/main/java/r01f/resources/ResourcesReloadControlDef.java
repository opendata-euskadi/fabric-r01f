package r01f.resources;

import java.util.Map;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.TimeLapse;

/**
 * Resource reloading definition
 * Usually comes from a {@link XMLProperties} file:<br>
 * <pre class="brush:xml">
 *		<resourcesLoader type='CLASSPATH'>
 *			<!-- PERIODIC, BBDD, CONTENT_SERVER_FILE_LAST_MODIF_TIMESTAMP, FILE_LAST_MODIF_TIMESTAMP, VOID -->
 *			<reloadControl impl='PERIODIC' enabled='true' checkInterval='2s'>
 *				<props>
 *					<period>2s</period>
 *				</props>
 *			</reloadControl>
 *		</resourcesLoader>
 * </pre>
 * If the {@link ResourcesReloadControlDef} must be created manually, use:
 * <ul>
 * 		<li>ResourcesReloadControlBBDDTimeStampBasedDef</li>
 * 		<li>ResourcesReloadControlContentServerFileLastModifTimeStampBasedDef</li>
 * 		<li>ResourcesReloadControlPeriodicDef</li>
 * 		<li>ResourcesReloadControlFileLastModifTimeStampBasedDef</li>
 * </ul>
 * 
 * or a builder...
 * 
 * If a {@link ResourcesReloadControlDef} must be created a builder method should be used:
 * <pre class='brush:java'>
 *		ResourcesReloadControlDef periodicReloadDef = ResourcesReloadControlDef.periodicReloading("5s")
 *																			   .enabled();
 *		ResourcesReloadControlDef fileSystemLastModifReloadDef = ResourcesReloadControlDef.fileSystemLastModifTimeStampReloading(ResourcesLoaderType.CLASSPATH,
 *																																 "/datos/r01f/myFile.chk")
 *																			   			  .enabled()
 * </pre>
 */
@MarshallType(as="reloadControl")
@Accessors(prefix="_")
@NoArgsConstructor
public class ResourcesReloadControlDef 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public enum ResourcesReloadPolicy {
		VOID,
		NO_RELOAD,
		BBDD,
		PERIODIC,
		FILE_LAST_MODIF_TIMESTAMP;
	}
///////////////////////////////////////////////////////////////////////////////
// 	VALOR POR DEFECTO (sin recarga)
///////////////////////////////////////////////////////////////////////////////
	public static ResourcesReloadControlDef DEFAULT = new ResourcesReloadControlDef(ResourcesReloadPolicy.NO_RELOAD) {
															@Override
															public boolean isEnabled() {
																return false;
															}
												   	  };
	public static ResourcesReloadControlDef NO_RELOAD = DEFAULT;
///////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="impl",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ResourcesReloadPolicy _impl;
	
	@MarshallField(as="checkInterval",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private TimeLapse _checkInterval = TimeLapse.createFor("3000s");	// Time between two reload need checks
	
	@MarshallField(as="enabled",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _enabled = true;
	
	@MarshallField(as="props")
	@Getter @Setter private Map<String,String> _controlProps;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ResourcesReloadControlDef(final ResourcesReloadPolicy policy) {
		_impl = policy;
	}
///////////////////////////////////////////////////////////////////////////////
// 	METHODS
///////////////////////////////////////////////////////////////////////////////
	public long getCheckIntervalMilis() {
		return _checkInterval != null ? _checkInterval.asMilis()
									  : ResourceBundle.Control.TTL_NO_EXPIRATION_CONTROL;
	}
	@Override
	public CharSequence debugInfo() {
		StringBuilder sw = new StringBuilder(100);
		sw.append(String.format("\r\n\t\t\t-      enabled: %s",Boolean.toString(_enabled)))
		  .append(String.format("\r\n\t\t\t-         impl: %s",_impl != null ? _impl.name() : null))
		  .append(String.format("\r\n\t\t\t-checkInterval: %s",_checkInterval));
		if (_controlProps != null) {
			sw.append(String.format("\r\n\t\t\t-        props: (%s)",_controlProps.size()));
			for (Map.Entry<String,String> prop : _controlProps.entrySet()) {
				sw.append(String.format("\r\n\t\t\t\t-%s:%s",prop.getKey(),prop.getValue()));
			}
		}
		return sw;
	}
}
