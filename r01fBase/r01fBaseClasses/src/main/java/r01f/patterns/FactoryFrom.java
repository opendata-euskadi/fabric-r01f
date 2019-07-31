package r01f.patterns;

/**
 * Interface for factory types
 * @param <S> the source type
 * @param <T> the type to create
 */
public interface FactoryFrom<S,T> {
	/**
	 * Creates an object instance using the given object
	 * @param source
	 * @return an instance of type T built from other
	 */
	public T from(final S source);
}
