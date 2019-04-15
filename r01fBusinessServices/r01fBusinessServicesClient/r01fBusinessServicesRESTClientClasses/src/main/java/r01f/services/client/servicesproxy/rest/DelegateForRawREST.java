package r01f.services.client.servicesproxy.rest;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpRequestHeader;
import r01f.httpclient.HttpRequestPayload;
import r01f.httpclient.HttpResponse;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.services.ServiceProxyException;
import r01f.types.url.Url;
import r01f.util.types.Strings;

@Slf4j
public abstract class DelegateForRawREST {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Objects to xml/json marshaller
	 */
	protected final Marshaller _marshaller;
	/**
	 * Maps from the REST Response to the returned object
	 */
	protected final RESTResponseToResultMapper _responseToResultMapper;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DelegateForRawREST(final Marshaller marshaller) {
		_marshaller = marshaller;
		_responseToResultMapper = new RESTResponseToResultMapper(marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected RESTResponseToResultMapper getResponseToResultMapper() {
		return _responseToResultMapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpResponse GET(final Url restResourceUrl,
								   final String securityContextXml) {
		log.trace("\t\tGET resource: {}",restResourceUrl);
			
		HttpResponse outHttpResponse = null;
		try {
			outHttpResponse = HttpClient.forUrl(restResourceUrl)
									    .withHeader("securityContext",securityContextXml)
									    .GET()
									  	.getResponse()
									  		.directNoAuthConnected();
		} catch(IOException ioEx) {
			log.error("Error while GETing {}: {}",restResourceUrl,ioEx.getMessage());
			throw new ServiceProxyException(ioEx);
		}
		return outHttpResponse;
	}
	public static HttpResponse POST(final Url restResourceUrl,
									final String securityContextXml,
							     	final String entityXml,
							     	final HttpRequestHeader... headers) {
		log.trace("\t\tPOST resource: {}",restResourceUrl);
		HttpResponse outHttpResponse = null;
		try {
			if (Strings.isNOTNullOrEmpty(entityXml)) {
				outHttpResponse = HttpClient.forUrl(restResourceUrl)		
										    .withHeader("securityContext",securityContextXml)
											.withHeaders(headers)		// any additional header
										    .POST()
									      		.withPayload(HttpRequestPayload.wrap(entityXml)
																			   .mimeType(MimeTypes.APPLICATION_XML))
										    .getResponse()
										    	.directNoAuthConnected();
			} else {
				outHttpResponse = HttpClient.forUrl(restResourceUrl)		
								   	        .withHeader("securityContext",securityContextXml)
											.withHeaders(headers)		// any additional header
										    .POST()
										 		.withoutPayload(MimeTypes.APPLICATION_XML)
										    .getResponse()
										    	.directNoAuthConnected();
			}
		} catch(IOException ioEx) {
			log.error("Error while POSTing to {}: {}",restResourceUrl,ioEx.getMessage());
			throw new ServiceProxyException(ioEx);
		}				
		return outHttpResponse;
	}
	public static HttpResponse PUT(final Url restResourceUrl,
								   final String securityContextXml,
				 			  	   final String entityXml,
				 			  	   final HttpRequestHeader... headers) {
		log.trace("\t\tPUT resource: {}",restResourceUrl);
		HttpResponse outHttpResponse = null;
		try {
			if (entityXml != null) {
				outHttpResponse = HttpClient.forUrl(restResourceUrl)		
											.withHeader("securityContext",securityContextXml)
											.withHeaders(headers)			// any additional header
											.PUT()
	
											.withPayload(HttpRequestPayload.wrap(entityXml)
																			   .mimeType(MimeTypes.APPLICATION_XML))
											.getResponse()
												.directNoAuthConnected();
			} else {
				outHttpResponse = HttpClient.forUrl(restResourceUrl)		
											.withHeader("securityContext",securityContextXml)
											.withHeaders(headers)			// any additional header
											.PUT()
												.withoutPayload(MimeTypes.APPLICATION_XML)
											.getResponse()
												.directNoAuthConnected();
			}
		} catch(IOException ioEx) {
			log.error("Error while PUTing to {}: {}",restResourceUrl,ioEx.getMessage());
			throw new ServiceProxyException(ioEx);
		}		
		return outHttpResponse;
	}
	public static HttpResponse DELETE(final Url restResourceUrl,
									  final String securityContextXml) {
		log.trace("\t\tDELETE resource: {}",restResourceUrl);
		HttpResponse outHttpResponse = null;
		try {
			outHttpResponse = HttpClient.forUrl(restResourceUrl)
										.withHeader("securityContext",securityContextXml)
										.DELETE()
										.getResponse()	
											.directNoAuthConnected();
		} catch(IOException ioEx) {
			log.error("Error while DELETEing {}: {}",restResourceUrl,ioEx.getMessage());
			throw new ServiceProxyException(ioEx);
		}
		return outHttpResponse;	
	}
}
