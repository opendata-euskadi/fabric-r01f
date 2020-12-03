package r01f.xmlproperties;

/**
 * Factora de objetos {@link XMLPropertiesForAppComponent}
 */
public interface XMLPropertiesForAppComponentFactory {
	/**
	 * Metodo factoria
	 * @param component identificador del componente
	 * @return el objeto {@link XMLPropertiesForAppComponent}
	 */
	public XMLPropertiesForAppComponent createFor(final String component);
}
