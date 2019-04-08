package r01f.xmlproperties;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.xmlproperties.XMLPropertiesForAppCache.XMLPropertiesForAppCacheFactory;

/**
 * see {@link XMLProperties}
 */
@Slf4j
     class XMLPropertiesImpl 
implements XMLProperties {
///////////////////////////////////////////////////////////////////////////////
// 	CONSTANTS
///////////////////////////////////////////////////////////////////////////////
	private static final int DEF_APP_NUM = 15;
	private static final float APP_CACHE_MAP_LOAD_FACTOR = 0.5F;
	private static final int DEF_APP_NUM_PROPS = 10;
///////////////////////////////////////////////////////////////////////////////
//	FIELDS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Cache of {@link XMLPropertiesForApp} for every managed app
	 * the {@link XMLPropertiesForApp} also have another cache (an instance of {@link XMLPropertiesCache})
	 */
	private final Map<AppCode,XMLPropertiesForApp> _propsForAppCache;
	/**
	 * App properties factory
	 */
	private final XMLPropertiesForAppCacheFactory _propsForAppCacheFactory;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	@Inject
	public XMLPropertiesImpl(final XMLPropertiesForAppCacheFactory cacheFactory) {
		log.trace("XMLProperties BootStraping");
		_propsForAppCacheFactory = cacheFactory;
		_propsForAppCache = new HashMap<AppCode,XMLPropertiesForApp>(DEF_APP_NUM,
																	 APP_CACHE_MAP_LOAD_FACTOR);
	}
///////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////
	public XMLPropertiesForApp forApp(final AppCode appCode,
									  final int componentsNumberEstimation) {
		XMLPropertiesForApp propsForApp = _propsForAppCache != null ? _propsForAppCache.get(appCode)
																	: null;
		if (propsForApp == null) {
			log.trace("The properties for application {} are not present in the cache: they must be loaded NOW",appCode);
			XMLPropertiesForAppCache propsForAppCache = _propsForAppCacheFactory.createFor(appCode,
																						   componentsNumberEstimation);
			
			propsForApp = new XMLPropertiesForAppImpl(propsForAppCache);		
			_propsForAppCache.put(appCode,propsForApp);
		}
		return propsForApp;		
	}
	public XMLPropertiesForApp forApp(final AppCode appCode) {
		return this.forApp(appCode,DEF_APP_NUM_PROPS); 	// Component number estimation for the app
	}
	/**
	 * Gets an app properties manager {@link XMLPropertiesForApp} 
	 * @param appCode app code
	 * @return the manager that provides access to components and from there to properties
	 */
	public XMLPropertiesForApp forApp(final String appCode) {
		return this.forApp(AppCode.forId(appCode));
	}
	/**
	 * Gets an app properties manager (a {@link XMLPropertiesForApp} instance) and from it
	 * an manager of component properties (a {@link XMLPropertiesForAppComponent}) that provides
	 * access to properties
	 * @param appCode
	 * @param component
	 * @return
	 */
	public XMLPropertiesForAppComponent forAppComponent(final AppCode appCode,final AppComponent component) {
		XMLPropertiesForApp propsForApp = this.forApp(appCode);
		return propsForApp.forComponent(component);
	}
	/**
	 * Gets an app properties manager (a {@link XMLPropertiesForApp} instance) and from it
	 * an manager of component properties (a {@link XMLPropertiesForAppComponent}) that provides
	 * access to properties
	 * @param appCode
	 * @param component
	 * @return
	 */
	public XMLPropertiesForAppComponent forAppComponent(final String appCode,final String component) {
		return this.forAppComponent(AppCode.forId(appCode),AppComponent.forId(component));
	}
}
