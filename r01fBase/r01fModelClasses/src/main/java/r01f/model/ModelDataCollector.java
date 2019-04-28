package r01f.model;

import r01f.patterns.IsBuilder;

/**
 * A type to be used to collect data for a model object
 * It's usually used at {@link IsBuilder} types 
 */
public interface ModelDataCollector<T> {
	/**
	 * Collects the data
	 * @return
	 */
	public T collect();
}
