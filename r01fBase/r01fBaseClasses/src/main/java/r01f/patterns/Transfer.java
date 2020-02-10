package r01f.patterns;

/**
 * Transfer data from one object to another
 * @param <S>
 * @param <D>
 */
public interface Transfer<S,D> {
	public void transfer(final S source,D destination);
}
