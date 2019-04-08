package r01f.xmlproperties;

/**
 * {@link XMLPropertiesForAppComponent} load listener
 */
interface XMLPropertiesComponentLoadedListener {
	/**
	 * a new component has been loaded
	 * @param def component definition
	 */
	public void newComponentLoaded(XMLPropertiesComponentDef def);
}
