package r01f.util.types;

import com.google.common.annotations.GwtIncompatible;

public class StringEscapeUtils {
	/**
	 * Escapes the characters in a JSon string
	 * According to the RFC. JSON is pretty liberal: The only characters that must be escaped are \, ", and control codes (anything less than U+0020).
	 * see http://stackoverflow.com/questions/3020094/how-should-i-escape-strings-in-json
	 * @return
	 */
	public static CharSequence escapeJSON(final CharSequence str) {
        char c = 0;
        int i;
        int len = str.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;
	    sb.append('"');
        for (i = 0; i < len; i += 1) {
        	c = str.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
            	sb.append('\\');
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
               sb.append("\\r");
               break;
            default:
                if (c < ' ') {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb;
	}
	/**
	 * Escapes the characters in a String using HTML entities.
	 * For example: "bread" & "butter" becomes &quot;bread&quot; &amp; &quot;butter&quot;.
	 * Supports all known HTML 4.0 entities, including funky accents. 
	 * Note that the commonly used apostrophe escape character (&apos;) is not a legal entity and so is not supported).
	 */
	@GwtIncompatible("apache commons StringEscapeUtils is NOT supported by GWT")
	public static CharSequence escapeHTML(final CharSequence str) {
		return new StringBuilder(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(str.toString()));
	}
	/**
	 * Escapes the characters in a String using XML entities.
	 * For example: "bread" & "butter" => &quot;bread&quot; &amp; &quot;butter&quot;.
	 * Supports only the five basic XML entities (gt, lt, quot, amp, apos). Does not support DTDs or external entities.
	 * Note that unicode characters greater than 0x7f are currently escaped to their numerical \\u equivalent. 
	 */
	@GwtIncompatible("apache commons StringEscapeUtils is NOT supported by GWT")
	public static CharSequence escapeXML(final CharSequence _buffer) {
		return new StringBuilder(org.apache.commons.lang3.StringEscapeUtils.escapeXml10(_buffer.toString()));
	}
	/**
	 * Escapes the characters in a {@code String} using Java String rules.
	 * For example: ""áéíóú"" => \u00E1\u00E9\u00ED\u00F3\u00FA\u00F1\u00F6.
	 */
	@GwtIncompatible("apache commons StringEscapeUtils is NOT supported by GWT")
	public static CharSequence escapeJava(final CharSequence _buffer) {
		return new StringBuilder(org.apache.commons.lang3.StringEscapeUtils.escapeJava(_buffer.toString()));
	}
	/**
	 * Escapes the characters in a {@code String} using Java String rules.
	 * For example: ""áéíóú"" => \u00E1\u00E9\u00ED\u00F3\u00FA\u00F1\u00F6.
	 */
	@GwtIncompatible("apache commons StringEscapeUtils is NOT supported by GWT")
	public static CharSequence escapeEcmaScript(final CharSequence _buffer) {
		return new StringBuilder(org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(_buffer.toString()));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * UnEscapes the characters in a String using HTML entities.
	 * For example: &quot;bread&quot; &amp; &quot;butter&quot; ==> "bread" & "butter"
	 * Supports all known HTML 4.0 entities, including funky accents. 
	 * Note that the commonly used apostrophe escape character (&apos;) is not a legal entity and so is not supported).
	 */
	@GwtIncompatible("apache commons StringEscapeUtils is NOT supported by GWT")
	public static CharSequence unescapeHTML(final CharSequence str) {
		return new StringBuilder(org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(str.toString()));
	}
	/**
	 * UnEscapes the characters in a String using XML entities.
	 * For example: &quot;bread&quot; &amp; &quot;butter&quot; ==> "bread" & "butter"
	 * Supports only the five basic XML entities (gt, lt, quot, amp, apos). Does not support DTDs or external entities.
	 * Note that unicode characters greater than 0x7f are currently escaped to their numerical \\u equivalent. 
	 */
	@GwtIncompatible("apache commons StringEscapeUtils is NOT supported by GWT")
	public static CharSequence unescapeXML(final CharSequence _buffer) {
		return new StringBuilder(org.apache.commons.lang3.StringEscapeUtils.unescapeXml(_buffer.toString()));
	}
	/**
	 * UnEscapes the characters in a {@code String} using Java String rules.
	 * For example: \u00E1\u00E9\u00ED\u00F3\u00FA\u00F1\u00F6. ==> "áéíóú"
	 */
	@GwtIncompatible("apache commons StringEscapeUtils is NOT supported by GWT")
	public static CharSequence unescapeJava(final CharSequence _buffer) {
		return new StringBuilder(org.apache.commons.lang3.StringEscapeUtils.unescapeJava(_buffer.toString()));
	}
	/**
	 * UnEscapes the characters in a {@code String} using Java String rules.
	 * For example: \u00E1\u00E9\u00ED\u00F3\u00FA\u00F1\u00F6. ==> "áéíóú"
	 */
	@GwtIncompatible("apache commons StringEscapeUtils is NOT supported by GWT")
	public static CharSequence unescapeEcmaScript(final CharSequence _buffer) {
		return new StringBuilder(org.apache.commons.lang3.StringEscapeUtils.unescapeEcmaScript(_buffer.toString()));
	}
}
