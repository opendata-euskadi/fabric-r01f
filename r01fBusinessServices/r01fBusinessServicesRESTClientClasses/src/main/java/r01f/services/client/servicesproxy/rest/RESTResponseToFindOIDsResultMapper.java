package r01f.services.client.servicesproxy.rest;

import java.util.ArrayList;

import com.google.common.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.guids.PersistableObjectOID;
import r01f.httpclient.HttpResponse;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.FindOIDsError;
import r01f.model.persistence.FindOIDsOK;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindOIDsResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.ServiceProxyException;
import r01f.types.url.Url;
import r01f.util.types.Strings;

@Slf4j
public class RESTResponseToFindOIDsResultMapper<O extends PersistableObjectOID,M extends PersistableModelObject<O>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Marshaller _marshaller;
	private final Class<M> _modelObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTResponseToFindOIDsResultMapper(final Marshaller marshaller,
											  final Class<M> modelObjectType) {
		_marshaller = marshaller;
		_modelObjectType = modelObjectType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public FindOIDsResult<O> mapHttpResponseForOids(final SecurityContext securityContext,
													final Url restResourceUrl,final HttpResponse httpResponse) {
		FindOIDsResult<O> outOperationResult = null;
		if (httpResponse.isSuccess()) {
			outOperationResult = _mapHttpResponseForSuccessFindingOids(securityContext,
																	   restResourceUrl,httpResponse);
		} else {
			outOperationResult = _mapHttpResponseForErrorFindigOids(securityContext,
														  			restResourceUrl,httpResponse);
		}
		return outOperationResult;
	}
	@SuppressWarnings({ "unused" })
	protected FindOIDsOK<O> _mapHttpResponseForSuccessFindingOids(final SecurityContext securityContext,
												   	   			  final Url restResourceUrl,final HttpResponse httpResponse) {
		FindOIDsOK<O> outOperationResult = null;
		
		// [0] - Load the response		
		String responseStr = httpResponse.loadAsString();		// DO not move!!
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		// [1] - Map the response
		outOperationResult = _marshaller.forReading().fromXml(responseStr,
															  new TypeToken<FindOIDsOK<O>>() { /* nothing */ });
		if (outOperationResult.getOrThrow() == null) outOperationResult.setOperationExecResult(new ArrayList<O>());	// ensure an empty array list for no results
		
		// [2] - Return
		return outOperationResult;
	}
	protected FindOIDsError<O> _mapHttpResponseForErrorFindigOids(final SecurityContext securityContext,
												    			  final Url restResourceUrl,final HttpResponse httpResponse) {
		FindOIDsError<O> outOpError = null;
		
		// [0] - Load the http response text
		String responseStr = httpResponse.loadAsString();
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		
		// [1] - Server error (the request could NOT be processed)
		if (httpResponse.isServerError()) {
			outOpError = FindOIDsResultBuilder.using(securityContext)
										  	  .on(_modelObjectType)
										  	  .errorFindingOids()
										  	  		.causedBy(responseStr);
		}
		// [2] - Error while request processing: the PersistenceCRUDError comes INSIDE the response
		else {
			outOpError = FindOIDsResultBuilder.using(securityContext)
										  	  .on(_modelObjectType)
										  	  .errorFindingOids()
										  	  		.causedBy(responseStr);
		}
		// [4] - Return the CRUDOperationResult
		return outOpError;
	}
}
