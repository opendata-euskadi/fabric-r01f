package r01f.objectstreamer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

@Target({
			ElementType.FIELD
		})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation @MarshallAnnotation
public @interface MarshallField {

	public final static String MARKER_FOR_DEFAULT = "";

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Explicitly sets the name the field is mashalled to/from
	 */
	public String as() default MARKER_FOR_DEFAULT;
	/**
	 * namespace
	 */
	public String namespace() default MARKER_FOR_DEFAULT;
	/**
	 * If the annotated field is an string, does the String content have to be escaped?
	 */
	public boolean escape() default false;
//	/**
//	 * The annotated field is marshalled as a raw value
//	 * (this is the same as @JsonRaw jackson annotation)
//	 * If the value is an XML string it'll NOT be parsed
//	 */
//	public boolean raw() default false;
	/**
	 * how does the date will be formated if the annotated field is a date
	 */
	public MarshallDateFormat dateFormat() default @MarshallDateFormat;
	/**
	 * How to marshall when the field is a collection-like field (Collection / Map)
	 */
	public MarshallCollectionField whenCollectionLike() default @MarshallCollectionField;
	/**
	 * How to marshall when xml
	 */
	public MarshallFieldAsXml whenXml() default @MarshallFieldAsXml();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public @interface MarshallFieldAsXml {
		public boolean attr() default false;
		public boolean asParentElementValue() default false;
		public String collectionElementName() default MARKER_FOR_DEFAULT;
	}
	public @interface MarshallCollectionField {
		public boolean useWrapping() default true;		// can be used to explicitly disable wrapping
	}
	public @interface MarshallDateFormat {
		public DateFormat use() default DateFormat.TIMESTAMP;
		public String format() default MARKER_FOR_DEFAULT;
		public String timezone() default "";
	}
	public enum DateFormat {
		EPOCH,
		ISO8601,
		TIMESTAMP,
		CUSTOM;
	}
}
