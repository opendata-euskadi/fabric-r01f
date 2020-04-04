package r01f.model.facets.view;

import r01f.facets.Facet;



/**
 * Models an object that's has a filtrable behavior / facet (it can be displayed / hidden)
 * It's normally used as the model part of some TreeView
 */
public interface IsFiltrable
	     extends Facet {
	/**
	 * @return true if the object is filtered
	 */
	public boolean isFiltered();
	/**
	 * Sets the object as filtered
	 */
	public void filter();
	/**
	 * Sets the object as unfiltered
	 */
	public void unFilter();
	/**
	 * Toggles the filtering status
	 */
	public void toggleFilter();
	/**
	 * Sets the filtering status
	 * @param filtered
	 */
	public void setFiltered(final boolean filtered);
	/**
	 * @return true if the object was filtered BUT later it was set to un-filtered status
	 */
	public boolean wasFiltered();
	/**
	 * Set whether it was previously filtered or not
	 * @param wasFiltered
	 */
	public void setWasFiltered(final boolean wasFiltered);
	/**
	 * @return true if it was filtered and now it's not or vice-versa
	 */
	public boolean hasChangedStatus();
}
