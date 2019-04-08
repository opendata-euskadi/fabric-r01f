package r01f.facets;

import r01f.locale.Language;

/**
 * Interface for language dependent model objects
 */
public interface HasLanguage 
	     extends Facet {
	/**
	 * Gets the language
	 * @return
	 */
	public Language getLanguage();
	/**
	 * Sets the language
	 * @param lang
	 */
	public void setLanguage(final Language lang);
}
