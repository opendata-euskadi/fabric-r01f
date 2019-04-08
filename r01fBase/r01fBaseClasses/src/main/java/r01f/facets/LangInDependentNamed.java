package r01f.facets;

/**
 * Interface for objects with lang-independent name
 */
public interface LangInDependentNamed {
/////////////////////////////////////////////////////////////////////////////////////////
//  HaLangInDependentNamedFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasLangInDependentNamedFacet 
					extends HasName {
		public LangInDependentNamed asLangInDependentNamed();
		
		public String getName();
		public void setName(final String name);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets a name 
	 */
	public void setName(final String name);
	/**
	 * Gets the name
	 * @return
	 */
	public String getName();
}
