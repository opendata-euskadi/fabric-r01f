package r01f.types.dirtytrack;

import java.util.Collection;
import java.util.Map;

import com.google.common.annotations.GwtIncompatible;

import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableMap;


/**
 * Adaptor type that transforms an object to a {@link DirtyStateTrackable} object
 * <pre>
 * BEWARE:	The object to be adapted MUST implement the {@link DirtyStateTrackable} interface
 * 		    Usually an object implements {@link DirtyStateTrackable} by:
 * 				1.- The object is annotated with {@link ConvertToDirtyStateTrackable}
 * 				2.- Weaving with the DirtyStateTrackable aspect
 * </pre>
 * Usual usage:
 * <pre class='brush:java'>
 * 		MyTrackableObj myObj = new MyTrackableObj();
 * 		myObj.setField("aaa");
 * 		
 * 		DirtyStateTrackable myObjTrackable = DirtyTrackAdapter.adapt(myObj);
 * 		myObjTrackable.startTrackingChanges(true);
 * 
 * 		myObj.addMyEntry("a",new MyChildObj("ccc"));
 * 
 * 		System.out.println("Dirty? " + myObjTrackable.isDirty());
 * </pre>
 */
@GwtIncompatible
@Slf4j
public class DirtyTrackAdapter {
	/**
	 * Adapts a {@link DirtyStateTrackable} object
	 * @param 
	 * @return
	 */
	public static <T> DirtyStateTrackable adapt(final T object) {
		try {
			return (DirtyStateTrackable)object;
		} catch(ClassCastException ccEx) {
			log.error("{} canot be cast-ed to {}: maybe it's not annotated as @{} or maybe weaving is not in use, add -javaagent:aspectjweaver.jar to the VM start command",
					  object.getClass(),DirtyStateTrackable.class,ConvertToDirtyStateTrackable.class.getName());
			throw ccEx;
		}
	}
	/**
	 * Adapts a {@link ChangesTrackableMap}
	 * @param 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> ChangesTrackableMap<K,V> adapt(final Map<K,V> map) {
		return (ChangesTrackableMap<K,V>)map;
	}
	/**
	 * Adapts a {@link Collection} (List or Set) as a {@link ChangesTrackableCollection} 
	 * @param 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V> ChangesTrackableCollection<V> adapt(final Collection<V> col) {
		return (ChangesTrackableCollection<V>)col;
	}
}
