package r01f.resources;

import java.util.Map;
import java.util.regex.Pattern;

import r01f.resources.ResourcesLoaderDef.ResourcesLoaderType;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;


/**
 * Reload policy implemented by a file last modify timestamp
 * (<i>usually the file is a properties file but it does NOT have to</i>).<br>
 * 
* The params (the {@link Map} provided to the constructor) needs:<br>
 * <ul>
 * <li>The name of the file and the loader used to load it (classpath o file).<br>
 * 	   ie:<br>
 * 			classpath:/config/appCode/appCode.comp.xml</br>
 * 			file:d:/appCode.comp.xml</li>
 * <li>The time interval by which the file modify timestamp has to be checked
 * 	   (when reaching the check interval the file modify timestamp is checked and if it's greater than the previous one the
 *      file is reloaded)</li>
 * </ul>
 */
public class ResourcesReloadControlFileLastModifTimeStampBased
     extends ResourcesReloadControlBase<ResourcesReloadControlFileLastModifTimeStampBased> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	static final String FILETOCHECK_PROP_KEY = "fileToCheck";				// file to be checked
	static final String FILETOCHECKLOADERTYPE_PROP_KEY = "loaderType";		// File loader (classpath/file)
	
	static final Pattern FILEPATH_PATTERN = Pattern.compile("(?:(classpath|file):)?(.+)");
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Path to the file whose last modify timestamp is checked
	 */
	@SuppressWarnings("unused")
	private Path _fileToCheckPath;
	/**
	 * file loader (classpath/file, etc)
	 */
	@SuppressWarnings("unused")
	private ResourcesLoaderType _resourcesLoaderType;
	/**
	 * TimeStamp for the last time the file modify timestamp was checked
	 * (if this timestamp is before the file date, a reload has to be done)
	 */
	@SuppressWarnings("unused")
	private long _lastFileCheckTimeStamp = -1;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ResourcesReloadControlFileLastModifTimeStampBased() {
		// nothing
	}
	public ResourcesReloadControlFileLastModifTimeStampBased(final ResourcesReloadControlDef resCtrlDef) {
		super(resCtrlDef);
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		boolean outOK = true;
		if (CollectionUtils.isNullOrEmpty(props)) {
			outOK = false;
		} else {
			outOK = CollectionUtils.of(props)
								   .containsAllTheseKeys(FILETOCHECK_PROP_KEY,
										   				 FILETOCHECKLOADERTYPE_PROP_KEY);	
		}
		return outOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ ResourcesReloadControl
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean needsReload(final String component) {
		return false;
	}

}
