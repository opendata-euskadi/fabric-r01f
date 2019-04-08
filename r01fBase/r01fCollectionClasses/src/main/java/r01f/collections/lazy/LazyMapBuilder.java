package r01f.collections.lazy;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.collections.KeyIntrospector;
import r01f.collections.lazy.LazyCollectionsInterfaces.MapKeySetSupplier;
import r01f.collections.lazy.LazyCollectionsInterfaces.MapKeysSupplier;
import r01f.collections.lazy.LazyCollectionsInterfaces.MapValuesSupplier;
import r01f.patterns.IsBuilder;

/**
 * {@link LazyMap} builder
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class LazyMapBuilder
		   implements IsBuilder {
	/**
	 * Sets the initial {@link Map} entries
	 * @param currentEntries the initial entries
	 */
	public static <K,V,C> LazyMapBuilderLoadedStep<K,V,C> withInitialEntries(final Map<K,V> currentEntries) {
		return new LazyMapBuilder() { /* ignore */ }
						.new LazyMapBuilderLoadedStep<K,V,C>(currentEntries);
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LazyMapBuilderLoadedStep<K,V,C> {
		private final Map<K,V> _initialEntries;
		
		/**
		 * Sets that the {@link Map} loading is completely done at creation time
		 */
		public LazyMapBuilderSuppliedKeySetBuildStep<K,V,C> fullLoadedOnCreation() {
			return new LazyMapBuilderSuppliedKeySetBuildStep<K,V,C>(true,	// fully loaded on creation
															 		_initialEntries,
															 		null,null,
															 		null,null);
		}
		/** 
		 * Sets a key supplier that hands all the {@link Map} keys
		 * @param keySetSupplier 
		 * @param criteria filter criteria that is handed to the supplier to do it's job
		 * 			  	   it could be for example
		 * 					<ul>
		 * 						<li>an object with filter data</li>
		 * 						<li>the lazy-loaded Map container object whose oid could be used to filter Map entries</li>
		 * 					</ul> 
		 */
		public LazyMapBuilderSuppliedKeySetValueLoaderStep<K,V,C> loadKeySetWith(final MapKeySetSupplier<K,C> keySetSupplier,
													       						 final C criteria) {
			return new LazyMapBuilderSuppliedKeySetValueLoaderStep<K,V,C>(_initialEntries,
																		  keySetSupplier,criteria);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LazyMapBuilderSuppliedKeySetValueLoaderStep<K,V,C> {
		private final Map<K,V> _initialEntries;
		private final MapKeySetSupplier<K,C> _keySetSupplier;
		private final C _keySetLoadCriteria;
		
		/**
		 * Values Supplier
		 * @param valuesSupplier
		 */
		public LazyMapBuilderSuppliedKeySetKeyIntrospectorStep<K,V,C> loadValuesWith(final MapValuesSupplier<K,V> valuesSupplier) {
			return new LazyMapBuilderSuppliedKeySetKeyIntrospectorStep<K,V,C>(_initialEntries,
																	   		  _keySetSupplier,_keySetLoadCriteria,
																	   		  valuesSupplier);		
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LazyMapBuilderSuppliedKeySetKeyIntrospectorStep<K,V,C> {
		private final Map<K,V> _initialEntries;
		private final MapKeySetSupplier<K,C> _keySetSupplier;
		private final C _keySetLoadCriteria;
		private final MapValuesSupplier<K,V> _valuesSupplier;
		
		/**
		 * Sets an object that helps into extracting the key from the value object
		 * @param keyIntrospector
		 */
		public LazyMapBuilderSuppliedKeySetBuildStep<K,V,C> introspectKeyFromValueWith(final KeyIntrospector<K,V> keyIntrospector) {
			return new LazyMapBuilderSuppliedKeySetBuildStep<K,V,C>(false,			// not fully loaded on creation
																	_initialEntries,
																	_keySetSupplier,_keySetLoadCriteria,
																	_valuesSupplier,keyIntrospector);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LazyMapBuilderSuppliedKeySetBuildStep<K,V,C> {
		private final boolean _fullLoadedOnCreation;
		private final Map<K,V> _initialEntries;
		private final MapKeySetSupplier<K,C> _keySetSupplier;
		private final C _keySetLoadCriteria;
		private final MapValuesSupplier<K,V> _valuesSupplier;
		private final KeyIntrospector<K,V> _keyIntrospector;
		
		public LazyMap<K,V> buildNotCachedBackedBy(final Map<K,V> backendMap) {
			// Memoize the key supplier
			Supplier<Set<K>> theKeySetSupplier = _keySetSupplier != null ? Suppliers.memoize(new MapKeysSupplier<K,C>(_keySetSupplier,
																							   						  _keySetLoadCriteria))
																		 : null;
			// Build the LazyNotCachedMap
			LazyMap<K,V> outMap = new LazyNotCachedMap<K,V>(_fullLoadedOnCreation,
															_keyIntrospector,
															theKeySetSupplier,_valuesSupplier,
															backendMap);
			return outMap;
		}
		public LazyMap<K,V> buildCached(final int initialCapacity) {
			return this.buildCached(initialCapacity,
									5,TimeUnit.MINUTES);
		}
		public LazyMap<K,V> buildCached(final int initialCapacity,
										final long expirationPeriod,final TimeUnit expirationPeriodTimeUnit) {
			// Memoize the key supplier
			Supplier<Set<K>> theKeySetSupplier = _keySetSupplier != null ? Suppliers.memoize(new MapKeysSupplier<K,C>(_keySetSupplier,
																							   						  _keySetLoadCriteria))
																		 : null;
			
			// Build the guava's cached Map
			LoadingCache<K,V> mapInstance = CacheBuilder.newBuilder()
											  		    .initialCapacity(initialCapacity)
											  		    .expireAfterWrite(expirationPeriod,expirationPeriodTimeUnit)	// cache entries expiration
											  		    .build(_valuesSupplier.asCacheLoader());
			// PUT the initial entries
			if (_initialEntries != null && _initialEntries.size() > 0) mapInstance.putAll(_initialEntries);
			
			// Build the map
			LazyMap<K,V> outMap = new LazyCachedMap<K,V>(_fullLoadedOnCreation,
													 	 _keyIntrospector,
													 	 theKeySetSupplier,
													 	 mapInstance);
			return outMap;
		}
	}
}
