package r01f.services.client.api.delegates;

import java.util.Collection;

import javax.inject.Provider;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.OID;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.FindServicesForDependentModelObject;

/**
 * Adapts Persistence API method invocations to the service proxy that performs the core method invocations
 * @param <O>
 * @param <M>
 */
@Slf4j
public class ClientAPIDelegateForDependentModelObjectFindServices<O extends PersistableObjectOID,M extends PersistableModelObject<O>,
															      P extends PersistableModelObject<?>> 
	 extends ClientAPIServiceDelegateBase<FindServicesForDependentModelObject<O,M,P>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForDependentModelObjectFindServices(final Provider<SecurityContext> securityContextProvider,
													   			final Marshaller modelObjectsMarshaller,
													   			final FindServicesForDependentModelObject<O,M,P> services) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  services);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds all objects that are dependent on the given one returning the objects oids
	 * @param parentOid
	 * @return a collection of the dependent entities
	 */
	@SuppressWarnings("unchecked")
	public <PO extends OID> Collection<O> findOidsOfDependentsOf(final PO parentOid) {
		FindOIDsResult<O> findResult = this.getServiceProxyAs(FindServicesForDependentModelObject.class)
												.findOidsOfDependentsOf(this.getSecurityContext(),
																  		parentOid);
		
		log.debug(findResult.debugInfo().toString());
		
		Collection<O> outOids = findResult.getOrThrow();
		return outOids;
	}
	/**
	 * Find all objects that are dependent on the given one returning the full objects
	 * @param parentOid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <PO extends OID> Collection<M> findDependentsOf(final PO parentOid) {
		FindResult<M> findResult = this.getServiceProxyAs(FindServicesForDependentModelObject.class)
											.findDependentsOf(this.getSecurityContext(),
															  parentOid);
		Collection<M> outObjs = findResult.getOrThrow();
		
		// Ensure the returned objects are managed
		ClientAPIModelObjectChangesTrack.startTrackingChangesOnLoaded(outObjs);
		
		// return
		return outObjs;
	}
	/**
	 * Finds all objects that are dependent on the given one
	 * @param parentOid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  <PO extends OID,S extends SummarizedModelObject<M>> Collection<S> findSummariesOfDependentsOf(final PO parentOid) {
		FindSummariesResult<M> findResult = this.getServiceProxyAs(FindServicesForDependentModelObject.class)
													.findSummariesOfDependentsOf(this.getSecurityContext(),
																				 parentOid);
		Collection<S> outSummaries = findResult.getOrThrow();
		return outSummaries;
	}
}
