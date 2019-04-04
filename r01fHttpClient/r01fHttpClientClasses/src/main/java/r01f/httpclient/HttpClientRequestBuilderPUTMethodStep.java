package r01f.httpclient;

import java.nio.charset.Charset;
import java.util.Map;

import r01f.httpclient.HttpClient.RequestMethod;
import r01f.types.url.Url;

public class HttpClientRequestBuilderPUTMethodStep
	 extends HttpClientRequestBuilderForMethodBase<HttpClientRequestBuilderPUTMethodStep> {
	
///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderPUTMethodStep(final Url targetUrl,
										   final Charset targetServerCharset,
						  				   final Map<String,String> newRequestHeaders,final Map<String,String> newRequestCookies) {
		super(RequestMethod.PUT,
			  targetUrl,
			  targetServerCharset,
			  newRequestHeaders,newRequestCookies);
	}
}
