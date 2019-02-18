package r01f.types;

/**
 * Param value encoder / decoder interface
 */
public interface ParamValueEncoderDecoder {
	public String encodeValue(final String value);
	public String decodeValue(final String value);
}
