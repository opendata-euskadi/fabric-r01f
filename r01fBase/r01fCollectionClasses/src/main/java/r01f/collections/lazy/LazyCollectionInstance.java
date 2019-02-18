package r01f.collections.lazy;

import java.util.Collection;

import r01f.collections.lazy.LazyCollectionsInterfaces.CollectionValuesSupplier;

public class LazyCollectionInstance<V>	
	 extends LazyCollectionBase<V> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public LazyCollectionInstance(final Collection<V> backEnd,
							      final CollectionValuesSupplier<V> valuesSupplier) {
		super(backEnd,valuesSupplier);
	}
}
