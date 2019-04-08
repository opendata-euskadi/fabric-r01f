package r01f.model.facets.view;

import r01f.model.facets.ModelObjectFacet;



/**
 * Models an object that's has a selectable behavior / facet (it can be selected / de-selected)
 * It's normally used as the model part of some TreeView
 */
public interface IsSelectable
	     extends ModelObjectFacet {
	/**
	 * @return true if it's selected either because it's a primary selection or because it's a secondary selection
	 */
	public boolean isSelected();
	/**
	 * @return true if the data is selected
	 */
	public boolean isPrimarySelected();
	/**
	 * @return true if the data is selected BUT because of a secondary selection
	 */
	public boolean isSecondarySelected();
	/**
	 * Selects the label as selected as a primary pick
	 * This might select other labels as secondary selected depending on the selection policy
	 */
	public void primarySelect();
	/**
	 * Sets the data as not selected as a primary pick
	 * This might deSelect other labels as secondary selected depending on the selection policy
	 */
	public void primaryDeSelect();
	/**
	 * DeSelects the data as a secondary selection (because of a primary pick)
	 * (it's selected because the same data is selected in other place)
	 */
	public void secondaryDeSelect();
	/**
	 * DeSelects the data as a secondary selection (because of a primary de-selection)
	 * (it's selected because the same label is selected in other place)
	 */
	public void secondarySelect();
}
