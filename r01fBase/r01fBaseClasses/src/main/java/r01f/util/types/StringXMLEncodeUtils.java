package r01f.util.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;

public class StringXMLEncodeUtils {
///////////////////////////////////////////////////////////////////////////////
//  CODE / DECODE
///////////////////////////////////////////////////////////////////////////////
    /**
     * Checks if an xml string character is valid according to the xml spec
     * @see http://seattlesoftware.wordpress.com/2008/09/11/hexadecimal-value-0-is-an-invalid-character/
     * If an xml string containing illegal characters is parsed, an error is raised:
     * 		"Hexadecimal value 0x[---] is an invalid character"
     * @param theChar
     * @return true if char is legal
     */
    public static boolean isLegalChar(final int theChar) {
    	return  (theChar == 0x9) ||/* == '\t' == 9   */
                (theChar == 0xA) ||/* == '\n' == 10  */
                (theChar == 0xD) ||/* == '\r' == 13  */
                ((theChar >= 0x20) && (theChar <= 0xD7FF)) ||
                ((theChar >= 0xE000) && (theChar <= 0xFFFD)) ||
                ((theChar >= 0x10000) && (theChar <= 0x10FFFF));
    }
    /**
     * Removes illegal characters according to the xml spec
     * @param str
     * @return 
     */
    public static CharSequence filterInvalidChars(final CharSequence str) {
        StringBuffer outResp = new StringBuffer(str.length());

        for (int i = 0; i < str.length(); i++) {
            int code = str.charAt(i);
            if (StringXMLEncodeUtils.isLegalChar(code)) {  
                    outResp.append(str.charAt(i));
            }
        }
        return outResp.toString();
    }   
	/**
     * Encodes a string to UTF: doble byte (>127) characters are converted to their escaped format (&#CODE;)
     * @see http://seattlesoftware.wordpress.com/2008/09/11/hexadecimal-value-0-is-an-invalid-character/
     * @param str 
     * @return 
     */
    public static CharSequence encodeUTFDoubleByteCharsAsEntities(final CharSequence str) {
    	if (str == null) return null;
        StringBuilder outResp = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            int code = str.charAt(i);
            if (StringXMLEncodeUtils.isLegalChar(code)) {
                if (code >= 127) {
                    outResp.append("&#");
                    outResp.append(code);
                    outResp.append(";");
                } else {
                    outResp.append(str.charAt(i));
                }
            }
        }
        return outResp.toString();
    }
    /**
     * Encodes like encodeUTF and also encodes quotes
     * @see http://seattlesoftware.wordpress.com/2008/09/11/hexadecimal-value-0-is-an-invalid-character/
     * @param str 
     * @return 
     */
    public static CharSequence encodeUTFDoubleByteCharsAndQuoutesAsEntities(final CharSequence str) {
    	if (str == null) return null;
        StringBuilder outResp = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            int code = str.charAt(i);
            if (StringXMLEncodeUtils.isLegalChar(code)) {
                if (code > 127 || code == 34 || code == 39) {
                    outResp.append("&#");
                    outResp.append(code);
                    outResp.append(";");
                } else {
                    outResp.append(str.charAt(i));
                }
            }
        }
        return outResp.toString();
    }
    /**
     * Decodes an string where UTF double byte chars (>127) aree encoded as entities 
     * @param str
     * @return 
     */
    @GwtIncompatible
    public static CharSequence decodeUTFDoubleByteCharsFromEntities(final CharSequence str) {
        if (str == null) return null;
        StringBuffer result = new StringBuffer(str.length());

        String regExp = "&#(([0-9]{1,7})|(x[0-9a-f]{1,6}));?";
        Pattern p = Pattern.compile(regExp,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        boolean found = m.find();
        if (found) {
            // replace
            do {
                //String replaceStr = m.group();
                String numericValue = m.group(1);
                char c = Character.MIN_VALUE;
                if (numericValue.startsWith("x") || numericValue.startsWith("X")) {
                    // hexadecimal
                    c = (char)Integer.parseInt(numericValue, 16);
                } else {
                    // decimal
                    c = (char)Integer.parseInt(numericValue);
                }
                m.appendReplacement(result, Character.toString(c));
                found = m.find();
            } while (found);
            m.appendTail(result);
            return result.toString();
        }
        // Return 
        return str;
    }  
}
