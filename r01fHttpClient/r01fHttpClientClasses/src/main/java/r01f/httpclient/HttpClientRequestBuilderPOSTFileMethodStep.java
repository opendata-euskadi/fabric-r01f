package r01f.httpclient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

import r01f.httpclient.HttpClient.RequestMethod;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

public class HttpClientRequestBuilderPOSTFileMethodStep
	 extends HttpClientRequestBuilderForMethodBase<HttpClientRequestBuilderPOSTFileMethodStep> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private List<HttpRequestFormParameter> _formParameters;  	// Parametros a enviar al servidor en el caso de utilizar GET o POST-FORM-URL-ENCODED

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderPOSTFileMethodStep(final Url targetUrl,
												final Charset targetServerCharset,
						  				        final Map<String,String> newRequestHeaders,final Map<String,String> newRequestCookies) {
		super(RequestMethod.POST_FORM_URL_ENCODED,
			  targetUrl,
			  targetServerCharset,	
			  newRequestHeaders,newRequestCookies);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  API
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets a param
	 * @param param 
	 */
	public HttpClientRequestBuilderPOSTFileMethodStep withPOSTFormParameter(final HttpRequestFormParameter param) {
		if (_formParameters == null) _formParameters = new ArrayList<HttpRequestFormParameter>();
		_formParameters.add(param);
		return this;
	}
	/**
	 * Sets params
	 * @param param
	 */
	public HttpClientRequestBuilderPOSTFileMethodStep withPOSTFormParameters(final List<HttpRequestFormParameter> params) {
		_formParameters = params;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Writes the POSTed form into the body of the request sent to the server
//	See http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4
//
//	Send a file with header content-type
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public HttpRequestPayloadWrapper getPayloadWrapper() {
		return new HttpRequestPayloadWrapper(_payload) {
						@Override
						public void payloadToOutputStream(final DataOutputStream dos) throws IOException {
							// Binary params
							final Collection<HttpRequestFormParameter> binaryParams = Collections2.filter(_formParameters,
																									      Predicates.instanceOf(HttpRequestFormParameterForMultiPartBinaryData.class));
							// Text params
							final Collection<HttpRequestFormParameter> textParams = Collections2.filter(_formParameters,
																								  		Predicates.instanceOf(HttpRequestFormParameterForText.class));
					
							// Send params
							if (CollectionUtils.hasData(textParams)) {
								for (final HttpRequestFormParameter param : textParams) {
									dos.write(param._serializeFormParam(_targetServerCharset,
																		true));
								}
							}
							// Send the binary params in the second place; be carefull if there are more than one file
							if (CollectionUtils.hasData(binaryParams)) {
								for (final HttpRequestFormParameter param : binaryParams) {
									dos.write(param._serializeFormParam(_targetServerCharset,
																		true));
								}
							}
					 	}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void debug(final DataOutputStream dos) throws IOException {
			this.getPayloadWrapper()
					.payloadToOutputStream(dos);
	}
}
