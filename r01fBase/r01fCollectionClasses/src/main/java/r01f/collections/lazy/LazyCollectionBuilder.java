package r01f.collections.lazy;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.collections.lazy.LazyCollectionsInterfaces.CollectionValuesSupplier;
import r01f.patterns.IsBuilder;


/**
 * lazily-loaded collection builder base
 * @param <V> value
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class LazyCollectionBuilder 
           implements IsBuilder {
	/**
	 * Sets the initial {@link Map} entries
	 * @param currentEntries the initial entries
	 */
	public static <V> LazyCollectionBuilderValuesLoaderStep<V> withInitialEntries(final Collection<V> currentEntries) {
		return new LazyCollectionBuilder() { /* ignore */ }
						.new LazyCollectionBuilderValuesLoaderStep<V>(currentEntries);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LazyCollectionBuilderValuesLoaderStep<V> {
		private final Collection<V> _initialEntries;
		/**
		 * Collection values supplier
		 * @param valuesSupplier
		 */
		public LazyCollectionBuilderBuildStep<V> loadValuesWith(final CollectionValuesSupplier<V> valuesSupplier) {
			return new LazyCollectionBuilderBuildStep<V>(_initialEntries,
														 valuesSupplier);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LazyCollectionBuilderBuildStep<V> {
		private final Collection<V> _initialEntries;
		private final CollectionValuesSupplier<? extends V> _valuesSupplier;
		/**
		 * Builds the lazy loaded collection
		 * @return 
		 */
		@SuppressWarnings("unchecked")
		public LazyCollection<V> buildBackedBy(final Collection<V> backEndCol) {
			Preconditions.checkArgument(backEndCol != null,"The backend Collection MUST NOT be null");
			if (_initialEntries != null && _initialEntries.size() > 0) backEndCol.addAll(_initialEntries);	// put all the initial entries into the lazy collection
			return new LazyCollectionInstance<V>(backEndCol,
											 	 (CollectionValuesSupplier<V>)_valuesSupplier);
		}
	}
}
