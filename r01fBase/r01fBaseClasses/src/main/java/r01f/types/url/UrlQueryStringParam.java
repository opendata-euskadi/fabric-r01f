package r01f.types.url;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.StringConverter.StringConverterFilter;
import r01f.util.types.StringConverterWrapper;
import r01f.util.types.StringEncodeUtils;
import r01f.util.types.Strings;

@ConvertToDirtyStateTrackable
@MarshallType(as="param")
@Immutable
@Accessors(prefix="_")
public class UrlQueryStringParam
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 2469798253802346787L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter protected final String _name;

	@MarshallField(as="value",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter protected final String _value;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlQueryStringParam(@JsonProperty("name") @MarshallFrom("name") final String name,
							   @JsonProperty("value") @MarshallFrom("value") final String value) {
		_name = name;
		_value = value;
	}
	public UrlQueryStringParam(final String name,final CanBeRepresentedAsString value) {
		this(name,
			 value != null ? value.asString() : null);
	}
	public static UrlQueryStringParam of(final String paramName,final String paramValue) {
		UrlQueryStringParam outParam = new UrlQueryStringParam(paramName,paramValue);
		return outParam;
	}
	public static UrlQueryStringParam of(final String name,final CanBeRepresentedAsString value) {
		return UrlQueryStringParam.of(name,
									  value != null ? value.asString() : null);
	}
	public static UrlQueryStringParam from(final String paramAndValue) {
		String[] paramAndValueSplitted = paramAndValue.split("=");
		if (paramAndValueSplitted.length == 2) {
			return UrlQueryStringParam.of(paramAndValueSplitted[0],
										  StringEncodeUtils.urlDecodeNoThrow(paramAndValueSplitted[1])
										  				   .toString());
		} else if (paramAndValueSplitted.length == 1) {
			return UrlQueryStringParam.of(paramAndValueSplitted[0],(String)null);
		} else {
			// sometimes a param value includes = (ie: W=sco_serie=11+and+sco_freun=20100421+order+by+sco_freun,sco_nasun)
			String paramName = paramAndValueSplitted[0];
			StringBuilder paramValue = new StringBuilder();
			for (int i=1; i < paramAndValueSplitted.length; i++) {
				paramValue.append(paramAndValueSplitted[i]);
				if (i < paramAndValueSplitted.length-1) paramValue.append("=");
			}
			return UrlQueryStringParam.of(paramName,paramValue.toString());
		}
	}
	public static UrlQueryStringParam of(final Language lang) {
		return UrlQueryStringParam.of("lang",lang.name());
	}
	public static UrlQueryStringParam of(final String paramName,final Language lang) {
		return new UrlQueryStringParam(paramName,lang.name());
	}
	public static UrlQueryStringParam of(final String paramName,final boolean val) {
		return new UrlQueryStringParam(paramName,Boolean.toString(val));
	}
	public static UrlQueryStringParam of(final String paramName,final int num) {
		return new UrlQueryStringParam(paramName,Integer.toString(num));
	}
	public static UrlQueryStringParam of(final String paramName,final long num) {
		return new UrlQueryStringParam(paramName,Long.toString(num));
	}
	public static UrlQueryStringParam of(final String paramName,final double num) {
		return new UrlQueryStringParam(paramName,Double.toString(num));
	}
	public static UrlQueryStringParam of(final String paramName,final short num) {
		return new UrlQueryStringParam(paramName,Short.toString(num));
	}
	public static UrlQueryStringParam of(final String paramName,final float num) {
		return new UrlQueryStringParam(paramName,Float.toString(num));
	}
	public static UrlQueryStringParam of(final String paramName,final Date date) {
		return new UrlQueryStringParam(paramName,Long.toString(Dates.asEpochTimeStamp(date)));
	}
	@GwtIncompatible
	public static UrlQueryStringParam of(final String paramName,final Range<Date> dateRange) {
		return new UrlQueryStringParam(paramName,dateRange.asString());
	}
	@GwtIncompatible
	public static UrlQueryStringParam of(final String paramName,final com.google.common.collect.Range<Date> dateRange) {
		return UrlQueryStringParam.of(paramName,new Range<Date>(dateRange));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SANITIZE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sanitizes the query string param
	 * It's usually used with OWASP like:
	 * <pre class='brush:java'>
	 *		protected static PolicyFactory policy = Sanitizers.FORMATTING
	 *											  		.and(Sanitizers.BLOCKS);
	 *	//										  		.and(Sanitizers.LINKS);		// do NOT escape @ character 
	 *		protected static StringConverterFilter SANITIZER_FILTER = (untrustedHtml) -> {
	 *																		String safeHtml = policy.sanitize(untrustedHtml);																					
	 *																		return safeHtml.replace("&#64;","@");		// mega-ñapa for emails
	 *																   }
	 * </pre>
	 * @param sanitizer
	 * @return
	 */
	public UrlQueryStringParam sanitizeUsing(final StringConverterFilter sanitizer) {
		if (Strings.isNullOrEmpty(_value)) return this;		// nothing to sanitize
		String safe = sanitizer.filter(_value);
		return UrlQueryStringParam.of(_name,safe);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the param contains data
	 */
	public boolean hasData() {
		return Strings.isNOTNullOrEmpty(_value);
	}
	/**
	 * @return the param value
	 */
	public String valueAsString() {
		return _value;
	}
	/**
	 * @return the param value url encoded
	 */
	public String valueAsStringUrlEncoded() {
		return StringEncodeUtils.urlEncodeNoThrow(_value)
								.toString();
	}
	/**
	 * @return a wrapper that provides easy access to parameter value
	 */
	public StringConverterWrapper value() {
		String paramValue = this.valueAsString();
		return Strings.isNOTNullOrEmpty(paramValue) ? new StringConverterWrapper(paramValue)
													: new StringConverterWrapper(null);
	}
	/**
	 * The param as name=value with the value encoded
	 * @return
	 */
	public String asStringUrlEncoded() {
		return Strings.customized("{}={}",
					  	          _name,StringEncodeUtils.urlEncodeNoThrow(_value));
	}
	/**
	 * The param as name=value with the value encoded as specified by the param
	 * @param encodeValues
	 * @return
	 */
	public String asString(final boolean encodeValues) {
		return encodeValues ? this.asStringUrlEncoded()
							: this.asString();
	}
	@Override
	public String asString() {
		return Strings.customized("{}={}",
					  		      _name,_value);
	}
	@Override
	public String toString() {
		return this.asString();

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODDE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if ( !(obj instanceof UrlQueryStringParam) ) return false;
		UrlQueryStringParam other = (UrlQueryStringParam)obj;
		return _equals(this.getName(),other.getName())
			&& _equals(this.getValue(),other.getValue());
	}
	private boolean _equals(final String a,final String b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_name,_value);
	}
}
