package r01f.util.types;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.helpers.MessageFormatter;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;


/**
 * This type provides some utility static methods:
 * 			<pre class='brush:java'>
 * 				String myQuotedStr = Strings.quote("my unquoted str");
 * 				...
 * 				String customized = Strings.customized("{}:{}",
 * 													   "hello","Alex");
 * 				...
 * 				int anInt = Strings.asInteger("25");
 * 				...
 * 				Reader r = Strings.asReader("sadfasdfasd");
 * 			</pre>
 */
public class Strings {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String EMPTY = "";
	
///////////////////////////////////////////////////////////////////////////////
// 	STATIC UTIL METHODS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns as a byte array encoded on the system's default charset
	 * @param str
	 */
	@GwtIncompatible("Charset.defaultCharset() is NOT supported by GWT")
	public static byte[] getBytes(final CharSequence str) {
		if (str == null) return null;
		return str.toString().getBytes(Charset.defaultCharset());
	}
	/**
	 * Returns as a byte array encoded on the provided charset
	 * @param str
	 * @param charset the encoding
	 */
	@GwtIncompatible("String.getBytes(charset) is NOT suppported by GWT")
	public static byte[] getBytes(final CharSequence str,
								  final Charset charset) {
		if (str == null) return null;
		return str.toString().getBytes(charset);
	}
	/**
	 * Returns an array of {@link String}s with each line of the original {@link String}
	 * @param str
	 */
	public static String[] getLines(final CharSequence str) {
		if (str == null) return null;
		String[] outLines = str.toString().split("\\r?\\n");
		return outLines;
	}
	/**
	 * Customizes a {@link String} containing placeholders like {} for provided vars
	 * ie:
	 * <pre class='brush:java'>
	 * 		Strings.of("Hello {} today is {}","Alex","Saturday"}
	 * </pre>
	 * @param strToCustomize the {@link String} to be customized
	 * @param vars the placeholder's values
	 * @return an {@link String} composed from the strToCustomize param replacing the placeholders with the provided values
	 */
	public static String customized(final CharSequence strToCustomize,final Object... vars) {
		return _customize(strToCustomize,vars);
	}
	/**
	 * Customizes a {@link String} containing placeholders like {} for provided vars
	 * ie:
	 * <pre class='brush:java'>
	 * 		Strings.of("Hello {} today is {}","Alex","Saturday"}
	 * </pre>
	 * @param strToCustomize the {@link String} to be customized
	 * @param vars the placeholder's values
	 * @return an {@link StringBuffer} composed from the strToCustomize param replacing the placeholders with the provided values
	 */
	private static String _customize(final CharSequence strToCustomize,final Object... vars) {
		if (strToCustomize == null) return null;
		if (vars == null || vars.length == 0) return strToCustomize.toString();
		
		// reuse MessageFormatter from SL4FJ
		return MessageFormatter.arrayFormat(strToCustomize.toString(),vars)
							   .getMessage();
//		// custom impl
//		String workStr = strToCustomize.toString();
//		for (Object var : vars) {
//			workStr = workStr.replaceFirst("\\{\\}",(var != null ? _matcherQuoteReplacement(var.toString())	// should be _objectToString(var) but it's problematic in GWT 
//																 : "null"));	
//		}
//		return new StringBuffer(workStr);
	}
    /**
     * Copy of {@link Matcher#quoteReplacement(String)} to make it possible to use
     * this with GWT
     * @param s
     * @return
     * @see Matcher#quoteReplacement(String)
     */
	@SuppressWarnings("unused")
	private static String _matcherQuoteReplacement(final String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1)) return s;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '$') sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }
