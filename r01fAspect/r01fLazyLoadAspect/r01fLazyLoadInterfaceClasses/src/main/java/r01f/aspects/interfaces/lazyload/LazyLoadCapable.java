package r01f.aspects.interfaces.lazyload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LazyLoadCapable {
	/**
	 * LazyLoaded types supplier
	 * @return the lazilyLoaded type supplier factory (a type that crates a supplier of lazy loaded types)
	 */
	@SuppressWarnings("rawtypes")
	public Class<? extends LazyLoadedTypeSupplierFactory> supplierFactory() default DEFAULT_SUPPLIER_FACTORY.class;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	DEFAULT
//		It's not possible to do:
//			public Class<?> supplierFactory() default null;	//<-- this does not compile
//		a trick is used: a DEFAULT_SUPPLIER_FACTORY type is returned 
/////////////////////////////////////////////////////////////////////////////////////////
	static final class DEFAULT_SUPPLIER_FACTORY<T> 
	        implements LazyLoadedTypeSupplierFactory<T> {
		@Override
		public LazyLoadedTypeSupplier<T> createSupplier() {
			throw new IllegalStateException("This is the default supplier factory!");	
		}
	}
}
