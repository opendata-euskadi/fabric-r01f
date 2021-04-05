package r01f.model.facets.view;

import r01f.facets.Facet;



/**
 * Models an object that's has a selectable behavior / facet (it can be selected / de-selected)
 * It's normally used as the model part of some TreeView
 */
public interface IsSelectable
	     extends Facet {
	/**
	 * @return true if it's selected either because it's a primary selection or because it's a secondary selection
	 */
	public boolean isSelected();
	/**
	 * Sets the selection status
	 * @param status
	 */
	public void setSelectionTo(final boolean status);
	/**
	 * Selects this item.
	 */
	public void setSelected();
	/**
	 * DeSelect this item
	 */
	public void setDeSelected();
	/**
	 * Toggles the selection status
	 */
	public void toggleSelected();
}
