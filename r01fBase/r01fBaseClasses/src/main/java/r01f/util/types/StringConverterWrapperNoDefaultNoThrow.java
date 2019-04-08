package r01f.util.types;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Date;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.types.Path;
import r01f.util.types.StringConverter.StringConverterFilter;

@RequiredArgsConstructor
public class StringConverterWrapperNoDefaultNoThrow {
/////////////////////////////////////////////////////////////////////////////////////////
//  FINAL
/////////////////////////////////////////////////////////////////////////////////////////
	private final CharSequence _theString;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link String}
	 * @return
	 */
	public String asString() {
		return _theString != null ? _theString.toString() : null;
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link String} applying the filter
	 * @param filter
	 * @return
	 */
	public String asString(final StringConverterFilter filter) {
		return _theString != null ? filter.filter(_theString.toString()) : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOLEAN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a boolean
	 * @return
	 */
	public Boolean asBoolean() {
		return StringConverter.asBoolean(_theString,false);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BYTE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a byte
	 * @return
	 */
	public Byte asByte() {
		return StringConverter.asByte(_theString,(byte)'\u0000');	// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a char
     * @return
     */
    public Character asChar() {
		return StringConverter.asChar(_theString,'\u0000');	// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DOUBLE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a double
     * @return
     */
    public Double asDouble() {
    	return StringConverter.asDouble(_theString,0D);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FLOAT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a float
     * @return
     */
    public Float asFloat() {
    	return StringConverter.asFloat(_theString,0F);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  INTEGER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as an integer
     * @return
     */
    public Integer asInteger() {
    	return StringConverter.asInteger(_theString,0);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  LONG
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a long
     * @return
     */
    public Long asLong() {
    	return StringConverter.asLong(_theString,0L);			// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  SHORT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a short
     * @return
     */
    public Short asShort() {
    	return StringConverter.asShort(_theString,(short)0);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Date} using the given format
	 * as a pattern to get the date
	 * @param format
	 * @return
	 */
    @GwtIncompatible
	public Date asDate(final String format) {
    	return StringConverter.asDate(_theString,format,
    								  null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Path}
	 * @return
	 */
	public Path asPath() {
		return StringConverter.asPath(_theString,null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Language
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Language}
	 * @return
	 */
	@GwtIncompatible
	public Language asLanguageFromCountryCode() {
		return StringConverter.asLanguageFromCountryCode(_theString,null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENUM
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends Enum<E>> E asEnumElement(final Class<E> enumType) {
		return StringConverter.asEnumElement(_theString,enumType,
											 null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link StringBuilder}
	 * @return
	 */
	public StringBuilder asStringBuilder() {
		return StringConverter.asStringBuilder(_theString,null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link StringBuffer}
	 * @return
	 */
	public StringBuffer asStringBuffer() {
		return StringConverter.asStringBuffer(_theString,null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR ARRAY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a char array
	 * @return
	 */
	public char[] asCharArray() {
		return StringConverter.asCharArray(_theString,null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  InputStream
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns an {@link InputStream} to the buffer, that's the {@link String} as an {@link InputStream}
	 * (the stream's byte charset is the System's default charset)
	 */
	@GwtIncompatible("IO is NOT supported by GWT")
	public InputStream asInputStream() {
		return StringConverter.asInputStream(_theString,Charset.defaultCharset());
	}
	/**
	 * Returns an {@link InputStream} to the buffer, that's the {@link String} as an {@link InputStream}
	 * the stream's byte charset is provided 
	 * @param charset 
	 */
	@GwtIncompatible("IO is NOT supported by GWT")
	public InputStream asInputStream(final Charset charset) {
		return StringConverter.asInputStream(_theString,charset);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  READER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Reader} to the buffer, that's the {@link String} as a {@link Reader}
	 */
	@GwtIncompatible("IO is NOT supported by GWT")
	public Reader asReader() {
		return StringConverter.asReader(_theString);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as an instance of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * <pre class="brush:java">
	 * 		MyType myType = wrapper.asType("...string representation...",
	 * 									   new Function<CharSequence,MyType>() {
	 * 											public MyType apply(final CharSequence str) {
	 * 												return ReflectionUtils.<MyType>createInstanceFromString(MyType.class,
	 *														 												value.toString());
	 *											}
	 *										},
	 *										MyType.class);
	 * </pre>
	 * @param str
	 * @param fromStringTransform a function that transforms the string to an instance of the given type
	 * @param defValue
	 * @return
	 */
	@GwtIncompatible
	private static <T> T _asType(final CharSequence str,
							     final Function<CharSequence,T> fromStringTransform,
							     final Class<T> type) {
		if (str == null) return null;
		T outValue = fromStringTransform.apply(str);
		return outValue;
	}
	/**
	 * Returns the given {@link CharSequence} as an instance of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * <pre class="brush:java">
	 * 		MyType myType = wrapper.asType()
	 * 							   .using(new Function<CharSequence,MyType>() {
	 * 											public MyType apply(final CharSequence str) {
	 * 												return ReflectionUtils.<MyType>createInstanceFromString(MyType.class,
	 *														 												value.toString());
	 *											}
	 *									   });
	 * </pre>
	 * @param fromStringTransform a function that transforms the string to an instance of the given type
	 * @param defValue
	 * @return	
	 */
	@GwtIncompatible
	public <T> StringConverterWrapperAsType<T> asType(final Class<T> type) {
		return new StringConverterWrapperAsType<T>(type);
	}
	@GwtIncompatible
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterWrapperAsType<T> {
		private final Class<T> _type;
		public T using(final Function<CharSequence,T> fromStringTransform) {
			return _asType(_theString,
						   fromStringTransform,
						   _type);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as an instance of the given oid type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param fromStringTransform a function that transforms the string to an instance of the given type
	 * @param defValue
	 * @return
	 */
	@GwtIncompatible("GWT does NOT supports reflection")
	private static <O extends OID> O _asOid(final CharSequence value,
										   final Function<CharSequence,O> fromStringTransform,
										   final Class<O> oidType) {
		return _asType(value,
					   fromStringTransform,
					   oidType);
	}
	/**
	 * Returns the given {@link CharSequence} as an instance of the given oid type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * <pre class="brush:java">
	 * 		MyOID myType = wrapper.asType()
	 * 							   .using(new Function<CharSequence,MyOID>() {
	 * 											public MyOID apply(final CharSequence str) {
	 * 												return MyOID.forId(str);
	 *											}
	 *									   })
	 *							   .orDefault(MyOID.forId("default"));
	 * </pre>
	 * @param fromStringTransform a function that transforms the string to an instance of the given type
	 * @param defValue
	 * @return
	 */
	@GwtIncompatible("GWT does NOT supports reflection")
	public <O extends OID> StringConverterWrapperAsType<O> asOid(final Class<O> oidType) {
		return new StringConverterWrapperAsType<O>(oidType);
	}
}
