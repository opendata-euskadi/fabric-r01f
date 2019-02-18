package r01f.types;

/**
 * Factory of a {@link KeyValue} type
 * @param <K> key type
 * @param <V> value type
 */
public interface KeyValueFactory<K,V> {
	public KeyValue<K,V> createFor(final K name,final V value);
}
