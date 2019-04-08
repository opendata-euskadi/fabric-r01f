package r01f.encoding;


/**
 * Interface to be implemented by types in charge of coding/decoding text
 */
public interface TextEncoder {
	/**
	 * Encodes text
	 * @param theText the text to be encoded
	 * @return the encoded text
	 */
	public CharSequence encode(CharSequence theText);
	/**
	 * Decodes text
	 * @param theText the text to be decoded
	 * @return the decoded text
	 */
	public CharSequence decode(CharSequence theText);
}
