package r01f.util.types.collections;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Models a Map.Entry
 * It's used to add many Map entries in a fluent way:
 * <pre class='brush:java'>
 * 		MyType type = new MyType();
 * 		type.addEntries(MapEntry.of("id1",new MyEntry()),
 * 						MapEntry.of("id2",new MyEntry()),
 * 						MapEntry.of("id3",new MyEntry()));
 * </pre>
 * MyType has to implement an addEntries method like:
 * <pre class='brush:java'>
 * 		public class MyType {
 * 			private Map<String,MyEntry> myMap = Maps.newHashMap();
 * 			
 * 			public MyType addEntries(MapEntry<String,MyEntry>... entries) {
 * 				if (CollectionUtils.hasData(entries)) {
 * 					if (myMap == null) myMap = new HashMap<String,MyEntry>();
 * 					for (MyEntry entry : entries) {
 * 						myMap.put(entry.getKey(),entry.getValue());
 * 					}
 * 				}
 * 			}
 * 		}
 * </pre>

 * @param <K>
 * @param <V>
 */
@Accessors(prefix="_")
public class MapEntry<K,V> 
  implements Map.Entry<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////
//  STATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final K _key;
	@Getter private  	  V _value;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private MapEntry(final K key,final V value) {
		_key = key;
		_value = value;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> MapEntry<K,V> of(final K key,final V value) {
		if (key == null) throw new IllegalArgumentException("The MapEntry key cannot be null");
		return new MapEntry<K,V>(key,value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ Map.Entry
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public V setValue(final V value) {
		_value = value;
		return _value;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OBJECT OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override 
	public boolean equals(final Object object) {
		if (object == null) return false;
		if (this == object) return true;
	    if (object instanceof Entry) {
	      Entry<?, ?> that = (Entry<?, ?>) object;
	      return Objects.equal(this.getKey(),that.getKey())
	          && Objects.equal(this.getValue(),that.getValue());
	    }
	    return false;
	}
	@Override 
	public int hashCode() {
	    K k = getKey();
	    V v = getValue();
	    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
	}
	/**
	 * Returns a string representation of the form {@code {key}={value}}.
	 */
	@Override 
	public String toString() {
		return getKey() + "=" + getValue();
	}
}
