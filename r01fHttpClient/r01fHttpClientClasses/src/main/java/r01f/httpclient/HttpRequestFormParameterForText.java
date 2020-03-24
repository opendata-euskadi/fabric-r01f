package r01f.httpclient;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.codec.EncoderException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.util.types.StringEncodeUtils;
import r01f.util.types.Strings;

/**
 * Creates a param to be send to the server using a POSTed form which could be:
 * <ol>
 * 		<li>a param in a form-url-encoded post</li>
 * 		<li>a form param in a multi-part post</li>
 * </ol>
 * The creation of a param is like:
 * <pre class='brush:java'>
 * 		HttpRequestFormParameterForText param = HttpRequestFormParameterForText.of("my param value")
 * 																	     	   .withName("myParam");
 * </pre>
 */
@Accessors(prefix="_")
@AllArgsConstructor
public class HttpRequestFormParameterForText
  implements HttpRequestFormParameter {

/////////////////////////////////////////////////////////////////////////////////////////
//  STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final String _name;
	@Getter private final String _value;

/////////////////////////////////////////////////////////////////////////////////////////
//   BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpRequestFormParameterForTextBuilderNameStep of(final String s) {
		return new HttpRequestFormParameterForTextBuilderNameStep(s);
	}
	public static HttpRequestFormParameterForTextBuilderNameStep of(final int i) {
		return new HttpRequestFormParameterForTextBuilderNameStep(Integer.toString(i));
	}
	public static HttpRequestFormParameterForTextBuilderNameStep of(final long l) {
		return new HttpRequestFormParameterForTextBuilderNameStep(Long.toString(l));
	}
	public static HttpRequestFormParameterForTextBuilderNameStep of(final double d) {
		return new HttpRequestFormParameterForTextBuilderNameStep(Double.toString(d));
	}
	public static HttpRequestFormParameterForTextBuilderNameStep of(final float f) {
		return new HttpRequestFormParameterForTextBuilderNameStep(Float.toString(f));
	}
	public static HttpRequestFormParameterForTextBuilderNameStep of(final boolean b) {
		return new HttpRequestFormParameterForTextBuilderNameStep(Boolean.toString(b));
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class HttpRequestFormParameterForTextBuilderNameStep {
		final String _value;
		public HttpRequestFormParameterForText withName(final String name) {
			return new HttpRequestFormParameterForText(name,_value); 
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public byte[] _serializeFormParam(final Charset targetServerCharset,
									  final boolean multiPart) throws IOException {

		Charset theTargetServerCharset = targetServerCharset == null ? Charset.defaultCharset()
																	 : targetServerCharset;
		try {
			byte[] outSerializedParamBytes = null;
			if (multiPart) {
				// Content-Disposition: form-data; name="param-name"
				//
				// Param-value
				outSerializedParamBytes = Strings.customized("Content-Disposition: form-data; name=\"{}\"\r\n" +
													 		 "\r\n" + //mandatory newline, header and value must be separated.
													 		 "{}\r\n",
													 		 _name,
														   	 _value)
												 .getBytes(theTargetServerCharset);
			} else {
				outSerializedParamBytes = Strings.customized("{}={}",
										         			StringEncodeUtils.urlEncode(_name),
										    			    StringEncodeUtils.urlEncode(_value))
										    	 .getBytes(theTargetServerCharset);
			}
			return outSerializedParamBytes != null ? outSerializedParamBytes
											  	   : null;
		} catch (EncoderException encEx) {
			throw new IOException(encEx);
		}
	}
}
