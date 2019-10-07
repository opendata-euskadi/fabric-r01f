package r01f.aspects.interfaces.lazyload;

/**
 * Lazy loaded type ({@link LazyTypeSupplier} or {@link LazyMapSupplier}) factory
 * @param T supplied type 
 */
public interface LazyLoadedTypeSupplierFactory<T> {
	public LazyLoadedTypeSupplier<T> createSupplier();
}
