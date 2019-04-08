package r01f.collections;

import java.util.Map;

/**
 * Interface to access the key for a type to be indexed in a {@link Map}
 * The type implementing this interface could use many ways to guess an object's key:
 * 		- A instrospecto for each object type:
 * 			KeyIntrospector<String,MyObj> intr = new KeyIntrospector<String,MyObj>() {
 * 														@Override
 * 														public String of(MyObj obj) {
 * 															return obj.getTheKey();
 * 														} 
 * 												 }
 * 		- Use reflection to find a @OidField -annotated field
 */
public interface KeyIntrospector<K,V> {
	/**
	 * Returns an object's instance key to be used as a {@link Map.Entry} key
	 * @param obj
	 * @return
	 */
	public K keyFor(V obj);
}