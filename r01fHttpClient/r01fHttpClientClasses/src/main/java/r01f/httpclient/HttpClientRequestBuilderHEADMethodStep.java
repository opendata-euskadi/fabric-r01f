package r01f.httpclient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import r01f.httpclient.HttpClient.RequestMethod;
import r01f.mime.MimeType;
import r01f.types.url.Url;

public class HttpClientRequestBuilderHEADMethodStep
	 extends HttpClientRequestBuilderForMethodBase<HttpClientRequestBuilderHEADMethodStep> {
///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderHEADMethodStep(final Url targetUrl,
											final Charset targetServerCharset,
						  				    final Map<String,String> newRequestHeaders,final Map<String,String> newRequestCookies) {
		super(RequestMethod.HEAD,
			  targetUrl,
			  targetServerCharset,
			  newRequestHeaders,newRequestCookies);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PAYLOAD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public HttpRequestPayloadWrapper getPayloadWrapper() {
		return new HttpRequestPayloadWrapper(_payload) {
						@Override
						public MimeType payloadContentType() {
							return null;
						}
						@Override
						public void payloadToOutputStream(final DataOutputStream dos) throws IOException {
							throw new IOException("A HEAD HTTP call cannot have payload!");
						}
			   };
	}
}
