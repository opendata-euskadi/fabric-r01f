package r01f.patterns;

public interface ProviderWith<O,T> {
	public T provideValue(final O other);
}
