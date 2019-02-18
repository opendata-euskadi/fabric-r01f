package r01f.patterns;

/**
 * Interface for factory types
 * @param <T> the type to create
 */
public interface FactoryFrom<O,T> {
	/**
	 * @return an instance of type T built from other
	 */
	public T from(final O other);
}
