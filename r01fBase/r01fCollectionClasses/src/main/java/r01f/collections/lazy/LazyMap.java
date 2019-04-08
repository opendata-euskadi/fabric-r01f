package r01f.collections.lazy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;

import r01f.collections.dirtytrack.interfaces.ChangesTrackableLazyMap;


/**
 * Lazy loaded {@link Map} interface
 * @see {@link LazyCachedMap} and {@link LazyNotCachedMap}
 * 
 * This {@link Map} works similarly as a normal {@link Map} does:
 * <ul>
 * 		<li>When a get(...) op is invoked the Entry is loaded lazyily</li> 
 * 		<li>When a put(...) op is invoked the Entry is stored into the Map as usual</li>
 * 		<li>When a remove(...) method is invoked, the Entry is removed from the Map</li>
 * </ul>
 * In addition to the normal {@link Map} interface methods, some Entry tracking methods are available:
 * <pre class='brush:java'>
 * 			Map<String,String> newEntries = map.newEntries();
 * 			Map<String,String> removedEntries = map.removedEntries();
 * 			Map<String,String> notNewOrRemovedEntries = map.notNewOrRemovedEntries();
 * </pre>
 * 
 * Sample usage:
 * <pre class='brush:java'>
 *		KeyIntrospector<String,String> keyIntrospector = new KeyIntrospector<String, String>() {
 *																	@Override
 *																	public String of(String obj) {
 *																		return obj;
 *																	}
 *														 };
 *		KeySetSupplier<String> keySetSupplier = new KeySetSupplier<String>() {
 *															@Override
 *															public Set<String> loadKeys() {
 *																System.out.println("> loading keys");
 *																return new TreeSet<String>(Arrays.asList("uno","dos","tres"));
 *															}
 *												};
 *		ValuesSupplier<String,String> valuesSupplier = new ValuesSupplier<String,String>() {
 *																@Override
 *																public String loadValue(String key) {
 *																	System.out.println("> loading value for key " + key);
 *																	return new String(key);
 *																}
 *													   };
 *		Map<String,String> noCache = LazyCollectionsBuilders.notCachedMapBuilder()
 *																	.loadKeySetWith(keySetSupplier)		// also LazyNotCachedMap().builder() is available
 *																	.loadValuesWith(valuesSupplier)
 *				 													.introspectKeyFromValueWith(keyIntrospector)
 *																	.build();
 * </pre>
 * 
 */
public interface LazyMap<K,V> 
         extends ChangesTrackableLazyMap<K,V>,
         		 Map<K,V> {
	/**
	 * Returns if a key is loaded
	 * @param key
	 * @return true if the key is loaded, false otherwise
	 */
	public boolean isLoaded(final K key);
	/**
	 * Checks if all Map's keys are loaded
	 * @return 
	 */
	public boolean isFullyLoaded();
	/**
	 * Remove a Map's entry BUT without loading the entry
	 * BEWARE that this method is mandatory because the Map's remove() method requires the removed object to be returned
	 * ... so in a LazyMap implementation this will require the entry loading if it wasn't loaded
	 * @param key the key to be removed
	 * @return true if the object is removed
	 */
	public boolean removeLazily(final K key);
	/**
	 * Returns the LOADED entries as a {@link Map} (other entries might exists BUT were not requested and NOT loaded)
	 * @return 
	 */
	public Map<K,V> loadedEntriesAsMap();
	/**
	 * Returns the LOADED entries as a {@link Map} (other entries might exists BUT were not requested and NOT loaded)
	 * @return 
	 */
	public Set<Map.Entry<K,V>> loadedEntrySet();
	/**
	 * Returns the LOADED keys as a {@link Map} (other entries might exists BUT were not requested and NOT loaded)
	 * @return 
	 */
	public Set<K> loadedKeySet();
	/**
	 * Returns the LOADED values as a {@link Map} (other entries might exists BUT were not requested and NOT loaded)
	 * @return 
	 */
	public Collection<V> loadedValues();
	/**
	 * Conciliates the keys from a provided KeySet with the Map's existing values
	 * The use case is:
	 * 		1.- The Map is loaded at client-side
	 * 		2.- Additions, removals, etc are done at the Map at client-side
	 * 		3.- A KeySet is get using the supplier (ie: from the server)
	 * 		In this moment, there're not-persisted client-side changes so
	 * 			a.- At client-side a key might be removed and the supplier still return it (because client-side changes have NOT be persisted)
	 * 			b.- A filter-compliant entry exists at client-side
	 * This method conciliates the supplier-returned KeySet (ie server data) with the client data
	 * 			a.- Removing from the provider-returned keySet every key removed on the client
	 * 			b.- Adding to the provided-returned keySet every key added on the client that verifies the filter criteria
	 * @param suppliedKeySet the provider-returned keySet 
	 * @param filter the filter applied at the provider
	 * @return the conciliated keySet
	 */
	public Set<K> conciliateKeySetOnValues(Set<K> suppliedKeySet,
								   		   Predicate<V> filter);
	/**
	 * Conciliates the keys from a provided KeySet with the Map's existing values
	 * The use case is:
	 * 		1.- The Map is loaded at client-side
	 * 		2.- Additions, removals, etc are done at the Map at client-side
	 * 		3.- A KeySet is get using the supplier (ie: from the server)
	 * 		In this moment, there're not-persisted client-side changes so
	 * 			a.- At client-side a key might be removed and the supplier still return it (because client-side changes have NOT be persisted)
	 * 			b.- A filter-compliant entry exists at client-side
	 * This method conciliates the supplier-returned KeySet (ie server data) with the client data
	 * 			a.- Removing from the provider-returned keySet every key removed on the client
	 * 			b.- Adding to the provided-returned keySet every key added on the client that verifies the filter criteria
	 * @param suppliedKeySet the provider-returned keySet 
	 * @param filter the filter applied at the provider
	 * @return the conciliated keySet
	 */
	public Set<K> conciliateKeySetOnKeys(Set<K> suppliedKeySet,
								   		 Predicate<K> filter);
	/**
	 * Returns a VIEW of the Map that contains ONLY {@link Entry} with a key within the provided ones
	 * @param keys the keys to filter
	 * @param onlyLoaded use only loaded entries (do NOT load server-side data)
	 * @return a Map ViewO
	 */
	public Map<K,V> filterEntriesWithKeys(final Set<K> key,
										  final boolean onlyLoaded);
}
