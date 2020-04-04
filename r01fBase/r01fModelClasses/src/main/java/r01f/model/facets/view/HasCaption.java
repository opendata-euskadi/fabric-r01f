package r01f.model.facets.view;

import r01f.facets.Facet;

/**
 * Models an object that has a caption
 */
public interface HasCaption 
		 extends Facet {
	/**
	 * @return the caption
	 */
	public String getCaption();
}
