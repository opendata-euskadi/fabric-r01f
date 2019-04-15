package r01f.services.client.servicesproxy.rest;

import java.util.Date;

import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.guids.VersionIndependentOID;
import r01f.httpclient.HttpResponse;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase;
import r01f.services.interfaces.CRUDServicesForVersionableModelObject;
import r01f.types.url.Url;

public abstract class RESTServicesForVersionableCRUDServicesProxyBase<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet>
              extends RESTServicesForDBCRUDProxyBase<O,M>
    	   implements CRUDServicesForVersionableModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public <P extends RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase<O>>
		   RESTServicesForVersionableCRUDServicesProxyBase(final Marshaller marshaller,
										   	    		   final Class<M> modelObjectType,
										   	    		   final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  modelObjectType,
			  servicesRESTResourceUrlPathBuilder,
			  new RESTResponseToCRUDResultMapperForVersionableModelObject<O,M>(marshaller,
																			   modelObjectType));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private RESTResponseToCRUDResultMapperForVersionableModelObject<O,M> getResponseToCRUDResultMapperForVersionableModelObject() {
		return (RESTResponseToCRUDResultMapperForVersionableModelObject<O,M>)_responseToCRUDResultMapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CRUD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<M> loadActiveVersionAt(final SecurityContext securityContext,
						   			   		 final VersionIndependentOID oid,final Date date) {
		// do the http call: GET the version whose activation date is the provided one
		Url restResourceUrl = null;
		if (date != null) {
			this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase.class)
								   .pathOfActiveVersionAt(oid,date));	// version active at the provided date
		} else {
			this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase.class)
								   .pathOfActiveVersion(oid));			// currently active version
		}
		String ctxXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.GET(restResourceUrl,
										 				   ctxXml);
		// map the response
		CRUDResult<M> outResponse = this.getResponseToCRUDResultMapperForVersionableModelObject()
											.mapHttpResponseForEntity(securityContext,
															  		  PersistenceRequestedOperation.LOAD,
															  		  oid,date,
															  		  restResourceUrl,httpResponse); 
		return outResponse;
	}
	@Override 
	public CRUDResult<M> loadWorkVersion(final SecurityContext securityContext,
							 			 final VersionIndependentOID oid) {
		// do the http call: GET the version whose activation date is NULL -it's not active-
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase.class)
															   .pathOfWorkVersion(oid));
		String ctxXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.GET(restResourceUrl,
										 				   ctxXml);
		// map the response
		CRUDResult<M> outResponse = this.getResponseToCRUDResultMapperForVersionableModelObject()
												.mapHttpResponseForEntity(securityContext,
															  			  PersistenceRequestedOperation.LOAD,
															  			  oid,null,
															  			  restResourceUrl,httpResponse); 
		// log & return 
		_logResponse(restResourceUrl,outResponse);
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDOnMultipleResult<M> deleteAllVersions(final SecurityContext securityContext,
													 final VersionIndependentOID oid) {
		return this.deleteAllVersions(securityContext,
									  oid,
									  null);	// no async callback
	}
	@Override
	public CRUDOnMultipleResult<M> deleteAllVersions(final SecurityContext securityContext,
													 final VersionIndependentOID oid,
													 final PersistenceOperationCallbackSpec callbackSpec) {
		// do the http call
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase.class)
															   .pathOfAllVersions(oid));
		String securityContextXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.DELETE(restResourceUrl,
															  securityContextXml);
		// map the response
		CRUDOnMultipleResult<M> outResults = this.getResponseToCRUDResultMapperForVersionableModelObject()
																.mapHttpResponseOnMultipleEntity(securityContext,
												   					     	 		  			 PersistenceRequestedOperation.DELETE,
												   					     	 		  			 oid,	
												   					     	 		  			 restResourceUrl,httpResponse);
		return outResults;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ACTIVATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public CRUDResult<M> activate(final SecurityContext securityContext,
								  final M entityToBeActivated) {
		return this.activate(securityContext,
						 	 entityToBeActivated,
						 	 null);		// no async callback
	}
	@Override 
	public CRUDResult<M> activate(final SecurityContext securityContext,
								  final M entityToBeActivated,
								  final PersistenceOperationCallbackSpec callbackSpec) {
		// do the http call: a CREATION (POST) of the entiy at the /versions/activeVersion resource path
		Url restResourceVersionUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase.class)
																				.pathOfActiveVersion(entityToBeActivated.getOid().getOid()));	// Version independent oid
		String securityContextXml = _marshaller.forWriting().toXml(securityContext);
		String entityXml = _marshaller.forWriting().toXml(entityToBeActivated);
		HttpResponse httpResponse = DelegateForRawREST.POST(restResourceVersionUrl,
										 				    securityContextXml,
										 				    entityXml);	// Empty PUT
		// map the response
		CRUDResult<M> outResult = this.getResponseToCRUDResultMapperForVersionableModelObject()
											.mapHttpResponseForEntity(securityContext,
														    		  PersistenceRequestedOperation.CREATE,
														    		  restResourceVersionUrl,httpResponse)
													.identifiedOnErrorBy(entityToBeActivated.getOid());
		// log & return 
		_logResponse(restResourceVersionUrl,outResult);
		return outResult;
	}
}
