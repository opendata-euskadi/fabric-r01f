package r01f.services.client.servicesproxy.rest;

import java.util.ArrayList;

import com.google.common.reflect.TypeToken;

import r01f.exceptions.Throwables;
import r01f.guids.PersistableObjectOID;
import r01f.httpclient.HttpResponse;
import r01f.model.PersistableModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.persistence.FindSummariesError;
import r01f.model.persistence.FindSummariesOK;
import r01f.model.persistence.FindSummariesResult;
import r01f.model.persistence.FindSummariesResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.ServiceProxyException;
import r01f.types.url.Url;
import r01f.util.types.Strings;

public class RESTResponseToFindSummariesResultMapper<O extends PersistableObjectOID,M extends PersistableModelObject<O>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Marshaller _marshaller;
	private final Class<M> _modelObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTResponseToFindSummariesResultMapper(final Marshaller marshaller,
										  		   final Class<M> modelObjectType) {
		_marshaller = marshaller;
		_modelObjectType = modelObjectType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public FindSummariesResult<M> mapHttpResponseForSummaries(final SecurityContext securityContext,
													 		  final Url restResourceUrl,final HttpResponse httpResponse) {
		FindSummariesResult<M> outOperationResult = null;
		if (httpResponse.isSuccess()) {
			outOperationResult = _mapHttpResponseForSuccessFindingSummaries(securityContext,
																	   	   restResourceUrl,httpResponse);
		} else {
			outOperationResult = _mapHttpResponseForErrorFindigSummaries(securityContext,
														  				restResourceUrl,httpResponse);
		}
		return outOperationResult;
	}
	@SuppressWarnings({ "rawtypes","unused" })
	protected FindSummariesOK<M> _mapHttpResponseForSuccessFindingSummaries(final SecurityContext securityContext,
												   	   			   			final Url restResourceUrl,final HttpResponse httpResponse) {
		FindSummariesOK<M> outOperationResult = null;
		
		// [0] - Load the response		
		String responseStr = httpResponse.loadAsString();		// DO not move!!
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		// [1] - Map the response
		outOperationResult = _marshaller.forReading().fromXml(responseStr,
															  new TypeToken<FindSummariesOK<M>>() { /* nothing */ });
		if ( ((FindSummariesResult)outOperationResult).getOrThrow() == null) outOperationResult.setOperationExecResult(new ArrayList<SummarizedModelObject<M>>());	// ensure an empty array list for no results
		
		// [2] - Return
		return outOperationResult;
	}
	protected FindSummariesError<M> _mapHttpResponseForErrorFindigSummaries(final SecurityContext securityContext,
												    			   			final Url restResourceUrl,final HttpResponse httpResponse) {
		FindSummariesError<M> outOpError = null;
		
		// [0] - Load the http response text
		String responseStr = httpResponse.loadAsString();
		if (Strings.isNullOrEmpty(responseStr)) throw new ServiceProxyException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		
		// [1] - Server error (the request could NOT be processed)
		if (httpResponse.isServerError()) {
			outOpError = FindSummariesResultBuilder.using(securityContext)
												   .on(_modelObjectType)
												   .errorFindingSummaries()
												  	  		.causedBy(responseStr);
		}
		// [2] - Error while request processing: the PersistenceCRUDError comes INSIDE the response
		else {
			outOpError = FindSummariesResultBuilder.using(securityContext)
												   .on(_modelObjectType)
												   .errorFindingSummaries()
												  	  		.causedBy(responseStr);
		}
		// [4] - Return the CRUDOperationResult
		return outOpError;
	}
}
