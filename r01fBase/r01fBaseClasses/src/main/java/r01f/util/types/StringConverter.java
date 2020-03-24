package r01f.util.types;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Date;

import com.google.common.annotations.GwtIncompatible;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.locale.Language;
import r01f.types.Path;
import r01f.util.types.locale.Languages;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class StringConverter {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A filter to be used when returning the underlying {@link CharSequence}
	 * as a {@link String}
	 */
	public interface StringConverterFilter {
		public String filter(String str);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOLEAN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a boolean 
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param val
	 * @param defValue
	 * @return
	 */
	public static Boolean asBoolean(final CharSequence val,final Boolean defValue) {
        if (val == null) return defValue;
        
        String value = val.toString();
        if ((value.equalsIgnoreCase("true")) ||
            (value.equalsIgnoreCase("on")) ||
            (value.equalsIgnoreCase("yes")) ||
            (value.equalsIgnoreCase("si")) ||
            (value.equalsIgnoreCase("bai"))) {
            return Boolean.TRUE;
        } else if ((value.equalsIgnoreCase("false")) ||
                   (value.equalsIgnoreCase("off")) ||
                   (value.equalsIgnoreCase("no")) ||
                   (value.equalsIgnoreCase("ez"))) {
            return Boolean.FALSE;
        } else {
            return defValue;
        }
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  BYTE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a byte 
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Byte asByte(final CharSequence value,final Byte defValue) {
    	if (value == null) return defValue;
    	return Byte.parseByte(value.toString());
    }	

/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the given {@link CharSequence} as a char 
	 * ...or the given default value if the wrapped {@link CharSequence} is null
     * @param value
     * @param defValue
     * @return
     */
    public static Character asChar(final CharSequence value,final Character defValue) {
        if (value == null) return defValue;
        return value.charAt(0);
    }    

/////////////////////////////////////////////////////////////////////////////////////////
//  DOUBLE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the given {@link CharSequence} as a double
	 * ...or the given default value if the wrapped {@link CharSequence} is null
     * @param value
     * @param defValue
     * @return
     */
    public static Double asDouble(final CharSequence value,final Double defValue) {
    	if (value == null) return defValue;
    	return Double.parseDouble(value.toString());	// bear formated numbers
    }    

/////////////////////////////////////////////////////////////////////////////////////////
//  FLOAT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the given {@link CharSequence} as a float
	 * ...or the given default value if the wrapped {@link CharSequence} is null
     * @param value
     * @param defValue
     * @return
     */
    public static Float asFloat(final CharSequence value,final Float defValue) {
    	if (value == null) return defValue;
    	return Float.parseFloat(value.toString());
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  INTEGER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the given {@link CharSequence} as an integer
	 * ...or the given default value if the wrapped {@link CharSequence} is null
     * @param value
     * @param defValue
     * @return
     */
    public static Integer asInteger(final CharSequence value,final Integer defValue) {
    	if (value == null) return defValue;
    	return Integer.parseInt(value.toString());
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  LONG
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the given {@link CharSequence} as a long
	 * ...or the given default value if the wrapped {@link CharSequence} is null
     * @param value
     * @param defValue
     * @return
     */
    public static Long asLong(final CharSequence value,final Long defValue) {
    	if (value == null) return defValue;
    	return Long.parseLong(value.toString());
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  SHORT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the given {@link CharSequence} as a short
	 * ...or the given default value if the wrapped {@link CharSequence} is null
     * @param value
     * @param defValue
     * @return
     */
    public static Short asShort(final CharSequence value,final Short defValue) {
    	if (value == null) return defValue;
    	return Short.parseShort(value.toString());
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link Date} using the given format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param format
	 * @param defValue
	 * @return
	 */
    @GwtIncompatible
	public static Date asDate(final CharSequence value,final String format,
							  final Date defValue) {
		if (value == null) return defValue;
		return Dates.fromFormatedString(value.toString(),format);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link Path}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Path asPath(final CharSequence value,final Path defValue) {
		if (value == null) return defValue;
		return Path.from(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Language
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link Language}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	@GwtIncompatible
	public static Language asLanguageFromCountryCode(final CharSequence value,final Language defValue) {
		if (value == null) return defValue;
		return Languages.fromLanguageCode(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENUM
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E extends Enum<E>> E asEnumElement(final CharSequence value,final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = Enum.valueOf(enumType,value.toString());
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value 
			}
		}
		return outE;
	}
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E extends EnumWithCode<String,E>> E asEnumElementFromStringCode(final CharSequence value,final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = EnumWithCodeWrapper.<String,E>wrapEnumWithCode(enumType)
										  .fromCode(value.toString());
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value 
			}
		}
		return outE;
	}
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E extends EnumWithCode<Integer,E>> E asEnumElementFromIntCode(final CharSequence value,final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = EnumWithCodeWrapper.<Integer,E>wrapEnumWithCode(enumType)
										  .fromCode(Integer.parseInt(value.toString()));
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value 
			}
		}
		return outE;
	}
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E extends EnumWithCode<Long,E>> E asEnumElementFromLongCode(final CharSequence value,final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = EnumWithCodeWrapper.<Long,E>wrapEnumWithCode(enumType)
										  .fromCode(Long.parseLong(value.toString()));
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value 
			}
		}
		return outE;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the given {@link CharSequence} as a {@link StringBuilder} 
	 * ...or the given default value if the wrapped {@link CharSequence} is null
     * @param value
     * @param defValue
     * @return
     */
    public static StringBuilder asStringBuilder(final CharSequence value,final StringBuilder defValue) {
    	if (value == null) return defValue;
    	return new StringBuilder(value);
    }
    /**
     * Returns the given {@link CharSequence} as a {@link StringBuilder} 
     * @param value
     * @return
     */
    public static StringBuilder asStringBuilder(final CharSequence value) {
    	return StringConverter.asStringBuilder(value,null);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link StringBuffer}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static StringBuffer asStringBuffer(final CharSequence value,final StringBuffer defValue) {
		if (value == null) return defValue;
		return new StringBuffer(value);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link StringBuffer}
	 * @param value
	 * @return
	 */
	public static StringBuffer asStringBuffer(final CharSequence value) {
		return StringConverter.asStringBuffer(value,null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR ARRAY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a char array
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static char[] asCharArray(final CharSequence value,final char[] defValue) {
		if (value == null) return defValue;
		return value.toString().toCharArray(); 
	}
	/**
	 * Returns the given {@link CharSequence} as a char array
	 * @param value
	 * @return
	 */
	public static char[] asCharArray(final CharSequence value) {
		if (value == null) return "".toString().toCharArray();
		return value.toString().toCharArray();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  InputStream
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns an {@link InputStream} to the buffer, that's the {@link String} as an {@link InputStream}
	 * the stream's byte charset is provided 
	 * @param str
	 */
	@GwtIncompatible("IO is NOT supported by GWT")
	public static InputStream asInputStream(final CharSequence str) {
		return StringConverter.asInputStream(str,Charset.defaultCharset());
	}
	/**
	 * Returns an {@link InputStream} to the buffer, that's the {@link String} as an {@link InputStream}
	 * the stream's byte charset is provided 
	 * @param str
	 * @param charset 
	 */
	@GwtIncompatible("IO is NOT supported by GWT")
	public static InputStream asInputStream(final CharSequence str,
											final Charset charset) {
		if (str == null) return null;
		return new ByteArrayInputStream(str.toString().getBytes(charset));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Reader} to the buffer, that's the {@link String} as a {@link Reader}
	 * @param str
	 */
	@GwtIncompatible("IO is NOT supported by GWT")
	public static Reader asReader(final CharSequence str) {
		if (str == null) return null;
		return new StringReader(str.toString());
	}
}
