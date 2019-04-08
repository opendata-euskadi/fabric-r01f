package r01f.httpclient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.types.url.Url;

@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
abstract class HttpClientRequestBuilderBase<SELF_TYPE extends HttpClientRequestBuilderBase<SELF_TYPE>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Url _targetUrl;    	// Destination url
}
