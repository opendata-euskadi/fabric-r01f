package r01f.services.client.servicesproxy.rest;

import com.google.common.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.guids.VersionIndependentOID;
import r01f.httpclient.HttpResponse;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.CRUDError;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.ServiceProxyException;
import r01f.types.url.Url;
import r01f.util.types.Strings;

@Slf4j
public class RESTResponseToCRUDResultMapperForVersionableModelObject<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet> 
	 extends RESTResponseToCRUDResultMapperForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTResponseToCRUDResultMapperForVersionableModelObject(final Marshaller marshaller,
							    								   final Class<M> modelObjectType) {
		super(marshaller,
			  modelObjectType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAP RESPONSE
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDResult<M> mapHttpResponseForEntity(final SecurityContext securityContext,
												  final PersistenceRequestedOperation requestedOp,
												  final VersionIndependentOID oid,final Object version,
												  final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDResult<M> outOperationResult = null;
		if (httpResponse.isSuccess()) {
			outOperationResult = _mapHttpResponseForSuccess(securityContext,
															requestedOp,
															restResourceUrl,httpResponse);
		} else {
			outOperationResult = _mapHttpResponseForError(securityContext,
														  requestedOp,
														  oid,version,
														  restResourceUrl,httpResponse);
		}
		return outOperationResult;
	}
	private CRUDError<M> _mapHttpResponseForError(final SecurityContext securityContext,
												  final PersistenceRequestedOperation requestedOp,
												  final VersionIndependentOID oid,final Object version,
												  final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDError<M> outOpError = null;
		
		// [0] - Load the http response text
		String httpResponseString = httpResponse.loadAsString();
		
		// [1] - Cannot connect to server
		if (httpResponse.isNotFound()) {
			outOpError = CRUDResultBuilder.using(securityContext)
										  .on(_modelObjectType)
										  .not(requestedOp)													
										  .becauseClientCannotConnectToServer(restResourceUrl)
										 		.about(oid,version).build();
		} 
		// [2] - Server error (the request could NOT be processed)
		else if (httpResponse.isServerError()) {
			outOpError = CRUDResultBuilder.using(securityContext)
										  .on(_modelObjectType)
										  .not(requestedOp)	
										  .becauseServerError(httpResponseString)	// the rest endpoint response is the error as TEXT
										 		.about(oid,version).build();
		}
		// [3] - Error while request processing: the PersistenceCRUDError comes INSIDE the response
		else {
			outOpError = _marshaller.forReading().fromXml(httpResponseString,	// the rest endpoint response is a PersistenceCRUDError XML
														  new TypeToken<CRUDError<M>>() { /* nothing */ });
		}
		// [4] - Return the CRUDOperationResult
		return outOpError;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDOnMultipleResult<M> mapHttpResponseOnMultipleEntity(final SecurityContext securityContext,
																		   final PersistenceRequestedOperation requestedOp,
																		   final VersionIndependentOID requestedOid,
																		   final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDOnMultipleResult<M> outOperationsResults = null;
		
		if (httpResponse.isSuccess()) {
			outOperationsResults = _mapHttpResponseForSuccessOnMultipleEntity(securityContext, 
																			  requestedOp, 
																			  requestedOid, 
																			  restResourceUrl, 
																			  httpResponse);
		} else {
			outOperationsResults = _mapHttpResponseForErrorOnMultipleEntities(securityContext,
														    				  requestedOp,
														    				  requestedOid,
														    				  restResourceUrl,
														    				  httpResponse);
		}
		return outOperationsResults;
	}
	@SuppressWarnings({ "unused" })
	protected CRUDOnMultipleResult<M> _mapHttpResponseForSuccessOnMultipleEntity(final SecurityContext securityContext,
															   				 	 final PersistenceRequestedOperation requestedOp,
															   				 	 final VersionIndependentOID requestedOid,
															   				 	 final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDOnMultipleResult<M> outOperationsResults = null;
		
		// [0] - Load the http response text
		String responseStr = httpResponse.loadAsString();		// DO not move!!
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		// [1] - Map the response
		outOperationsResults = _marshaller.forReading().fromXml(responseStr,	// Get the REST Service's returned entity: transform from the result string representation (ie xml) to it's object representation
																new TypeToken<CRUDOnMultipleResult<M>>() { /* nothing */ });

		// [2] - Return
		return outOperationsResults;
	}
	protected CRUDOnMultipleResult<M> _mapHttpResponseForErrorOnMultipleEntities(final SecurityContext securityContext,
															   	 				 final PersistenceRequestedOperation requestedOp,
															   	 				 final VersionIndependentOID requestedOid,
															   	 				 final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDOnMultipleResult<M> outOpError = null;
		
		// [0] - Load the http response text
		String responseStr = httpResponse.loadAsString();		// DO not move!!
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		
		
		// [1] - Cannot connect to server
		if (httpResponse.isNotFound()) {
			log.error("REST: cannot connect to REST end-point {}",restResourceUrl);
			outOpError = new CRUDOnMultipleResult<M>(requestedOp,
													 _modelObjectType);
			outOpError.addOperationNOK(CRUDResultBuilder.using(securityContext)
											    .on(_modelObjectType)
											    .not(requestedOp)
											    .becauseClientCannotConnectToServer(restResourceUrl)
											    	.about(requestedOid)
											    .build());
		} 
		// [2] - Server error (the request could NOT be processed)
		else if (httpResponse.isServerError()) {
			log.error("REST: server error for {}",restResourceUrl);
			outOpError = new CRUDOnMultipleResult<M>(requestedOp,
													 _modelObjectType);
			outOpError.addOperationNOK(CRUDResultBuilder.using(securityContext)
											    .on(_modelObjectType)
											    .not(requestedOp)
											    .becauseServerError(httpResponse.loadAsString())	// error details
											    	.about(requestedOid)
											    .build());
		}
		// [3] - Error while request processing: the PersistenceCRUDError comes INSIDE the response
		else {
			outOpError = _marshaller.forReading().fromXml(responseStr,	// the rest endpoint response is a PersistenceCRUDError XML
														  new TypeToken<CRUDOnMultipleResult<M>>() { /* nothing */ });
		}
		// [4] - Return the CRUDOperationResult
		return outOpError;
	}
}