package r01f.model.facets.view;

import r01f.facets.Facet;
import r01f.locale.Language;

/**
 * Models an object that has a caption depending on the language
 */
public interface HasCaptionLanguageDependent 
		 extends Facet {
	/**
	 * @return the caption
	 */
	public String getCaption(final Language lang);
}
