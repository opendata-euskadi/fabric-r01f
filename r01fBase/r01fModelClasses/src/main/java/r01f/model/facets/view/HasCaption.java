package r01f.model.facets.view;

import r01f.model.facets.ModelObjectFacet;

/**
 * Models an object that has a caption
 */
public interface HasCaption 
		 extends ModelObjectFacet {
	/**
	 * @return the caption
	 */
	public String getCaption();
}
