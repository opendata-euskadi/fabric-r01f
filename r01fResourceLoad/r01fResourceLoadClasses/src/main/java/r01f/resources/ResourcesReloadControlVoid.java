package r01f.resources;

import java.util.Map;




/**
 * void reload policy: it does nothing, does NOT reload
 */
public class ResourcesReloadControlVoid 
     extends ResourcesReloadControlBase<ResourcesReloadControlVoid> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 */
	public ResourcesReloadControlVoid() {
		// nothing
	}
	/**
	 * Constructor
	 * @param checkIntervalMillis
	 * @param props
	 */
	public ResourcesReloadControlVoid(final ResourcesReloadControlDef resCtrlDef) {
		super(resCtrlDef);
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		boolean outOK = true;
		return outOK;		// no properties is needed
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ ResourcesReloadControl
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean needsReload(String component) {
		return false;
	}

}
