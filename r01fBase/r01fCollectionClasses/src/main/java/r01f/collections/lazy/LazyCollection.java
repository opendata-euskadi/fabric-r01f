package r01f.collections.lazy;

import java.util.Collection;

import r01f.collections.dirtytrack.interfaces.ChangesTrackableLazyCollection;


/**
 * LazilyLoaded {@link Collection} marker interface
 * see {@link LazyList} 
 */
public interface LazyCollection<V> 
         extends ChangesTrackableLazyCollection<V>,
         		 Collection<V> {
	/* nothing */
}
