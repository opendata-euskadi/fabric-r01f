package r01f.model.facets;

/**
 * Interface to be implemented by model objects that can be
 * converted to a CSV line 
 */
public interface CanBeConvertedToCSV {
	/**
	 * @return a {@link String} array that contains all CSV line's fields
	 */
	public String[] toCSVFields();
}
