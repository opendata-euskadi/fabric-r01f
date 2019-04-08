package r01f.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor @AllArgsConstructor
@Accessors(prefix="_")
public abstract class KeyValue<K,V> {
	@Getter @Setter private K _key;
	@Getter @Setter private V _value;
}
