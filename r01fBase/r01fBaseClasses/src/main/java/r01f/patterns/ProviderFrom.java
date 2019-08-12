package r01f.patterns;

/**
 * Provides a value using the given object
 * @param <S>
 * @param <T>
 */
public interface ProviderFrom<S,T> {
	public T from(final S source);
}
