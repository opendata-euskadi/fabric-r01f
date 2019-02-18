package r01f.aspects.core.lazyload;

import java.lang.reflect.Field;
import java.util.Map;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.lazyload.LazyLoadCapable;
import r01f.aspects.interfaces.lazyload.LazyLoadedTypeSupplier;
import r01f.aspects.interfaces.lazyload.LazyLoadedTypeSupplierFactory;
import r01f.aspects.interfaces.lazyload.LazyMapSupplier;
import r01f.aspects.interfaces.lazyload.LazyTypeSupplier;
import r01f.reflection.ReflectionUtils;
import r01f.types.dirtytrack.DirtyTrackAdapter;

public class LazyLoadUtils {
	/**
	 * Loads a type's field lazily:
	 * <ul>
	 * 		<li>If the field is a Map it wraps the field with a {@link r01f.collections.lazy.LazyMap}) which is charge of loading the values when needed</li>
	 * 		<li>If the field is a normal object it loads the object</li>
	 * </ul>
	 * This method also sets the field with de value
	 * @param container	 the field container
	 * @param field the field
	 * @return the lazy-loaded object for the field
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object loadTypeFieldLazily(final Object container,final Field field) {
		Object lazyLoaded = null;
		
		// [0].- If the container object is a DirtyStateTrackable instance and also it's NEW, there's no sense
		//		 in loading dependant objects... there'd not be any
		if (container instanceof DirtyStateTrackable 
		 && DirtyTrackAdapter.adapt(container).getTrackingStatus().isThisNew()) {
			return null;
		}
		
		// [1].- Get the object's instance using a supplier
		try {
			if (ReflectionUtils.isImplementing(field.getType(),Map.class)) {
				LazyMapSupplier supplier = (LazyMapSupplier)_createSupplierFor(container,
															  				   field);
				lazyLoaded = supplier.instanceFor(container);
			} else {
				LazyTypeSupplier supplier = (LazyTypeSupplier)_createSupplierFor(container,
															   					 field);
				lazyLoaded = supplier.instanceFor(container);
			}
		} catch(ClassCastException ccEx) {
			// A ClassCastException is thrown because the Supplier is not returning the expected object's type
			// (thisJoinPoint.getTarget() is NOT of the expected type)
			throw new IllegalArgumentException("The Supplier does NOT match with the actual type: " + container.getClass().getName(),
											   ccEx);
		}
		
		// [2].- The DirtyTracking status MUST be copied from the parent object to the new child object
		if (container instanceof DirtyStateTrackable && lazyLoaded instanceof DirtyStateTrackable) {
			DirtyStateTrackable trckContainer = DirtyTrackAdapter.adapt(container);
			DirtyStateTrackable trckLazyLoaded = DirtyTrackAdapter.adapt(lazyLoaded);
			if (trckContainer.getTrackingStatus().isThisDirtyTracking() != trckLazyLoaded.getTrackingStatus().isThisDirtyTracking()
			 || trckContainer.getTrackingStatus().isThisCheckIfValueChanges() != trckLazyLoaded.getTrackingStatus().isThisCheckIfValueChanges()) {
				trckLazyLoaded.getTrackingStatus().setThisDirtyTracking(trckContainer.getTrackingStatus().isThisDirtyTracking());
				trckLazyLoaded.getTrackingStatus().setThisCheckIfValueChanges(trckContainer.getTrackingStatus().isThisCheckIfValueChanges());
			}
		}
		
		// [4].- Set the instance 
		ReflectionUtils.setFieldValue(container,field,lazyLoaded,
									  false);	// use accessors
		
		return lazyLoaded;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
// 	METODOS PRIVADOS
/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a supplier for the field
     * @param container the container of the {@link LazyLoadCapable} annotated field
	 * @param field the field
	 * @return the supplier
	 */
	@SuppressWarnings("unchecked")
	private static <T> LazyLoadedTypeSupplier<T> _createSupplierFor(final Object container,
																	final Field field) {
		LazyLoadedTypeSupplier<T> outSupplier = null;
		
		LazyLoadedTypeSupplier<T> supplier = (LazyLoadedTypeSupplier<T>)_createLazyLoaderSupplierInstance(container,field);
		
		Class<?> containerType = container.getClass();
		if (ReflectionUtils.isImplementing(field.getType(),Map.class)) {
			if (!(supplier instanceof LazyMapSupplier)) throw new IllegalArgumentException("The field " + containerType.getName() + "." +  field.getName() + " (" + field.getType() + ") is annotated with @LazyLoadCapable, BUT the 'supplierFactory' is NOT providing instances of LazyMapSupplier");
			LazyMapSupplier<?,T> mapSupplier = (LazyMapSupplier<?,T>)supplier;
			outSupplier = mapSupplier;
		} else {
			if (!(supplier instanceof LazyTypeSupplier)) throw new IllegalArgumentException("The field " + containerType.getName() + "." +  field.getName() + " (" + field.getType() + ") is annotated with @LazyLoadCapable, BUT the 'supplierFactory' is NOT providing instances of LazyTypeSupplier");
			LazyTypeSupplier<T> typeSupplier = (LazyTypeSupplier<T>)supplier;
			outSupplier = typeSupplier;
		}
		return outSupplier;
	}
    /**
     * Gets an instance of {@link r01f.util.types.lazy.LazyLoadedTypeSupplier} from the @LazyLoadCapable annotation's supplierFactory  parameter value
     * @param container the container of the {@link LazyLoadCapable} annotated field
     * @param f the annotated field
     * @return an instance of {@link r01f.util.types.lazy.LazyLoadedTypeSupplier} that could be either a {@link LazyTypeSupplier} instance or  {@link LazyMapSupplier} instance
     */
	@SuppressWarnings("unchecked")
	private static <T> LazyLoadedTypeSupplier<T> _createLazyLoaderSupplierInstance(final Object container,
																				   final Field f) {
		Class<?> containerType = container.getClass();
		
		LazyLoadCapable lannot = f.getAnnotation(LazyLoadCapable.class);
		Class<?> supplierFactoryType = lannot.supplierFactory();
		if (supplierFactoryType == null || supplierFactoryType == LazyLoadCapable.DEFAULT_SUPPLIER_FACTORY.class) throw new IllegalArgumentException("The field " + containerType.getName() + "." +  f.getName() + " (" + f.getType() + ") is annotated with @LazyLoadCapable, BUT the 'supplierFactory' attribute is missing");
		
		// If the type of the supplier provided by the annotation is THE SAME as the type of the field container, 
		// just return the container, otherwise create a new supplier instance
		Object outSupplierFactory = null;
		if (supplierFactoryType == container.getClass()) {
			outSupplierFactory = container;
		} else {
			outSupplierFactory = ReflectionUtils.createInstanceOf(supplierFactoryType);
		}
		if ( !(outSupplierFactory instanceof LazyLoadedTypeSupplierFactory) ) throw new IllegalArgumentException("The field " + containerType.getName() + "." +  f.getName() + " (" + f.getType() + ") is annotated with @LazyLoadCapable, BUT the 'supplierFactory' attribute is NOT a type implementing LazySupplierFactory");
		
		return ((LazyLoadedTypeSupplierFactory<T>)outSupplierFactory).createSupplier();
    }
}
