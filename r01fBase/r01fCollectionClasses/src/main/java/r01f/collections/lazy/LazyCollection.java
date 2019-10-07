package r01f.collections.lazy;

import java.util.Collection;


/**
 * LazilyLoaded {@link Collection} marker interface
 * see {@link LazyList} 
 */
public interface LazyCollection<V> 
         extends Collection<V> {
	/**
	 * @return the loaded values
	 */
	public Collection<V> loadedValues();
}
