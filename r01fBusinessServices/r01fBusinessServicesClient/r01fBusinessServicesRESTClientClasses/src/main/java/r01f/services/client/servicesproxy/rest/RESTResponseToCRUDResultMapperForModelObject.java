package r01f.services.client.servicesproxy.rest;

import java.util.Iterator;

import com.google.common.reflect.TypeToken;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.exceptions.Throwables;
import r01f.guids.AnyOID;
import r01f.guids.OID;
import r01f.guids.PersistableObjectOID;
import r01f.httpclient.HttpResponse;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDError;
import r01f.model.persistence.CRUDOK;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistenceErrorType;
import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.reflection.ReflectionUtils;
import r01f.securitycontext.SecurityContext;
import r01f.services.ServiceProxyException;
import r01f.types.url.Url;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

public class RESTResponseToCRUDResultMapperForModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Marshaller _marshaller;
	protected final Class<M> _modelObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTResponseToCRUDResultMapperForModelObject(final Marshaller marshaller,
							    						final Class<M> modelObjectType) {
		_marshaller = marshaller;
		_modelObjectType = modelObjectType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceCRUDResultIdOnErrorStep mapHttpResponseForEntity(final SecurityContext securityContext,
															   	 	   final PersistenceRequestedOperation requestedOp,
															   	 	   final Url restResourceUrl,final HttpResponse httpResponse) {
		return new PersistenceCRUDResultIdOnErrorStep(securityContext,
													  requestedOp,
													  restResourceUrl,httpResponse);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PersistenceCRUDResultIdOnErrorStep {
		private final SecurityContext securityContext;
	   	private final PersistenceRequestedOperation requestedOp;
	   	private final Url restResourceUrl;
	   	private final HttpResponse httpResponse;
	   	
	   	public CRUDResult<M> identifiedOnErrorBy(final OID... oids) {
	   		CRUDResult<M> outCRUDResult = null;
	   		if (oids.length == 1) {
	   			outCRUDResult = _identifiedOnErrorBy(oids[0]);
	   		} else {
		   		StringBuilder oidStr = new StringBuilder(oids.length * 32);
		   		for (Iterator<OID> oidIt = CollectionUtils.of(oids).asCollection().iterator(); oidIt.hasNext(); ) {
		   			OID oid = oidIt.next();
		   			oidStr.append(oid.asString());
		   			if (oidIt.hasNext()) oidStr.append("/");
		   		}
		   		outCRUDResult = _identifiedOnErrorBy(AnyOID.forId(oidStr.toString()));
	   		}
	   		return outCRUDResult;
	   	}
	   	public CRUDResult<M> identifiedOnErrorBy(final String any) {
	   		return _identifiedOnErrorBy(AnyOID.forId(any));
	   	}
		private CRUDResult<M> _identifiedOnErrorBy(final OID oid) {
			CRUDResult<M> outOperationResult = null;
			if (httpResponse.isSuccess()) {
				outOperationResult = _mapHttpResponseForSuccess(securityContext,
																requestedOp,
																restResourceUrl,httpResponse);
			} else {
				outOperationResult = _mapHttpResponseForError(securityContext,
															  requestedOp,
															  oid,
															  restResourceUrl,httpResponse);
			}
			return outOperationResult;
		}
	}
	public CRUDResult<M> mapHttpResponseForEntity(final SecurityContext securityContext,
												  final PersistenceRequestedOperation requestedOp,
												  final M targetEntity,
												  final Url restResourceUrl,final HttpResponse httpResponse) {
		return this.mapHttpResponseForEntity(securityContext,
											 requestedOp,
											 targetEntity.getOid(),
											 restResourceUrl,httpResponse);
	}
	public CRUDResult<M> mapHttpResponseForEntity(final SecurityContext securityContext,
												  final PersistenceRequestedOperation requestedOp,
												  final OID targetEntityOid,
												  final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDResult<M> outOperationResult = null;
		if (httpResponse.isSuccess()) {
			outOperationResult = _mapHttpResponseForSuccess(securityContext,
															requestedOp,
															restResourceUrl,httpResponse);
		} else {
			outOperationResult = _mapHttpResponseForError(securityContext,
														  requestedOp,
														  targetEntityOid,
														  restResourceUrl,httpResponse);
		}
		return outOperationResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SUCCESS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unused","serial" })
	protected CRUDOK<M> _mapHttpResponseForSuccess(final SecurityContext securityContext,
												   final PersistenceRequestedOperation requestedOp,
												   final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDOK<M> outOperationResult = null;
		
		// [0] - Load the response		
		String responseStr = httpResponse.loadAsString();		// DO not move!!
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		// [1] - Map the response
		outOperationResult = _marshaller.forReading().fromXml(responseStr,
															  new TypeToken<CRUDOK<M>>() { /* nothging */});
		
		// [2] - Return
		return outOperationResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds back a {@link CRUDError} object from the {@link HttpResponse} object
	 * The exception was mapped to the {@link HttpResponse} at server type r01f.rest.RESTExceptionMappers object
	 * @param securityContext
	 * @param requestedOp
	 * @param requestedOid
	 * @param restResourceUrl
	 * @param httpResponse
	 * @return
	 */
	protected CRUDError<M> _mapHttpResponseForError(final SecurityContext securityContext,
												    final PersistenceRequestedOperation requestedOp,
												    final OID requestedOid,
												    final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDError<M> outOpError = null;
		
		// [0] - Load the http response text
		String responseStr = httpResponse.loadAsString();
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		
		// [1] - Server error (the request could NOT be processed)
		if (httpResponse.isServerError()) {
			outOpError = CRUDResultBuilder.using(securityContext)
										  .on(_modelObjectType)
										  .not(requestedOp)	
										  .becauseServerError(responseStr)	// the rest endpoint response is the error as TEXT
										 		.about(requestedOid).build();
		}
		// [2] - Client error (the client sent an unprocessable entity)
		if (httpResponse.isClientError()) {
			if (httpResponse.isNotFound()) {
				// Not found
				outOpError = CRUDResultBuilder.using(securityContext)
											  .on(_modelObjectType)
											  .not(requestedOp)													
											  .becauseClientRequestedEntityWasNOTFound()
											 		 .about(requestedOid).build();
			} else {
				// other client errors: entity update conflict, illegal argument, etc
				String errorCodeHeader = httpResponse.getSingleValuedHeaderAsString("x-r01-errorCode");
				String extErrorCodeHeader = httpResponse.getSingleValuedHeaderAsString("x-r01-extErrorCode");
				String errorMessageHeader = httpResponse.getSingleValuedHeaderAsString("x-r01-errorMessage");
				//String requestedOperationHeader = httpResponse.getSingleValuedHeaderAsString("x-r01-requestedOperation");
				String errorJavaTypeHeader = httpResponse.getSingleValuedHeaderAsString("x-r01-errorType");
				
				Class<? extends Throwable> errorJavaType = null;
				if (Strings.isNOTNullOrEmpty(errorJavaTypeHeader)) errorJavaType = ReflectionUtils.typeFromClassName(errorJavaTypeHeader);
				
				if (errorJavaType != null && ReflectionUtils.isSubClassOf(errorJavaType,PersistenceException.class)) {
					PersistenceErrorType persistErrorType = Strings.isNOTNullOrEmpty(errorCodeHeader) ? PersistenceErrorType.fromName(errorCodeHeader) : null;
					int extErrorCode = Strings.isNOTNullOrEmpty(extErrorCodeHeader) ? Numbers.toInt(extErrorCodeHeader) : -1;
					
					if (persistErrorType != null) {
						outOpError = CRUDResultBuilder.using(securityContext)
													  .on(_modelObjectType)
													  .not(requestedOp)
													  .becauseClientError(persistErrorType,errorMessageHeader)
													  		.about(requestedOid)
													  		.buildWithExtendedErrorCode(extErrorCode);
					}
				}
				// The exception type is unknown... but it's sure it's a client bad request
				if (outOpError == null) {
					outOpError = CRUDResultBuilder.using(securityContext)
												  .on(_modelObjectType)
												  .not(requestedOp)
												  .becauseClientBadRequest(errorMessageHeader)
												  		.about(requestedOid).build();
				}
			}
		}
		// [3] - Unknown error
		else {
			outOpError = CRUDResultBuilder.using(securityContext)
										  .on(_modelObjectType)
										  .not(requestedOp)	
										  .becauseServerError(responseStr)	// the rest endpoint response is the error as TEXT
										 		.about(requestedOid).build();
		}
		// [4] - Return the CRUDOperationResult
		return outOpError;
	}
	protected CRUDError<M> _mapHttpResponseForError(final SecurityContext securityContext,
												    final PersistenceRequestedOperation requestedOp,
												    final M requestedEntity,
												    final Url restResourceUrl,final HttpResponse httpResponse) {
		CRUDError<M> outOpError = null;

		// [0] - Load the http response text
		String responseStr = httpResponse.loadAsString();
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
				
		// [1] - Server error (the request could NOT be processed)
		if (httpResponse.isServerError()) {
			outOpError = CRUDResultBuilder.using(securityContext)
										  .on(_modelObjectType)
										  .not(requestedOp)	
										  .becauseServerError(responseStr)	// the rest endpoint response is the error as TEXT
										 		.about(requestedEntity).build();
		}
		// [2] - Client error (the client sent an unprocessable entity)
		else if (httpResponse.isClientError()) {
			if (httpResponse.isNotFound()) {
				// Not found
				outOpError = CRUDResultBuilder.using(securityContext)
											  .on(_modelObjectType)
											  .not(requestedOp)													
											  .becauseClientCannotConnectToServer(restResourceUrl)
											 		.about(requestedEntity.getOid()).build();
			} else {
				// other client errors: entity update conflict, illegal argument, etc
				//String errorCode = httpResponse.getSingleValuedHeaderAsString("x-r01-errorCode");
				String errorMessage = httpResponse.getSingleValuedHeaderAsString("x-r01-errorMessage");
				outOpError = CRUDResultBuilder.using(securityContext)
											  .on(_modelObjectType)
											  .not(requestedOp)	
											  .becauseClientBadRequest(errorMessage)
											  		.about(requestedEntity).build();
			}
		}
		// [3] - Unknown error
		else {
			outOpError = CRUDResultBuilder.using(securityContext)
										  .on(_modelObjectType)
										  .not(requestedOp)	
										  .becauseServerError(responseStr)	// the rest endpoint response is the error as TEXT
										 		.about(requestedEntity).build();
		}
		// [4] - Return the CRUDOperationResult
		return outOpError;
	}
}
