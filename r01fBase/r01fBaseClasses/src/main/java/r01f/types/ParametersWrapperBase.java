package r01f.types;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.patterns.Provider;
import r01f.util.types.collections.CollectionUtils;

/**
 * Helper type to build string-encoded parameters that encapsulates all the string building stuff by offering an api
 * that isolates user from string concat errors
 * The simplest usage is to add param by name and value:
 * <pre class="brush:java">
 *		ParameterStringWrapper qryStr = ParameterStringWrapper.create('&')	// use the & char as param separator
 *															  .addParam("param0","param0Value");
 * </pre>
 * But the paramValue normally comes from run-time values that must be evaluated to compose the paramValue
 * This type offers a method to add those types of param values:
 * <pre class="brush:java">
 *	ParameterStringWrapper qryStr = ParameterStringWrapper.create('&')	// use the & char as param separator
 *										.addParam("param1",,
 *												  new ParamValueProvider() {
 *															@Override 
 *															public String provideValue() {
 *																return Strings.of("{},{}")
 *																			  .customizeWith(someVar.getA(),someVar.getB())
 *																			  .asString();
 *															}
 *												  })
 * <pre>
 * 
 * To get the string from the params, simply call:
 * <pre class="brush:java">
 * 		ParameterStringWrapper qryStrWrap = ...
 * 		String queryString = qryStrWrap.asString();
 * </pre>
 * 
 * A {@link ParametersWrapperBase} can be created from the query string:
 * <pre class="brush:java">
 * 		ParameterStringWrapper qryStr2 = ParameterStringWrapper.fromParamString("param1=a,b&param2=myParam2-a");
 * </pre> 
 */
