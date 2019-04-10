package r01f.services.client.servicesproxy.rest;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpRequestPayload;
import r01f.httpclient.HttpResponse;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.index.IndexManagementCommand;
import r01f.securitycontext.SecurityContext;
import r01f.services.ServiceProxyException;
import r01f.types.jobs.EnqueuedJob;
import r01f.types.url.Url;
import r01f.util.types.Strings;

@Slf4j
public class DelegateForRawRESTIndexManagement 
     extends DelegateForRawREST {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public DelegateForRawRESTIndexManagement(final Marshaller marshaller) {
		super(marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public EnqueuedJob doIndexManagementCommand(final Url restResourceUrl,
							   	  				final SecurityContext securityContext,
							   	  				final IndexManagementCommand indexCmd) {
		log.trace("\t\tINDEX resource: {}",restResourceUrl);
		
		// [1] - Serialize params
		String securityContextXml = securityContext != null ? _marshaller.forWriting().toXml(securityContext) : null;
		String dataXml = _marshaller.forWriting().toXml(indexCmd);
		
		// [2] - Do http request
		HttpResponse httpResponse = null;
		try {
			// index some records
			if (indexCmd != null) {
				if (Strings.isNOTNullOrEmpty(securityContextXml)) {
					httpResponse = HttpClient.forUrl(restResourceUrl)		
								             .withHeader("securityContext",securityContextXml)								             
								             .PUT()
								             	.withPayload(HttpRequestPayload.wrap(dataXml)
								             								   .mimeType(MimeTypes.APPLICATION_XML))
											 .getResponse()
											 	.directNoAuthConnected();
				} else {
					httpResponse = HttpClient.forUrl(restResourceUrl)		
								             .PUT()
								             	.withPayload(HttpRequestPayload.wrap(dataXml)
								             								   .mimeType(MimeTypes.APPLICATION_XML))
											 .getResponse()
											 	.directNoAuthConnected();
				}
			}
			else {
				throw new IllegalArgumentException(Throwables.message("The index resource {} is NOT valid",restResourceUrl));
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
