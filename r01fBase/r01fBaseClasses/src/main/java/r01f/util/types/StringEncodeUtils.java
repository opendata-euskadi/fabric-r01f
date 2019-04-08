package r01f.util.types;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringEncodeUtils {

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
     *       http://en.wikipedia.org/wiki/Windows-1252
     *       http://www.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WindowsBestFit/bestfit1252.txt
     *       http://download-llnw.oracle.com/javase/tutorial/i18n/text/convertintro.html
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
		} catch(EncoderException encEx) {
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
	 * Encodes in Base64
	 * @param str the {@link String} to be encoded
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence decodeBase64(final CharSequence str) {
		if (str == null) return null;
		String encodedStr = new String(Base64.decodeBase64(str.toString().getBytes()));
		return encodedStr;
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
		} catch(DecoderException encEx) {
			log.error("Could NOT url decode {}",str,encEx);
		}
		return outDecoded;
	}
}
