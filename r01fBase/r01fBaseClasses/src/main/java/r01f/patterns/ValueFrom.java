package r01f.patterns;

/**
 * Provides a value from a given object
 * @param <S>
 * @param <T>
 */
public interface ValueFrom<S,T> {
	public T from(final S source);
}
