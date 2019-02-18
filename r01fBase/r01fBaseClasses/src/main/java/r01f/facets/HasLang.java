package r01f.facets;

import r01f.locale.Language;

/**
 * Interface for language dependent model objects
 */
public interface HasLang 
	     extends Facet {
	/**
	 * Gets the language
	 * @return
	 */
	public Language getLang();
	/**
	 * Sets the language
	 * @param lang
	 */
	public void setLang(final Language lang);
}