/*
	private static String _objectToString(final Object object) {
		if (object == null) return null;
		String outStr = null;
		if (CollectionUtils.isArray(object.getClass())) {
			outStr = _objectArrayToString((Object[])object);
		} else if (CollectionUtils.isCollection(object.getClass())) {
			outStr = _objectCollectionToString((Collection<?>)object);
		} else if (CollectionUtils.isMap(object.getClass())) {
			outStr = _objectCollectionToString((Map<?,?>)object);
		} else {
			outStr = object.toString();
		}
		return outStr;
	}
	private static String _objectArrayToString(final Object[] objects) {
		StringBuffer outStr = new StringBuffer();
		outStr.append("[");
		for (int i=0; i<objects.length; i++) {
			outStr.append(objects[i]);
			if (i < objects.length-1) outStr.append(",");
		}
		outStr.append("]");
		return outStr.toString();
	}
	private static String _objectCollectionToString(final Collection<?> objects) {
		String outStr = null;
		if (CollectionUtils.hasData(objects)) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Iterator<?> it = objects.iterator(); it.hasNext(); ) {
				Object o = it.next();
				sb.append(o != null ? o.toString() : "null");
				if (it.hasNext()) sb.append(",");
			}
			sb.append("]");
			outStr = sb.toString();
		}
		return outStr;
	}
	private static String _objectCollectionToString(final Map<?,?> objects) {
		String outStr = null;
		if (CollectionUtils.hasData(objects)) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Iterator<?> it = objects.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry<?,?> me = (Map.Entry<?,?>)it.next();
				sb.append(me.getKey())
				  .append(": ")
				  .append(me.getValue() != null ? me.getValue() : "null");
				if (it.hasNext()) sb.append(",");
			}
			sb.append("]");
			outStr = sb.toString();
		}
		return outStr;
	}
*/
	public interface StringCustomizerVarsProvider {
		public Object[] provideVars();
	}
	public interface StringCustomizerVarsProviderConditioned 
			 extends StringCustomizerVarsProvider {
		public boolean shouldAdd();
	}
	/**
	 * Customizes the underlyng string with variables are provided by the {@link StringCustomizerVarsProvider} instance
	 * The {@link StringCustomizerVarsProvider} instance can be in two flavours:
	 * <ul>
	 * 		<li>{@link StringCustomizerVarsProvider} if the provided string is added without evaluating any condition</li>
	 * 		<li>{@link StringCustomizerProviderVarsConditioned} if the provided string is added if the shouldAdd() method of the vars provider returns true</li>
	 * <ul>
	 * Sample code:
	 * <pre class="brush:java">
	 * 		String customized = Strings.customizedWith("the var value is {}",
	 * 												   new StringCustomizerVarsProviderConditioned() {
	 * 														@Override
	 * 														public boolean shouldAdd() {
	 * 															return true;		// any condition could be evaluated to guess if the string should be added or not
	 * 														}
	 * 														@Override
	 * 														public Object[] provideVars() {	
	 * 															return new Object[] {"the value"};
	 * 														}
	 * 												  })
	 * 								   .asString();
	 * </pre>
	 * @param str
	 * @param varsProvider
	 * @return
	 */
	public static String customizedWith(final String str,
								 		final StringCustomizerVarsProvider varsProvider) {
		boolean shouldAdd = (varsProvider instanceof StringCustomizerVarsProviderConditioned) ? ((StringCustomizerVarsProviderConditioned)varsProvider).shouldAdd()
																						      : true;
		if (shouldAdd) {
			Object[] vars = varsProvider.provideVars();
			return Strings.customized(str,vars);
		}
		return str;
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
    /**
     * Validates if a string is null or empty: whitespace is NOT taken into account
     * This method can be used in place of <code>sb.toString().trim().length()</code>,
     * @param str 
     * @return 
     */
    public static boolean isNullOrEmpty(final CharSequence str) {
        if (str == null || str.length() == 0) return true;        // true if null
        return CharMatcher.whitespace().matchesAllOf(str);
        /*
        // GWT does NOT supports Character.isWhitespace method
        boolean isEmpty = true;
        for (int i=0;i<str.length();i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
        */
    }
    /**
     * Validates if a string is null or empty: whitespace is NOT taken into account
     * This method can be used in place of <code>sb.toString().trim().length()</code>,
     * @param char
     * @return 
     */
    public static boolean isNullOrEmpty(final char[] chars) {
    	return Strings.isNullOrEmpty(new String(chars));
    }
    /**
     * The reverse method of {@link Strings}{@link #isNullOrEmpty(CharSequence)}
     * @param str the {@link CharSequence}
     * @return true is the CharSequence is NOT null or empty
     */
    public static boolean isNOTNullOrEmpty(final CharSequence str) {
    	return !Strings.isNullOrEmpty(str);
    }
    /**
     * The reverse method of {@link Strings}{@link #isNullOrEmpty(char[])}
     * @param str the char array
     * @return true is the char array NOT null or empty
     */
    public static boolean isNOTNullOrEmpty(final char[] chars) {
    	return !Strings.isNullOrEmpty(chars);
    }
	/**
	 * Concatenates a bunch of strings
	 * @param strs 
	 * @return 
	 */
	public static String concat(final CharSequence... strs) {
		if (strs == null || strs.length == 0) return null;
		StringBuilder sb = new StringBuilder();
		for (CharSequence currSeq : strs) sb.append(currSeq);
		return sb.toString();
	}
	/**
	 * Concatenates a bunch of Strings
	 * @param strs 
	 * @return 
	 */
	public static String concat(final String... strs) {
		if (strs == null || strs.length == 0) return null;
		StringBuilder sb = new StringBuilder();
		for (String s : strs) sb.append(s);
		return sb.toString();
	}
	/**
	 * Quotes the string (surrounds it with single quotes)
	 * @param s 
	 * @return 
	 */
	public static String quote(final String s) {
		return s != null ? Strings.concat("'",s,"'") : null;
	}
	/**
	 * Double quotes the string (surrounds it with double quotes)
	 * @param s
	 * @return
	 */
	public static String doubleQuoute(final String s) {
		return s != null ? Strings.concat("\"",s,"\"") : null;
	}
	/**
	 * Returns the value if the first parameter is not null or the default value if it is
	 * @param s the value
	 * @param defaultValue the defaultValue
	 * @return the value or the default value
	 */
	public static String valueOrDefault(final String s,
										final String defaultValue) {
		return s != null ? s 
						 : defaultValue;
	}
///////////////////////////////////////////////////////////////////////////////
// 	PAD
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Removes everything that's not a word (spaces, _, /, etc) 
	 * @param str la cadena de origen
	 * @return la cadena modificada
	 */
	@GwtIncompatible("regex is NOT supported by GWT")
	public static String removeWhitespace(final String str) {
		if (str == null) return str;
		StringBuffer sb = new StringBuffer();
		Pattern regex = Pattern.compile("[^\\w]");
		Matcher regexMatcher = regex.matcher(str);
		while(regexMatcher.find()) regexMatcher.appendReplacement(sb,"");
		regexMatcher.appendTail(sb);
		return sb.toString();
	}
	/**
	 * Removes all \n or \r character
	 * @param str
	 * @return
	 */
	public static String removeNewlinesOrCarriageRetuns(final String str) {
		if (str == null) return str;
		return str.replaceAll("[\n\r]","");
	}
	/**
	 * Adjust the string size appending chars at the left until the string size is the given one
	 * @param size 
	 * @param character 
	 * @return
	 */
	public static String leftPad(final String str,
						  		 final int size,final char character) {
		if (str == null) return str;
		return StringUtils.leftPad(str,size,character);
	}
	/**
	 * Adjust the string size appending chars at the right until the string size is the given one
	 * @param str
	 * @param size 
	 * @param character 
	 * @return
	 */
	public static String rightPad(final String str,
						   		  final int size,final char character) {
		if (str == null) return str;
		return StringUtils.rightPad(str,size,character);
	}
	/**
	 * Replaces accentuated chars with their no-accentuated equivalents
	 * @param str
	 */
	@GwtIncompatible("Normalized and regex is not supported by GWT")
	public static String removeAccents(final String str) {
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
	/**
	 * Sets the first letter in upper case
	 * @param str
	 */
	@GwtIncompatible
	public static String capitalizeFirstLetter(final String str) {
		if (str == null) return str;
		return StringUtils.capitalize(str);
	}
///////////////////////////////////////////////////////////////////////////////
//  COMPARATOR
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link String} comparator that takes the language (Locale) into account
	 * @param lang
	 * @return
	 */
	@GwtIncompatible
	public static Comparator<String> comparatorIn(final Language lang) {
		return Languages.stringComparatorFor(lang);
	}
	public static StringIsContainedWrapper isContainedWrapper(final String str) {
		return new StringIsContainedWrapper(str);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class StringIsContainedWrapper {
		private final String _wrappedStr;
		
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean in(final Set<String> otherStrings) {
			return this.in(false,otherStrings);
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean in(final String... otherStrings) {
			return this.in(false,otherStrings);
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * ignorign the {@link String} case
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean inIgnoringCase(final Set<String> otherStrings) {
			return this.in(true,otherStrings);
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * ignorign the {@link String} case
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean inIgnoringCase(final String... otherStrings) {
			return this.in(true,otherStrings);
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @param ignoreCase if the check should take case into account
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean in(final boolean ignoreCase,final String... otherStrings) {
			return this.in(ignoreCase,Sets.newHashSet(otherStrings));
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @param ignoreCase if the check should take case into account
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean in(final boolean ignoreCase,final Set<String> otherStrings) {
			if (CollectionUtils.isNullOrEmpty(otherStrings)) return false;
			boolean outIsIn = false;
			for (String s : otherStrings) {
				if (s != null) {
					outIsIn = ignoreCase ? _wrappedStr.equalsIgnoreCase(s)
										 : _wrappedStr.equals(s);
					if (outIsIn) break;
				}
			}
			return outIsIn;
		}
		/**
		 * Checks if the wrapped string contains any of the given strings
		 * @param otherStrings 
		 * @return 
		 */
		public boolean containsAny(final String... otherStrings) {
			return this.containsAny(false,otherStrings);
		}
		/**
		 * Checks if the wrapped string contains any of the given strings
		 * @param otherStrings 
		 * @return 
		 */
		public boolean containsAnyIgnoringCase(final String... otherStrings) {
			return this.containsAny(true,otherStrings);
		}
		/**
		 * Checks if the string contains any of the given strings 
		 * @param ignoreCase
		 * @param otherStrings
		 * @return 
		 */
		public boolean containsAny(final boolean ignoreCase,final String... otherStrings) {
			if (CollectionUtils.isNullOrEmpty(otherStrings)) return false;
			boolean outContains = false;
			for (String s : otherStrings) {
				if (s != null) {
					outContains = ignoreCase ? _wrappedStr.toLowerCase().contains(s.toLowerCase())
										     : _wrappedStr.contains(s);
					if (outContains) break;
				}
			}
			return outContains;
		}
	}
}
