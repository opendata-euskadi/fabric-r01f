package r01f.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import r01f.util.types.Strings;
import lombok.extern.slf4j.Slf4j;



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
@Provider
@RESTLogFilter
public class RESTLogRequestFilter
  implements ContainerRequestFilter,
  			 RESTFilter {
	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		String baseUri = requestContext.getUriInfo().getBaseUri().toString();
		String method = requestContext.getMethod();
		String path = requestContext.getUriInfo().getAbsolutePath().toString();
		MediaType mediaType = requestContext.getMediaType();
		String dbg = Strings.customized("\r\n"
										+"\r\nLog FILTER RESTEndPoint : {}"  +
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
		log.warn(dbg);
	}
}
