package r01f.resources;


/**
 * Interface for the resource re-load controllers
 */
public interface ResourcesReloadControl {
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns <code>true</code> if the resource should be reloaded
	 * @param component the component
	 * @return <code>false</code> if the resource does NOT have to be reloaded
	 */
	public boolean needsReload(String component);
}
