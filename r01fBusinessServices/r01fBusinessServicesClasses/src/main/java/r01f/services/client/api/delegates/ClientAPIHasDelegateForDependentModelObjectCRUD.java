package r01f.services.client.api.delegates;

/**
 * An interface implemented by {@link ClientAPIDelegateForModelObjectCRUDServices} types
 * used to get the delegate (used on tests)
 */
public interface ClientAPIHasDelegateForDependentModelObjectCRUD<D extends ClientAPIDelegateForDependentModelObjectCRUDServices<?,?,?>> {
	public D getClientApiForDependentDelegate();
}
