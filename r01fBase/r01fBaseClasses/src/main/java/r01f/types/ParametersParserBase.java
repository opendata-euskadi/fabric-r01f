package r01f.types;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

/**
 * Default parameters parser using regex
 */
abstract class ParametersParserBase 
    implements ParametersParser {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public ParamValueEncoderDecoder getParamValueEncoderDecoder() {
		return new ParamValueEncoderDecoder() {
					@Override
					public String encodeValue(final String value) {
						return _urlEncodeNoThrow(value).toString();
					}
					@Override
					public String decodeValue(final String value) {
						return _urlEncodeNoThrow(value).toString();
					}
			   };
	}
	/**
	 * Encodes in a www-form-urlencoded format using the default charset
	 * it does NOT throw any exception if the encoding cannot be done
	 * @param str
	 * @return
	 */
	private static CharSequence _urlEncodeNoThrow(final CharSequence str) {
		if (str == null) return null;
		try {
			URLCodec codec = new URLCodec();
			return codec.encode(str.toString());
		} catch (EncoderException encEx) {
			encEx.printStackTrace(System.out);
		}
		return str;		// at least return the original string			
	}
}