@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class ParametersWrapperBase<SELF_TYPE extends ParametersWrapperBase<SELF_TYPE>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final char DEFAULT_PARAM_SPLIT_CHAR = '&';
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a {@link ParametersWrapperBase} object from it's elements 
	 */
	public interface ParametersWrapperFactory<PW extends ParametersWrapperBase<?>> {
		public PW createParametersWrapperFrom(final Map<String,String> params);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected static <PW extends ParametersWrapperBase<PW>> PW _loadFromString(final ParametersWrapperFactory<PW> pwFactory,final ParametersParser parser,
																			   final String paramsStr,
								   											   final boolean decodeParamValues) {
		if (Strings.isNullOrEmpty(paramsStr)) return null;
		
		Map<String,String> paramMap = parser.parse(paramsStr,
												   decodeParamValues);
		return pwFactory.createParametersWrapperFrom(paramMap);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The param join char to be used when serializing the parameters
	 */
	@Getter(AccessLevel.PROTECTED) private final char _paramSplitChar;
	/**
	 * Parses the params
	 */
	@Getter(AccessLevel.PROTECTED) private final ParametersParser _paramsParser;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Internal map holding the params
	 */
	@Getter protected final ImmutableMap<String,String> _params;
	
	public boolean hasParams() {
		return _params != null && _params.size() > 0;
	}
	/**
	 * @return the param entries ordered
	 */
	protected Set<Map.Entry<String,String>> _paramEntriesOrdered() {
		if (CollectionUtils.isNullOrEmpty(_params)) return Sets.newLinkedHashSet();
		
		Set<String> keysOrdered = Sets.newLinkedHashSet(Ordering.natural()
																.sortedCopy(_params.keySet()));
		Set<Map.Entry<String,String>> outEntries = Sets.newLinkedHashSetWithExpectedSize(keysOrdered.size());
		for (final String key : keysOrdered) {
			final String val = _params.get(key);
			outEntries.add(new AbstractMap.SimpleEntry<String,String>(key,val));
		}
		return outEntries;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ParametersWrapperBase(final Map<String,String> params) {
		_paramSplitChar = DEFAULT_PARAM_SPLIT_CHAR;
		_paramsParser = new ParametersParserRegexBased();
		if (CollectionUtils.hasData(params)) {
			_params = ImmutableMap.<String,String>copyOf(FluentIterable.from(params.entrySet())
																	   .filter(new Predicate<Map.Entry<String,String>>() {
																						@Override
																						public boolean apply(Entry<String,String> entry) {
																							return entry != null
																								&& entry.getKey() != null
																								&& entry.getValue() != null;
																						}
																				})
																	   .transform(new Function<Map.Entry<String,String>,Map.Entry<String,String>>() {
																						@Override
																						public Entry<String, String> apply(Entry<String, String> t) {
																							t.setValue(t.getValue().replaceAll(String.valueOf(DEFAULT_PARAM_SPLIT_CHAR),
																															   _paramsParser.getParamValueEncoderDecoder().encodeValue(String.valueOf(DEFAULT_PARAM_SPLIT_CHAR))));
																							return t;
																						}
																	   })
																	   .toList());
		} else {
			_params = null;
		}
	}
	public ParametersWrapperBase(final Map<String,String> params,
								 final ParametersParser paramsParser) {
		_paramSplitChar = DEFAULT_PARAM_SPLIT_CHAR;
		_paramsParser = paramsParser;
		_params = params != null ? ImmutableMap.<String,String>copyOf(params)
								 : ImmutableMap.<String,String>of();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JOIN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a param from its name and value
	 * @param pwType the type of the {@link ParametersWrapperBase} instance
	 * @param paramWrapper 
	 * @param paramName
	 * @param paramValue
	 * @return
	 */
	public static <PW extends ParametersWrapperBase<PW>> PW join(final ParametersWrapperFactory<PW> pwFactory,
																 final PW paramWrapper,
																 final String paramName,final String paramValue) {
		if (Strings.isNullOrEmpty(paramValue)) return paramWrapper;
		
		Map<String,String> newParams = Maps.newHashMapWithExpectedSize(paramWrapper.getParams().size() + 1);
		newParams.putAll(paramWrapper.getParams());
		newParams.put(paramName,paramValue);
		return pwFactory.createParametersWrapperFrom(newParams);
	}
	/**
	 * Adds a param from its name and value which is provided by a {@link ParamValueProvider}
	 * @param pwType the type of the {@link ParametersWrapperBase} instance
	 * @param paramWrapper 
	 * @param paramValueProvider the param value provider to be used to get the value at runtime
	 * @return
	 */
	public static <PW extends ParametersWrapperBase<PW>> PW join(final ParametersWrapperFactory<PW> pwFactory,
														   	  	 final PW paramWrapper,
														   	  	 final String paramName,final Provider<String> paramValueProvider) {
		String paramValue = paramValueProvider.provideValue();
		return ParametersWrapperBase.join(pwFactory,
					 					  paramWrapper,
					 					  paramName,paramValue);
	}
	public static <PW extends ParametersWrapperBase<PW>> PW join(final ParametersWrapperFactory<PW> pwFactory,
																 final PW paramWrapper,
																 final PW otherParamWrapper) {
		if (otherParamWrapper == null) return paramWrapper;
		if (paramWrapper == null) return otherParamWrapper;
		
		Map<String,String> params = paramWrapper.getParams();
		Map<String,String> otherParams = otherParamWrapper.getParams();
		Map<String,String> allParams = Maps.newHashMapWithExpectedSize(params.size() + (otherParams != null ? otherParams.size() : 0));
		allParams.putAll(params);
		if (otherParams != null && !otherParams.isEmpty()) allParams.putAll(otherParams);
		
		return pwFactory.createParametersWrapperFrom(allParams);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ACCESSORS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the query string contains a given param
	 * @param paramName
	 * @return
	 */
	public boolean hasParam(final String paramName) {
		if (_params == null || Strings.isNullOrEmpty(paramName)) return false;
		return _params != null ? _params.containsKey(paramName)
							   : false;
	}
	/**
	 * Returns a param value from it's name
	 * @param paramName
	 * @return
	 */
	public String getParamValue(final String paramName) {
		if (Strings.isNullOrEmpty(paramName)) return null;
		return _params != null ? _params.get(paramName)
							   : null;
	}
	/**
	 * Returns a param in the format to be placed at the query string (paramName=paramValue)
	 * NO url encoding is made
	 * @param paramName
	 * @return
	 */
	public String serializeParamNameAndValue(final String paramName,
											 final boolean encodeParamValue) {
		return this.hasParams() ? _serializeParamNameAndValue(paramName,_params.get(paramName),
															  encodeParamValue)
								: null;
	}
	private String _serializeParamNameAndValue(final String paramName,final String paramValue,
											   final boolean encodeParamValue) {
		String outValueFormated = null;
		if (paramValue != null) {
			String theParamValue = encodeParamValue && _paramsParser != null ? _paramsParser.getParamValueEncoderDecoder()
																							.encodeValue(paramValue)
																	 		 : paramValue;
			outValueFormated = new StringBuilder()
										.append(paramName).append("=").append(theParamValue)
										.toString();
		} else {
			outValueFormated = paramName;
		}
		return outValueFormated;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the params string built from the params encoding the param 
	 * @return
	 */
	public String asStringEncodingParamValues() {
		return this.asString(true);
	}
	/**
	 * Returns the params string built from the params NOT encoding the param values
	 * @return
	 */
	public String asStringNotEncodingParamValues() {
		return this.asString(false);
	}
	/**
	 * Returns the params string built from the params
	 * @return
	 */
	public String asString() {
		return this.asString(false);
	}
	/**
	 * Returns the params string built from the params encoding the param values as specified
	 * @param encodeParamValues
	 * @return
	 */
	public String asString(final boolean encodeParamValues) {
		String outStr = null;
		if (this.hasParams()) {
			StringBuilder paramsSB = new StringBuilder();
			// BEWARE!!! _paramEntriesOrdered() is used instead _params.entrySet()
			//			 ... this way, the query string params is ORDERED by param name
			//			 this is VERY IMPORTANT when comparing queryStrings
			for (Iterator<Map.Entry<String,String>> meIt = _paramEntriesOrdered().iterator(); meIt.hasNext(); ) {
				Map.Entry<String,String> me = meIt.next();
				
				String paramNameAndValue = _serializeParamNameAndValue(me.getKey(),me.getValue(),
																	   encodeParamValues);
				
				if (paramNameAndValue != null && paramNameAndValue.length() > 0) {
					paramsSB.append(paramNameAndValue);
					if (meIt.hasNext()) paramsSB.append(_paramSplitChar);	// params separator
				}
			}
			outStr = paramsSB.toString();
		}
		return outStr;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final SELF_TYPE other) {
		return this.equals(other);
	}
	public boolean isNOT(final SELF_TYPE other) {
		return !this.equals(other);
	}
	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (this == other) return true;
		if (other instanceof ParametersWrapperBase
		 && other.getClass() == this.getClass()) {
				ParametersWrapperBase<?> pw = (ParametersWrapperBase<?>)other;
				Map<String,String> otherParams = pw.getParams();
				MapDifference<String,String> diff = Maps.difference(_params,otherParams);
				return diff.entriesInCommon().size() == _params.size()		
					&& diff.entriesInCommon().size() == otherParams.size();
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.asString().hashCode();
	}
}
