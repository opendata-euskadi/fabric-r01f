package r01f.services.client.servicesproxy.rest;

import java.util.ArrayList;

import com.google.common.reflect.TypeToken;

import r01f.exceptions.Throwables;
import r01f.guids.PersistableObjectOID;
import r01f.httpclient.HttpResponse;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.FindError;
import r01f.model.persistence.FindOK;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.ServiceProxyException;
import r01f.types.url.Url;
import r01f.util.types.Strings;

public class RESTResponseToFindResultMapper<O extends PersistableObjectOID,M extends PersistableModelObject<O>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Marshaller _marshaller;
	private final Class<M> _modelObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTResponseToFindResultMapper(final Marshaller marshaller,
										  final Class<M> modelObjectType) {
		_marshaller = marshaller;
		_modelObjectType = modelObjectType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public FindResult<M> mapHttpResponseForEntities(final SecurityContext securityContext,
													final Url restResourceUrl,final HttpResponse httpResponse) {
		FindResult<M> outOperationResult = null;
		if (httpResponse.isSuccess()) {
			outOperationResult = _mapHttpResponseForSuccessFindingEntities(securityContext,
																	   	   restResourceUrl,httpResponse);
		} else {
			outOperationResult = _mapHttpResponseForErrorFindigEntities(securityContext,
														  				restResourceUrl,httpResponse);
		}
		return outOperationResult;
	}
	@SuppressWarnings({ "unused" })
	protected FindOK<M> _mapHttpResponseForSuccessFindingEntities(final SecurityContext securityContext,
												   	   			  final Url restResourceUrl,final HttpResponse httpResponse) {
		FindOK<M> outOperationResult = null;
		
		// [0] - Load the response		
		String responseStr = httpResponse.loadAsString();		// DO not move!!
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		// [1] - Map the response
		outOperationResult = _marshaller.forReading().fromXml(responseStr,
															  new TypeToken<FindOK<M>>() { /* nothing */ });
		if (outOperationResult.getOrThrow() == null) outOperationResult.setOperationExecResult(new ArrayList<M>());	// ensure an empty array list for no results
		
		// [2] - Return
		return outOperationResult;
	}
	protected FindError<M> _mapHttpResponseForErrorFindigEntities(final SecurityContext securityContext,
												    			  final Url restResourceUrl,final HttpResponse httpResponse) {
		FindError<M> outOpError = null;
		
		// [0] - Load the http response text
		String responseStr = httpResponse.loadAsString();
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		
		// [1] - Server error (the request could NOT be processed)
		if (httpResponse.isServerError()) {
			outOpError = FindResultBuilder.using(securityContext)
										  .on(_modelObjectType)
										  .errorFindingEntities()
										  	  		.causedBy(responseStr);
		}
		// [2] - Error while request processing: the FindError comes INSIDE the response
		else {
			outOpError = FindResultBuilder.using(securityContext)
										  .on(_modelObjectType)
										  .errorFindingEntities()
										  	  		.causedBy(responseStr);
		}
		// [4] - Return the CRUDOperationResult
		return outOpError;
	}
}
