package r01f.patterns;

/**
 * Interface for factory types
 * @param <T> the type to create
 */
public interface Factory<T> {
	/**
	 * @return an instance of type T
	 */
	public T create();
}
