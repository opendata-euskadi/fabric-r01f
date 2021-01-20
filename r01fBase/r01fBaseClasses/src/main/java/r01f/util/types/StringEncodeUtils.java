package r01f.util.types;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.net.URLCodec;

import com.google.common.base.Predicate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class StringEncodeUtils {
///////////////////////////////////////////////////////////////////////////////
//  CODE
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes a {@link String}
	 * @param str the string to be encoded
	 * @param encoding the encoding
	 * @return the provided {@link String} encoded
	 */
	public static CharSequence encode(final CharSequence str,final Charset encoding) {
		if (str == null) return null;
		Charset theEncoding = encoding != null ? encoding : Charset.defaultCharset();
		// Encode
		CharBuffer cb = CharBuffer.wrap(str);
		ByteBuffer bb = theEncoding.encode(cb);

		// Transform to a CharBuffer in order to get an CharSequence
		CharBuffer outCb = theEncoding.decode(bb);
		return outCb;
	}
	/**
	 * Transcodes a byte array
	 * @param bytes
	 * @param srcCharset
	 * @param dstCharset
	 * @return
	 * @throws CharacterCodingException
	 */
	public static byte[] transcode(final byte[] bytes,
								   final Charset srcCharset,final Charset dstCharset) throws CharacterCodingException {

		// Decode from srcCharset
		CharsetDecoder decoderSrc = srcCharset.newDecoder();
		decoderSrc.onMalformedInput(CodingErrorAction.IGNORE)
				  .onUnmappableCharacter(CodingErrorAction.IGNORE);
		ByteBuffer bbufSrc = ByteBuffer.wrap(bytes);
		CharBuffer cbufSrc = decoderSrc.decode(bbufSrc);

		// Encode to dstCharset
		CharsetEncoder encoderDst = dstCharset.newEncoder();
		encoderDst.onMalformedInput(CodingErrorAction.IGNORE)
				  .onUnmappableCharacter(CodingErrorAction.IGNORE);
		ByteBuffer bbufDst = encoderDst.encode(cbufSrc);
		byte[] bytesInDstCharset = bbufDst.array();

		// Return
		return bytesInDstCharset;
	}
	/**
	 * UTF-8 Encodes an {@link String}
	 * @param str
	 * @return the UTF-8 encoded provided {@link String}
	 */
	public static CharSequence encodeUTF(final CharSequence str) {
		return StringEncodeUtils.encode(str,Charset.forName("UTF-8"));
	}
	/**
	 * ISO-8859- encodes an {@link String}
	 * @param str
	 * @return the ISO-8859-1 encoded provided {@link String}
	 */
	public static CharSequence encodeISO8859(final CharSequence str) {
		return StringEncodeUtils.encode(str,Charset.forName("ISO-8859-1"));
	}
	/**
	 * According to http://en.wikipedia.org/wiki/Windows-1252 there are 27 NON-standard characters
	 * used by Microsoft's Windows-1252 character set that is supposed to be an ISO-8859-1 super-set
	 * This method replaces those characters with other from the ISO-8859-1 character set while other characters are removed
	 * References:
	 *	   http://en.wikipedia.org/wiki/Windows-1252
	 *	   http://www.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WindowsBestFit/bestfit1252.txt
	 *	   http://download-llnw.oracle.com/javase/tutorial/i18n/text/convertintro.html
	 * @param str the {@link String} to be converted
	 */
	public static CharSequence windows1252ToIso8859(final CharSequence str)  {
	   if (null == str) return null;
	   StringBuilder modif = new StringBuilder(str);	// Create an original string copy that will be modified
	   for (int i = 0; i < str.length(); i++)  {
		  int origCharAsInt = str.charAt(i);

		  switch (origCharAsInt) {
		  // replaced characters
		  case ('\u2018'):  modif.setCharAt(i,'\''); break;  // left single quote
		  case ('\u2019'):  modif.setCharAt(i,'\''); break;  // right single quote
		  case ('\u201A'):  modif.setCharAt(i,'\''); break;  // lower quotation mark

		  case ('\u201C'):  modif.setCharAt(i,'"'); break;  // left double quote
		  case ('\u201D'):  modif.setCharAt(i,'"'); break;  // right double quote
		  case ('\u201E'):  modif.setCharAt(i,'"'); break;  // double low quotation mark

		  case ('\u2039'):  modif.setCharAt(i,'\''); break;  // Single Left-Pointing Quotation Mark
		  case ('\u203A'):  modif.setCharAt(i,'\''); break;  // Single right-Pointing Quotation Mark

		  case ('\u02DC'):  modif.setCharAt(i,'~'); break;  // Small Tilde

		  case ('\u2013'):  modif.setCharAt(i,'-'); break;  // En Dash
		  case ('\u2014'):  modif.setCharAt(i,'-'); break;  // EM Dash

		  // Removed characters
		  case ('\u0178'):  modif.deleteCharAt(i); break;
		  case ('\u017E'):  modif.deleteCharAt(i); break;
		  case ('\u0153'):  modif.deleteCharAt(i); break;
		  case ('\u0161'):  modif.deleteCharAt(i); break;
		  case ('\u2122'):  modif.deleteCharAt(i); break;
		  case ('\u2022'):  modif.deleteCharAt(i); break;
		  case ('\u017D'):  modif.deleteCharAt(i); break;
		  case ('\u0152'):  modif.deleteCharAt(i); break;
		  case ('\u0160'):  modif.deleteCharAt(i); break;
		  case ('\u2030'):  modif.deleteCharAt(i); break;
		  case ('\u02C6'):  modif.deleteCharAt(i); break;
		  case ('\u2021'):  modif.deleteCharAt(i); break;
		  case ('\u2020'):  modif.deleteCharAt(i); break;
		  case ('\u2026'):  modif.deleteCharAt(i); break;
		  case ('\u0192'):  modif.deleteCharAt(i); break;
		  case ('\u20AC'):  modif.deleteCharAt(i); break;

		  // by default the character remains the same
		  default: modif.setCharAt(i,str.charAt(i)); 	break;
		  }
	   }
	   return modif;
	}
	/**
	 * Filters the input {@link String} to replace any occurrence of the characters of the provided array that are
	 * replaced by the chars also provided in the other array
	 * This method is useful specially to filter forbidden characters as '<' or '>' in HTML {@link String} that are
	 * replaced by '&gt;' y '&lt;'
	 * @param strToBeFiltered the input {@link String} some of whose characters are to be replaced
	 * @param charsToFilter the characters to be filtered
	 * @param charsFiltered the filtered characters substitutions
	 * @return
	 */
	public static CharSequence filterAndReplaceChars(final CharSequence strToBeFiltered,
													 final char[] charsToFilter,final String[] charsFiltered) {
		if (strToBeFiltered == null) return null;

		// Copy the input String to a char[]
		char content[] = new char[strToBeFiltered.length()];
		for (int i = 0; i < strToBeFiltered.length(); i++) {
			content[i] = strToBeFiltered.charAt(i);
		}
		// filter chars and put the result in a StringBuffer
		StringBuilder result = new StringBuilder(content.length + 50);
		for (int i = 0; i < content.length; i++) {
			boolean replaced = false;
			for (int j = 0; j < charsToFilter.length; j++) {
				if (content[i] == charsToFilter[j]) {
					result.append(charsFiltered[j]);
					replaced = true;
				}
			}
			if (!replaced) result.append(content[i]);
		}
		return (result.toString());
	}
	/**
	 * Filters chars
	 * @param strToBeFiltered
	 * @param isLegalCharPred
	 * @return
	 */
	public static CharSequence filterChars(final CharSequence strToBeFiltered,
										   final Predicate<Character> isLegalCharPred) {
		StringBuffer out = new StringBuffer(strToBeFiltered.length());

		for (int i = 0; i < strToBeFiltered.length(); i++) {
			char code = strToBeFiltered.charAt(i);
			if (isLegalCharPred.apply(code)) {
				out.append(code);
			}
		}
		return out;
	}
	public static CharSequence removeAccents(final CharSequence str) {
		if (str == null ) return str;
		// ver http://www.v3rgu1.com/blog/231/2010/programacion/eliminar-acentos-y-caracteres-especiales-en-java/
		// canonical decomposition is used:
		//		A character can be represented in many ways:
		//			- char c1 = 'á'							usual
		//			- char c2 = '\u00ed'					unicode
		//			- char[] c3 = {'\u0069', '\u0301'};		unicode en forma canonica
		//		Unicode canonical representation is composed by two chars: base letter + the accent
		//		this way á can be canonically represented as letter (\u0069) and accent (\u0301)
		String normalized = Normalizer.normalize(str,Normalizer.Form.NFD);
		// get ASCII chars
		Pattern pattern = Pattern.compile("[^\\p{ASCII}+]");
		return pattern.matcher(normalized).replaceAll("");
	}
///////////////////////////////////////////////////////////////////////////////
//  CODE / DECODE www-form-urlencodec
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes in a www-form-urlencoded format using the default charset
	 * @param str the {@link String} to be encoded
	 * @return the www-form-urlencoded {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence urlEncode(final CharSequence str) throws EncoderException  {
		if (str == null) return null;
		URLCodec codec = new URLCodec();
		String encodedStr = codec.encode(str.toString());
		return encodedStr;
	}
	/**
	 * Encodes in a www-form-urlencoded format using the default charset
	 * it does NOT throw any exception if the encoding cannot be done
	 * @param str
	 * @return
	 */
	public static CharSequence urlEncodeNoThrow(final CharSequence str) {
		try {
			return StringEncodeUtils.urlEncode(str);
		} catch (EncoderException encEx) {
			log.error("Could NOT url encode {}",str,encEx);
		}
		return str;	// fall back to the original not-encoded string
	}
///////////////////////////////////////////////////////////////////////////////
// ENCODE / DECODE BASE64
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes in Base64
	 * @param str the {@link String} to be encoded
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence encodeBase64(final CharSequence str) {
		if (str == null) return null;
		String encodedStr = new String(Base64.encodeBase64(str.toString().getBytes()));
		return encodedStr;
	}
	/**
	 * Encodes a byte array in a base64 String
	 * @param bytes The byte array to be encoded
	 * @return The base64 {@link String}
	 */
	@Deprecated
	public static String encodeBase64String(final byte[] bytes) {
		return StringEncodeUtils.encodeBase64AsString(bytes);
	}
	/**
	 * Encodes a byte array in a base64 String
	 * @param bytes The byte array to be encoded
	 * @return The base64 {@link String}
	 */
	public static String encodeBase64AsString(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base64.encodeBase64String(bytes);
	}
	/**
	 * Encodes in Base64
	 * @param str the byte array to be encoded
	 * @return the Base64 dta in byte array format
	 * @throws EncoderException if the encoding could not be done
	 */
	public static byte[] encodeBase64(final byte[] bytes) {
		return encodeBase64(bytes, false, false);
	}
	/**
	 * Encodes in Base64
	 *
	 * @param bytes the bytes to be encoded
	 * @param isChunked if true, the encoder will chunk the base64 output into 76 character blocks
	 * @param urlSafe if true, the encoder will output '-' and '_' instead of the usual '+' and '/'
	 *
	 * @return the Base64 data in byte array format
	 * @throws EncoderException if the encoding could not be done
	 */
	public static byte[] encodeBase64(final byte[] bytes, final boolean isChunked, final boolean urlSafe) {
		if (bytes == null || bytes.length == 0) return null;
		return Base64.encodeBase64(bytes, isChunked, urlSafe);
	}
	/**
	 * Decodes a Base64 {@link String}
	 * @param str the {@link String} to be decoded
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence decodeBase64(final CharSequence str) {
		if (str == null) return null;
		String encodedStr = new String(Base64.decodeBase64(str.toString().getBytes()));
		return encodedStr;
	}
	/**
	 * Decodes a Base64 byte array
	 * @param bytes The byte array to be decoded
	 * @return The decoded data in byte array format
	 */
	public static byte[] decodeBase64(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base64.decodeBase64(bytes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DECODE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Decodes a www-form-urlencodec {@link String} using the default charset
	 * @param str the {@link String} to be decoded
	 * @return the decoded {@link String}
	 * @throws DecoderException if the decoding could not be done
	 */
	public static CharSequence urlDecode(final CharSequence str) throws DecoderException  {
		if (str == null) return null;
		URLCodec codec = new URLCodec();
		String encodedStr = codec.decode(str.toString());
		return encodedStr;
	}
	/**
	 * Decodes a www-form-urlencoded  {@link String} using the default charset
	 * it does NOT throw any exception if the decoding cannot be done
	 * @param str
	 * @return
	 */
	public static CharSequence urlDecodeNoThrow(final CharSequence str) {
		CharSequence outDecoded = null;
		try {
			outDecoded = StringEncodeUtils.urlDecode(str);
		} catch (DecoderException encEx) {
			log.error("Could NOT url decode {}",str,encEx);
		}
		return outDecoded;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// ENCODE BYTES TO HEX.
/////////////////////////////////////////////////////////////////////////////////////////
	public static String encodeAsHexString(byte[] data) {
		return Hex.encodeHexString(data);
	}
	public static byte[] decodeHex(char[] data) {
		byte[] bytes = null;
		try {
			bytes = Hex.decodeHex(data);
		} catch (DecoderException e) {
			log.error("Could NOT  decode {}",new String(data));
		}
		return bytes;
	}
}
