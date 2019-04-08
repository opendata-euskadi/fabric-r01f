package r01f.patterns;

/**
 * Interface for factory types
 * @param <T> the type to create
 */
public interface FactoryFromType<T> {
	/**
	 * @param type
	 * @param args
	 * @return an instance of type T
	 */
	public T create(final Class<? extends T> type,
					final Object... args);
}
