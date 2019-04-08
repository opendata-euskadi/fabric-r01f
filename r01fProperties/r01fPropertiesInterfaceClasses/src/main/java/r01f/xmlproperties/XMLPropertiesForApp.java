package r01f.xmlproperties;
/**

 * @author  Alex Lara
 * @version
 */

import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;

/**
 * Manages properties for an app code<br/>
 * Provides access to each app's component's properties
 */
public interface XMLPropertiesForApp {
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the appcode
	 */
	public AppCode getAppCode();
	/**
	 * @return the environment
	 */
	public Environment getSystemSetEnvironment();
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE RELOADING AND CACHE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the cache usage info
     * @return 
     */
    public String cacheStatsDebugInfo();
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Fluent api that provides access to properties
     * @param component app component
     * @return 
     */
    public ComponentProperties of(final AppComponent component);
    /**
     * Fluent api that provides access to properties
     * @param component app component
     * @return 
     */
    public ComponentProperties of(final String component);
    /**
     * Encapsulates the component properties
     * @param component the component
     * @return the {@link XMLPropertiesForAppComponent}
     */
    public XMLPropertiesForAppComponent forComponent(final AppComponent component);
    /**
     * Encapsulates the component properties
     * @param component the component
     * @return the {@link XMLPropertiesForAppComponent}
     */
    public XMLPropertiesForAppComponent forComponent(final String component);
}