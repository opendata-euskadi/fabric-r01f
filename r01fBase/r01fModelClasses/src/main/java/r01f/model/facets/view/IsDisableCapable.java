package r01f.model.facets.view;

import r01f.model.facets.ModelObjectFacet;



/**
 * Models an object that's has a filtrable behavior / facet (it can be displayed / hidden)
 * It's normally used as the model part of some TreeView
 */
public interface IsDisableCapable
	     extends ModelObjectFacet {
	/**
	 * @return true if the object is enabled
	 */
	public boolean isEnabled();
	/**
	 * @return true if the object is disabled
	 */
	public boolean isDisabled();
	/**
	 * Sets the object as enabled
	 */
	public void enable();
	/**
	 * Sets the object as disabled
	 */
	public void disable();
	/**
	 * Toggles the enabling status
	 */
	public void toggleEnable();
	/**
	 * Sets the enabling status
	 * @param enabled
	 */
	public void setEnabled(final boolean enabled);
}
