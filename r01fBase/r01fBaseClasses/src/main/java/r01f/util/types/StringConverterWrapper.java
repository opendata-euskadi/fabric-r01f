package r01f.util.types;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Date;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.enums.EnumWithCode;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.types.Path;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.util.types.StringConverter.StringConverterFilter;

@RequiredArgsConstructor
public class StringConverterWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  FINAL
/////////////////////////////////////////////////////////////////////////////////////////
	private final CharSequence _theString;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterDefaultOrThrowStep<T> {
		private final T _value;
		private final T _def;
		
		public T orDefault() {
			return _value != null ? _value : _def;
		}
		public T orDefault(final T def) {
			return _value != null ? _value
								  : def != null ? def : _def;
		}
		public T orNull() {
			return _value != null ? _value
								  : null;
		}
		public T orThrow() {
			return this.orThrow(null);
		}
		public T orThrow(final String msg,Object... params) {
			if (_value == null) throw new IllegalArgumentException(Strings.isNOTNullOrEmpty(msg) ? Strings.customized(msg,params) 
																								 : "Null or empty string");
			return _value;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link String}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<String> asString() {
		return new StringConverterDefaultOrThrowStep<String>(_theString != null ? _theString.toString() : null,
															 null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link String} applying the filter
	 * @param filter
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<String> asString(final StringConverterFilter filter) {
		return new StringConverterDefaultOrThrowStep<String>(_theString != null ? filter.filter(_theString.toString()) : null,
															 null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Url
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Url}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Url> asUrl() {
		return new StringConverterDefaultOrThrowStep<Url>(_theString != null ? Url.from(_theString.toString()) : null,
														  null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link UrlPath}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<UrlPath> asUrlPath() {
		return new StringConverterDefaultOrThrowStep<UrlPath>(_theString != null ? UrlPath.from(_theString.toString()) : null,
														      null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOLEAN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a boolean
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Boolean> asBoolean() {
		return new StringConverterDefaultOrThrowStep<Boolean>(StringConverter.asBoolean(_theString,null),
															  false);	// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BYTE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a byte
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Byte> asByte() {
		return new StringConverterDefaultOrThrowStep<Byte>(StringConverter.asByte(_theString,null),
												 		   new Byte((byte)'\u0000'));	// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a char
     * @return
     */
    public StringConverterDefaultOrThrowStep<Character> asChar() {
		return new StringConverterDefaultOrThrowStep<Character>(StringConverter.asChar(_theString,null),
												 				new Character('\u0000'));	// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DOUBLE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a double
     * @return
     */
    public StringConverterDefaultOrThrowStep<Double> asDouble() {
    	return new StringConverterDefaultOrThrowStep<Double>(StringConverter.asDouble(_theString,null),
												 			 0D);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FLOAT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a float
     * @return
     */
    public StringConverterDefaultOrThrowStep<Float> asFloat() {
    	return new StringConverterDefaultOrThrowStep<Float>(StringConverter.asFloat(_theString,null),
												 		    0F);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  INTEGER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as an integer
     * @return
     */
    public StringConverterDefaultOrThrowStep<Integer> asInteger() {
    	return new StringConverterDefaultOrThrowStep<Integer>(StringConverter.asInteger(_theString,null),
												 			  0);		// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  LONG
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a long
     * @return
     */
    public StringConverterDefaultOrThrowStep<Long> asLong() {
    	return new StringConverterDefaultOrThrowStep<Long>(StringConverter.asLong(_theString,null),
												 		   0L);			// default value
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  SHORT
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the wrapped {@link CharSequence} as a short
     * @return
     */
    public StringConverterDefaultOrThrowStep<Short> asShort() {
    	return new StringConverterDefaultOrThrowStep<Short>(StringConverter.asShort(_theString,null),
												 			(short)0);		// default value
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
	public StringConverterDefaultOrThrowStep<Date> asDate(final String format) {
    	return new StringConverterDefaultOrThrowStep<Date>(StringConverter.asDate(_theString,format,
    																			  null),
    													   new Date());		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Path}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<Path> asPath() {
		return new StringConverterDefaultOrThrowStep<Path>(StringConverter.asPath(_theString,null),
    													   null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Language
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link Language}
	 * @return
	 */
	@GwtIncompatible
	public StringConverterDefaultOrThrowStep<Language> asLanguageFromCountryCode() {
		return new StringConverterDefaultOrThrowStep<Language>(StringConverter.asLanguageFromCountryCode(_theString,null),
    													   	   Language.ENGLISH);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENUM
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends Enum<E>> StringConverterDefaultOrThrowStep<E> asEnumElement(final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<E>(StringConverter.asEnumElement(_theString,enumType,
																					  null),
    													null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends EnumWithCode<String,E>> StringConverterDefaultOrThrowStep<E> asEnumElementFromStringCode(final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<E>(StringConverter.asEnumElementFromStringCode(_theString,enumType,
																					  				null),
    													null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends EnumWithCode<Integer,E>> StringConverterDefaultOrThrowStep<E> asEnumElementFromIntCode(final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<E>(StringConverter.asEnumElementFromIntCode(_theString,enumType,
																					  			 null),
    													null);		// default value
	}
	/**
	 * Returns the wrapped {@link CharSequence} as an {@link Enum} of the given type
	 * @param enumType
	 * @return
	 */
	public <E extends EnumWithCode<Long,E>> StringConverterDefaultOrThrowStep<E> asEnumElementFromLongCode(final Class<E> enumType) {
		return new StringConverterDefaultOrThrowStep<E>(StringConverter.asEnumElementFromLongCode(_theString,enumType,
																					  			 null),
    													null);		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link StringBuilder}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<StringBuilder> asStringBuilder() {
		return new StringConverterDefaultOrThrowStep<StringBuilder>(StringConverter.asStringBuilder(_theString,null),
    													   	   		new StringBuilder());		// default value
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the wrapped {@link CharSequence} as a {@link StringBuffer}
	 * @return
	 */
	public StringConverterDefaultOrThrowStep<StringBuffer> asStringBuffer() {
		return new StringConverterDefaultOrThrowStep<StringBuffer>(StringConverter.asStringBuffer(_theString,null),
    													   	   	   new StringBuffer());		// default value
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
	 *										new MyType("default"),
	 *										MyType.class);
	 * </pre>
	 * @param value
	 * @param fromStringTransform a function that transforms the string to an instance of the given type
	 * @param defValue
	 * @return
	 */
	private static <T> T _asType(final CharSequence value,
							   	 final Function<CharSequence,T> fromStringTransform,
							   	 final T defValue,final Class<T> type) {
		if (value == null) return defValue;
		T outValue = fromStringTransform.apply(value);
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
	 *									   })
	 *							   .orDefault(new MyType("default"));
	 * </pre>
	 * @param fromStringTransform a function that transforms the string to an instance of the given type
	 * @param defValue
	 * @return	
	 */
	public <T> StringConverterWrapperAsType<T> asType(final Class<T> type) {
		return new StringConverterWrapperAsType<T>(type);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterWrapperAsType<T> {
		private final Class<T> _type;
		public StringConverterWrapperAsTypeDefault<T> using(final Function<CharSequence,T> fromStringTransform) {
			return new StringConverterWrapperAsTypeDefault<T>(_type,fromStringTransform);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class StringConverterWrapperAsTypeDefault<T> {
		private final Class<T> _type;
		private final Function<CharSequence,T> _fromStringTransform;
		
		public T orDefault(final T def) {
			return _asType(_theString,_fromStringTransform,
					  	   def,_type);
		}
		public T orNull() {
			return _asType(_theString,_fromStringTransform,
							null,_type);		// default value
		}
		public T orThrow() {
			return this.orThrow(null);
		}
		public T orThrow(final String msg,Object... params) {
			if (_theString == null) throw new IllegalArgumentException(Strings.isNOTNullOrEmpty(msg) ? Strings.customized(msg,params) 
																									 : "Null or empty string");
			return this.orDefault(null);
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
										   final O defValue,final Class<O> oidType) {
		return _asType(value,
					   fromStringTransform,
					   defValue,oidType);
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
	 * @param oidType
	 * @return
	 */
	@GwtIncompatible("GWT does NOT supports reflection")
	public <O extends OID> StringConverterWrapperAsType<O> asOid(final Class<O> oidType) {
		return new StringConverterWrapperAsType<O>(oidType);
	}
}
