package r01f.collections.lazy;

import java.util.Collection;


/**
 * Adapter type that transforms a {@link r01f.aspects.lazyload.LazyLoadCapable} annotated {@link Collection}
 * to a {@link LazyCollection}
 * <pre>
 * BEWARE!:	The {@link Collection} to be adapted MUST be annotated with {@link r01f.aspects.lazyload.LazyLoadCapable}
 * 				so the LazyLoadAspect aspect is applied: AspectJ weaver weaves the object
 * </pre>
 * Usually is used as:
 * <pre class='brush:java>
 * 		@LazyLoadCapable(supplierFactory=MyLazyCollectionSupplierFactory.class)
 *   	List<MyObj> myCol;
 * 		
 * 		LazyCollection<MyObj> myColAdapted = LazyColAdapter.adapt(myCol);
 * 		myColAdapted.newEntries();
 * </pre>
 */
public class LazyCollectionpAdapter {
	/**
	 * Adapts a {@link LazyCollection}
	 * @param theCol 
	 * @return a {@link LazyCollection} view of the {@link Collection}
	 */
	public static <V> LazyCollection<V> adapt(final Collection<V> theCol) {
		return (LazyCollection<V>)theCol;
	}
}
