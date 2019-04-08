package r01f.httpclient;

import java.nio.charset.Charset;
import java.util.Map;

import r01f.httpclient.HttpClient.RequestMethod;
import r01f.types.url.Url;

public class HttpClientRequestBuilderDELETEMethodStep
	 extends HttpClientRequestBuilderForMethodBase<HttpClientRequestBuilderDELETEMethodStep> {
///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderDELETEMethodStep(final Url targetUrl,
											  final Charset targetServerCharset,
						  				      final Map<String,String> newRequestHeaders,final Map<String,String> newRequestCookies) {
		super(RequestMethod.DELETE,
			  targetUrl,
			  targetServerCharset,
			  newRequestHeaders,newRequestCookies);
	}
}
