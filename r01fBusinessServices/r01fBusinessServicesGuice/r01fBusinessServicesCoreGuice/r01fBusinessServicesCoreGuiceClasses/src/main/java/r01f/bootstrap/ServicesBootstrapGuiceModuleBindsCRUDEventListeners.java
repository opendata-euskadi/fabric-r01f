package r01f.bootstrap;


import com.google.inject.Binder;

/**
 * Interface to be implemented by {@link BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase} subtypes that 
 * are interested in binding CRUD event listeners
 */
public interface ServicesBootstrapGuiceModuleBindsCRUDEventListeners {
	/**
	 * Binds the indexers (instances of {@link IndexerCRUDOKEventListener})
	 * @param binder
	 */
	public void bindCRUDEventListeners(final Binder binder);	
}
