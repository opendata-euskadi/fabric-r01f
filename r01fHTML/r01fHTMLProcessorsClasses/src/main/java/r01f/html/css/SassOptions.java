package r01f.html.css;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.apache.commons.lang3.Validate;

@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class SassOptions
  implements SassOptionsBuilder,
  			 Cloneable {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	final static String DEFAULT_FRAMEWORKS_LOCAL_HOME_DIR = "d:/temp";
	final static String DEFAULT_REMOTE_RESOURCES_LOCATION = null;
	final static String DEFAULT_WEB_SERVER_DIR_FOR_STYLESHEETS = "stylesheets";
	final static String DEFAULT_WEB_SERVER_DIR_FOR_SCRIPTS = "javascripts";
	final static String DEFAULT_WEB_SERVER_DIR_FOR_IMAGES = "images";
	
	public static final SassOptions DEFAULT_OPTIONS = new SassOptions(DEFAULT_FRAMEWORKS_LOCAL_HOME_DIR,
																	  DEFAULT_REMOTE_RESOURCES_LOCATION,
																	  DEFAULT_WEB_SERVER_DIR_FOR_STYLESHEETS,
																	  DEFAULT_WEB_SERVER_DIR_FOR_SCRIPTS,
																  	  DEFAULT_WEB_SERVER_DIR_FOR_IMAGES);
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private String _frameworksLocalHomeDir;
	@Getter @Setter private String _remoteResourcesLocation;
	@Getter @Setter private String _webServerDirForStylesheets;
	@Getter @Setter private String _webServerDirForScripts;
	@Getter @Setter private String _webServerDirForImages;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static SassOptionsBuilder create() {
		return new SassOptions();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a copy (clone) of this object
	 */
	public SassOptions copy() {
		SassOptions outCopy = new SassOptions();
		outCopy.setFrameworksLocalHomeDir(_frameworksLocalHomeDir);
		outCopy.setRemoteResourcesLocation(_remoteResourcesLocation);
		outCopy.setWebServerDirForStylesheets(_webServerDirForStylesheets);
		outCopy.setWebServerDirForScripts(_webServerDirForScripts);
		outCopy.setWebServerDirForImages(_webServerDirForImages);
		return outCopy;
	}
	@Override
	public SassOptions clone() {
		return this.copy();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SassOptionsBuilder findFrameworkLocalResourcesAt(final String frameworkDir) {
		Validate.notEmpty(frameworkDir);
		this.setFrameworksLocalHomeDir(frameworkDir);
		return this;
	}
	@Override
	public SassOptionsBuilder findRemoteResourcesAt(final String remoteResourcesLocation) {
		Validate.notEmpty(remoteResourcesLocation);
		this.setRemoteResourcesLocation(remoteResourcesLocation);
		return this;
	}
	@Override
	public SassOptionsBuilder webServerDirForStylesheets(final String cssDir) {
		Validate.notEmpty(cssDir);
		this.setWebServerDirForStylesheets(cssDir);
		return this;
	}
	@Override
	public SassOptionsBuilder webServerDirForScripts(final String jsDir) {
		Validate.notEmpty(jsDir);
		this.setWebServerDirForScripts(jsDir);
		return this;
	}
	@Override
	public SassOptionsBuilder webServerDirForImages(final String imgDir) {
		Validate.notEmpty(imgDir);
		this.setWebServerDirForImages(imgDir);
		return this;
	}
	@Override 
	public SassOptions build() {
		return this;
	}
}
