package r01f.rest;

import javax.ws.rs.core.MediaType;

import r01f.util.types.Strings;
import lombok.extern.slf4j.Slf4j;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * A REST endpoint request filter that logs
 * To configure the filter put it at the web.xml:
 * <pre class='brush:xml'>
 * 		<init-param>
 *	       	<param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
 *	       	<param-value>r01f.rest.RESTLogRequestFilter</param-value>
 *	   	</init-param>
 * </pre>
 */
@Slf4j
public class RESTLogRequestFilter 
  implements ContainerRequestFilter {
	
	@Override
	public ContainerRequest filter(final ContainerRequest req) {
		
		if (log.isTraceEnabled()) {
			String baseUri = req.getBaseUri().getPath();
			String method = req.getMethod();
			String path = req.getPath();
			MediaType mediaType = req.getMediaType();
			
			String dbg = Strings.customized("\r\nRESTEndPoint: {}"  +														
					  						"\r\n---------------------------------------------------------------------"  +				
					  						"\r\n       Method: {}"  +
					  						"\r\n        Path: {}" + 
					  						"\r\nContent-Type: {}" +
					  					"\r\n---------------------------------------------------------------------",
					  					baseUri,
					  					method,
					  					path,
					  					mediaType != null ? mediaType
					  						   			  : "not specified");
			log.trace(dbg);
		}
		return req;
	}

}
