package r01f.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Triplet of objects, can be used for returned three values in one method.
 * @param <K>
 * @param <V>
 * @param <S>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class Triplet<K, V, S> {

	@Getter private final K _a;
	@Getter private final V _b;
	@Getter private final S _c;

	public static <K, V, S> Triplet<K, V, S> from(K element0, V element1, S element2) {
		return new Triplet<K, V, S>(element0, element1, element2);
	}
}
