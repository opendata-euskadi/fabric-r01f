package r01f.collections.lazy;

import java.util.Map;


/**
 * Adapter type that transforms a {@link r01f.aspects.lazyload.LazyLoadCapable} annotated {@link Map}
 * to a {@link LazyMap}
 * <pre>
 * BEWARE!:	The {@link Map} to be adapted MUST be annotated with {@link r01f.aspects.lazyload.LazyLoadCapable}
 * 				so the LazyLoadAspect aspect is applied: AspectJ weaver weaves the object
 * </pre>
 * Usually is used as:
 * <pre class='brush:java>
 * 		@LazyLoadCapable(supplierFactory=MyLazyMapSupplierFactory.class)
 *   	Map<MyObjOID,MyObj> myMap;
 * 		
 * 		LazyMap<MyObjOID,MyObj> myMapAdapted = LazyMapAdapter.adapt(myMap);
 * 		myMapAdapted.newEntries();
 * </pre>
 */
public class LazyMapAdapter {
	/**
	 * Adapts a {@link Map} to a {@link LazyMap}
	 * @param theMap 
	 * @return a {@link LazyMap} interface view of the {@link Map}
	 */
	public static <K,V> LazyMap<K,V> adapt(final Map<K,V> theMap) {
		return (LazyMap<K,V>)theMap;
	}
}
