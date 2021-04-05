package r01f.model.facets.view;

public interface IsPrimaryAndSecondarySelectable 
		 extends IsSelectable {
	/**
	 * @return true if the data is selected
	 */
	public boolean isPrimarySelected();
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
	 * @return true if the data is selected BUT because of a secondary selection
	 */
	public boolean isSecondarySelected();
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
