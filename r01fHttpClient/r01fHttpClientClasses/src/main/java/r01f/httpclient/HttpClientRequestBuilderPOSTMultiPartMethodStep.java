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
import r01f.mime.MimeType;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

public class HttpClientRequestBuilderPOSTMultiPartMethodStep
	 extends HttpClientRequestBuilderForMethodBase<HttpClientRequestBuilderPOSTMultiPartMethodStep> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private List<HttpRequestFormParameter> _formParameters;  	// params to be set to the server if GET o POST-FORM-URL-ENCODED

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderPOSTMultiPartMethodStep(final Url targetUrl,
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
	 * Establece un parametro de la llamada
	 * @param param el parametro
	 */
	public HttpClientRequestBuilderPOSTMultiPartMethodStep withPOSTFormParameter(final HttpRequestFormParameter param) {
		if (_formParameters == null) _formParameters = new ArrayList<HttpRequestFormParameter>();
		_formParameters.add(param);
		return this;
	}
	/**
	 * Establece los parametros de la llamada
	 * @param params parametros
	 */
	public HttpClientRequestBuilderPOSTMultiPartMethodStep withPOSTFormParameters(final List<HttpRequestFormParameter> params) {
		_formParameters = params;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Writes the POSTed form into the body of the request sent to the server
//	See http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4
//
// IMPORTANT!!!		The x-www-form-urlencoded enctype is handled by HttpRequestFluentStatementForPOSTFormURLEncodedMethod type
//
// 	If the form is like:
//		<FORM action="http://server.com/cgi/handle"
//            enctype="multipart/form-data"
//            method="post">
//   				What is your name? 			<INPUT type="text" name="user_name">
//					What is your surname? 		<INPUT type="text" name="user_surname">
//   				What files are you sending? <INPUT type="file" name="selected_files">
//   		<INPUT type="submit" value="Send">
//			<INPUT type="reset">
// 		</FORM>
//
// If the user enters "Larry" and "Page" in the text inputs and selects only one file, the user agent might send back the following data:
//
//   	HEADER: Content-Type: multipart/form-data; boundary=AaB03x
//
//   	--AaB03x
//   	Content-Disposition: form-data; name="user_name"
//
//   	Larry
//   	--AaB03x
//   	Content-Disposition: form-data; name="user_surname"
//
//   	Page
//   	--AaB03x
//   	Content-Disposition: form-data; name="selected_files"; filename="file1.txt"
//   	Content-Type: text/plain
//
//   	... contents of file1.txt ...
//  	 --AaB03x--
//
//
// If the user enters "Larry" and "Page" in the text inputs, and selects two files "file1.txt" & "file2.gif", the user agent might send back the following data:
//
//		HEADER: Content-Type: multipart/form-data; boundary=AaB03x
//
//	   --AaB03x
//  	Content-Disposition: form-data; name="user_name"
//
//  	Larry
//   	--AaB03x
//   	Content-Disposition: form-data; name="user_surname"
//
//   	Page
//  	--AaB03x
//  	Content-Disposition: form-data; name="selected_files"
//  	Content-Type: multipart/mixed; boundary=BbC04y
//
//  	--BbC04y
//  	Content-Disposition: file; filename="file1.txt"
//  	Content-Type: text/plain
//
//  	... contents of file1.txt ...
//  	--BbC04y
//  	Content-Disposition: file; filename="file2.gif"
//  	Content-Type: image/gif
//  	Content-Transfer-Encoding: binary
//
//  	...contents of file2.gif...
//  	--BbC04y--
//  	--AaB03x--
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public HttpRequestPayloadWrapper getPayloadWrapper() {
		return new HttpRequestPayloadWrapper(_payload) {
						@Override
						public MimeType payloadContentType() {
							return new MimeType("multipart/form-data; boundary=**R01BOUNDR01**");
						}
						@Override
						public void payloadToOutputStream(final DataOutputStream dos) throws IOException {
							// Binary params
							final Collection<HttpRequestFormParameter> binaryParams = Collections2.filter(_formParameters,
																									Predicates.instanceOf(HttpRequestFormParameterForMultiPartBinaryData.class));
							// Text params
							final Collection<HttpRequestFormParameter> textParams = Collections2.filter(_formParameters,
																								  Predicates.instanceOf(HttpRequestFormParameterForText.class));
					
							// Send params
							// Each param has the representation in the POST payload
							//		--boundary_start
							//		Content-Disposition: form-data; name="param_name"
							//
							//		param_value
							if (CollectionUtils.hasData(textParams)) {
								for (final HttpRequestFormParameter param : textParams) {
									dos.write("--**R01BOUNDR01**\r\n".getBytes());
									dos.write(param._serializeFormParam(_targetServerCharset,
																		true));
								}
							}
							// Send the binary params in the second place; be carefull if there are more than one file
							if (CollectionUtils.hasData(binaryParams)) {
								for (final HttpRequestFormParameter param : binaryParams) {
									dos.write("--**R01BOUNDR01**\r\n".getBytes());
									dos.write(param._serializeFormParam(_targetServerCharset,
																		true));
								}
							}
							// Finish the multipart
							dos.write("--**R01BOUNDR01**--\r\n".getBytes());
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
