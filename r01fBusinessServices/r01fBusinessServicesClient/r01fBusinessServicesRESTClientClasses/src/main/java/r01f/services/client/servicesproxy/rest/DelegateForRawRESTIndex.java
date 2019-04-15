package r01f.services.client.servicesproxy.rest;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpRequestPayload;
import r01f.httpclient.HttpResponse;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.ServiceProxyException;
import r01f.types.jobs.EnqueuedJob;
import r01f.types.url.Url;

@Slf4j
public class DelegateForRawRESTIndex 
     extends DelegateForRawREST {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public DelegateForRawRESTIndex(final Marshaller marshaller) {
		super(marshaller);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  INDEX
/////////////////////////////////////////////////////////////////////////////////////////
	public EnqueuedJob index(final Url restResourceUrl,
						     final SecurityContext securityContext,
 							 final Object data) {
		log.trace("\t\tINDEX resource: {}",restResourceUrl);
		
		// [1] - Serialize params
		String securityContextXml = _marshaller.forWriting().toXml(securityContext);
		String dataXml = _marshaller.forWriting().toXml(data);
		
		// [2] - Do http request
		HttpResponse httpResponse = null;
		try {
			// index some records
			if (data != null) {
				httpResponse = HttpClient.forUrl(restResourceUrl)		
							             .withHeader("securityContext",securityContextXml)
							             .POST()
							             	.withPayload(HttpRequestPayload.wrap(dataXml)
							             								   .mimeType(MimeTypes.APPLICATION_XML))
										 .getResponse()
										 	.directNoAuthConnected();
			}
			// index all records
			else {
				httpResponse = HttpClient.forUrl(restResourceUrl)		
							             .withHeader("securityContext",securityContextXml)
							             .POST()
							             	.withoutPayload(MimeTypes.APPLICATION_XML)
										 .getResponse()
										 	.directNoAuthConnected();
			}
		} catch(IOException ioEx) {
			throw new ServiceProxyException(ioEx);
		}
		
		// [3] - De-serialize response
		EnqueuedJob outJob = this.mapHttpResponseForEnqueuedJob(securityContext,
																restResourceUrl,
																httpResponse);
		return outJob;
	}
	public EnqueuedJob updateIndex(final Url restResourceUrl,
							   	   final SecurityContext securityContext,
							   	   final Object data) {
		log.trace("\t\tINDEX resource: {}",restResourceUrl);
		
		// [1] - Serialize params
		String securityContextXml = _marshaller.forWriting().toXml(securityContext);
		String dataXml = _marshaller.forWriting().toXml(data);
		
		// [2] - Do http request
		HttpResponse httpResponse = null;
		try {
			// index some records
			if (data != null) {
				httpResponse = HttpClient.forUrl(restResourceUrl)		
							             .withHeader("securityContext",securityContextXml)
							             .PUT()
							             	.withPayload(HttpRequestPayload.wrap(dataXml)
							             								   .mimeType(MimeTypes.APPLICATION_XML))
										 .getResponse()
										 	.directNoAuthConnected();
			}
			// index all records
			else {
				httpResponse = HttpClient.forUrl(restResourceUrl)		
							             .withHeader("securityContext",securityContextXml)
							             .PUT()
							             	.withoutPayload(MimeTypes.APPLICATION_XML)
										 .getResponse()
										 	.directNoAuthConnected();
			}
		} catch(IOException ioEx) {
			throw new ServiceProxyException(ioEx);
		}
		
		// [3] - De-serialize response
		EnqueuedJob outJob = this.mapHttpResponseForEnqueuedJob(securityContext,
																restResourceUrl,
																httpResponse);
		return outJob;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UNINDEX
/////////////////////////////////////////////////////////////////////////////////////////
	public EnqueuedJob removeFromIndex(final Url restResourceUrl,
							   		   final SecurityContext securityContext,
							   		   final Object data) {
		log.trace("\t\tUN INDEX resource: {}",restResourceUrl);
		
		// [1] - Serialize params
		String securityContextXml = _marshaller.forWriting().toXml(securityContext);
		String dataXml = data != null ? _marshaller.forWriting().toXml(data) 
									  : null;
		
		// [2] - Do http request
		HttpResponse httpResponse = null;
		try {
			// index some records
			if (data != null) {
				httpResponse = HttpClient.forUrl(restResourceUrl)		
							             .withHeader("securityContext",securityContextXml)
							             .DELETE()
							             		.withPayload(HttpRequestPayload.wrap(dataXml)
							             									   .mimeType(MimeTypes.APPLICATION_XML))
										 .getResponse()
										 	.directNoAuthConnected();
			}
			// index all records
			else {
				httpResponse = HttpClient.forUrl(restResourceUrl)		
							             .withHeader("securityContext",securityContextXml)
							             .DELETE()
							             		.withoutPayload(MimeTypes.APPLICATION_XML)
										 .getResponse()
										 	.directNoAuthConnected();
			}
		} catch(IOException ioEx) {
			throw new ServiceProxyException(ioEx);
		}
		
		// [3] - De-serialize response
		EnqueuedJob outJob = this.mapHttpResponseForEnqueuedJob(securityContext,
																restResourceUrl,
																httpResponse);
		return outJob;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public EnqueuedJob mapHttpResponseForEnqueuedJob(final SecurityContext securityContext,
													 final Url restResourceUrl,
													 final HttpResponse httpResponse) {
		EnqueuedJob outJob = this.getResponseToResultMapper()
										.mapHttpResponse(securityContext,
												  		 restResourceUrl,
												  		 httpResponse,
												  		 EnqueuedJob.class);
		return outJob;
	}
}
