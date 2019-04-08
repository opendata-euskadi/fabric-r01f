package r01f.services.client.api.delegates;

/**
 * An interface implemented by {@link ClientAPIDelegateForModelObjectFindServices} types
 * used to get the delegate (used on tests)
 */
public interface ClientAPIHasDelegateForDependentModelObjectFind<D extends ClientAPIDelegateForDependentModelObjectFindServices<?,?,?>> {
	public D getClientApiForDependentDelegate();
}
