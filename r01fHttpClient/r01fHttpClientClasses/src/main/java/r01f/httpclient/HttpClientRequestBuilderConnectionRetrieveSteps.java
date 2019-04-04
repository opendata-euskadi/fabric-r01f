package r01f.httpclient;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HttpClientRequestBuilderConnectionRetrieveSteps {
///////////////////////////////////////////////////////////////////////////////
// 	HttpUrlConnection
///////////////////////////////////////////////////////////////////////////////
	public static class HttpClientRequestBuilderConnectionRetrieveForHttpUrlConnectionStep
		 		extends HttpClientRequestBuilderConnectionRetrieveStepBase<HttpURLConnection> {
		HttpClientRequestBuilderConnectionRetrieveForHttpUrlConnectionStep(final HttpClientRequestConnectionProvider<HttpURLConnection> httpUrlConnectionProvider) {
			super(httpUrlConnectionProvider);
		}
	}
///////////////////////////////////////////////////////////////////////////////
// 	HttpResponse
///////////////////////////////////////////////////////////////////////////////
	public static class HttpClientRequestBuilderConnectionRetrieveForResponseStep
		 extends HttpClientRequestBuilderConnectionRetrieveStepBase<HttpResponse> {
		HttpClientRequestBuilderConnectionRetrieveForResponseStep(final HttpClientRequestConnectionProvider<HttpResponse> responseProvider) {
			super(responseProvider);
		}
	}
///////////////////////////////////////////////////////////////////////////////
// InputStream
///////////////////////////////////////////////////////////////////////////////
	public static class HttpClientRequestBuilderConnectionRetrieveForInputStreamStep
		        extends HttpClientRequestBuilderConnectionRetrieveStepBase<InputStream> {
	 
		HttpClientRequestBuilderConnectionRetrieveForInputStreamStep(final HttpClientRequestConnectionProvider<InputStream> linesProvider) {
			super(linesProvider);
		}
	}
///////////////////////////////////////////////////////////////////////////////
// 	String
///////////////////////////////////////////////////////////////////////////////
	public static class HttpClientRequestBuilderConnectionRetrieveForStringStep
		 	    extends HttpClientRequestBuilderConnectionRetrieveStepBase<String> {
	
		HttpClientRequestBuilderConnectionRetrieveForStringStep(final HttpClientRequestConnectionProvider<String> stringProvider) {
			super(stringProvider);
		}
	}
///////////////////////////////////////////////////////////////////////////////
// 	Lines
///////////////////////////////////////////////////////////////////////////////
	public static class HttpClientRequestBuilderConnectionRetrieveForLinesStep
		 	    extends HttpClientRequestBuilderConnectionRetrieveStepBase<Collection<String>> {
		HttpClientRequestBuilderConnectionRetrieveForLinesStep(final HttpClientRequestConnectionProvider<Collection<String>> linesProvider) {
			super(linesProvider);
		}
	}
}
