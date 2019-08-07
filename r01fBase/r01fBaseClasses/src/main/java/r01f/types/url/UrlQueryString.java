package r01f.types.url;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.patterns.Memoized;
import r01f.patterns.Provider;
import r01f.types.ParametersParser;
import r01f.types.ParametersParserRegexBased;
import r01f.types.ParametersWrapperBase;
import r01f.util.types.collections.CollectionUtils;


/**
 * Helper type to build url-string-encoded parameters that encapsulates all the string building stuff offering an api
 * that isolates user from string concat errors
 * @see ParametersWrapperBase
 */
@Immutable
public class UrlQueryString 
	 extends ParametersWrapperBase<UrlQueryString> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlQueryString(final Map<String,String> params,
						  final ParametersParser paramsParser) {
		super(params,
			  paramsParser);
	}
	public UrlQueryString(final Map<String,String> params) {
		super(params);
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	private static ParametersWrapperFactory<UrlQueryString> FACTORY = new ParametersWrapperFactory<UrlQueryString>() {
																				@Override
																				public UrlQueryString createParametersWrapperFrom(final Map<String,String> params) {
																					return new UrlQueryString(params);
																				}
																		 };
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new instance from a param {@link Map}
	 * @param params
	 * @return
	 */
	public static UrlQueryString from(final Map<String,String> params) {
		return new UrlQueryString(params);
	}
	/**
	 * Creates a new instance form a params list
	 * @param params
	 * @return
	 */
	public static UrlQueryString fromParams(final Iterable<UrlQueryStringParam> params) {
		Map<String,String> paramsMap = Maps.newHashMap();
		for (UrlQueryStringParam param : params) {
			paramsMap.put(param.getName(),param.getValue());
		}
		return new UrlQueryString(paramsMap);
	}
	/**
	 * Creates a new instance form a params list
	 * @param params
	 * @return
	 */
	public static UrlQueryString fromParams(final Collection<UrlQueryStringParam> params) {
		if (CollectionUtils.isNullOrEmpty(params)) return new UrlQueryString(null);
		Map<String,String> paramsMap = Maps.newHashMapWithExpectedSize(params.size());
		for (UrlQueryStringParam param : params) {
			paramsMap.put(param.getName(),param.getValue());
		}
		return new UrlQueryString(paramsMap);
	}
	/**
	 * Creates a new instance form a params list
	 * @param params
	 * @return
	 */
	public static UrlQueryString fromParams(final UrlQueryStringParam... params) {
		if (CollectionUtils.isNullOrEmpty(params)) return new UrlQueryString(null);
		Map<String,String> paramsMap = Maps.newHashMapWithExpectedSize(params.length);
		for (UrlQueryStringParam param : params) {
			paramsMap.put(param.getName(),param.getValue());
		}
		return new UrlQueryString(paramsMap);
	}
	/**
	 * Creates a new instance form a full query string
	 * @param paramsStr
	 * @param paramsParser
	 * @return
	 */
	public static UrlQueryString fromParamsString(final String paramsStr,
												  final ParametersParser paramsParser) {
		String theParamStr = paramsStr != null 
						  && paramsStr.trim().startsWith("?") ? paramsStr.trim().substring(1) 
								  							  : paramsStr;
		return ParametersWrapperBase._loadFromString(FACTORY,paramsParser,
											  		 theParamStr,
											  		 false);	// do not decode param values
	}
	/**
	 * Creates a new instance form a full query string
	 * @param paramsStr
	 * @param paramsParser
	 * @return
	 */
	public static UrlQueryString fromUrlEncodedParamsString(final String paramsStr,
															final ParametersParser paramsParser) {
		String theParamStr = paramsStr != null && paramsStr.trim().startsWith("?") ? paramsStr.trim().substring(1) : paramsStr;
		return ParametersWrapperBase._loadFromString(FACTORY,paramsParser,
													 theParamStr,
													 true);	// decode param values
	}
	/**
	 * Creates a new instance form a full query string
	 * @param paramsStr
	 * @return
	 */
	public static UrlQueryString fromParamsString(final String paramsStr) {
		return UrlQueryString.fromParamsString(paramsStr,
											   new ParametersParserRegexBased());				// not gwt-compatible by default
	}
	/**
	 * Creates a new instance form a full query string
	 * @param paramsStr
	 * @return
	 */
	public static UrlQueryString fromUrlEncodedParamsString(final String paramsStr) {
		return UrlQueryString.fromUrlEncodedParamsString(paramsStr,
														 new ParametersParserRegexBased());	// not gwt-compatible by default
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	private final transient Memoized<Set<UrlQueryStringParam>> _qryStrParams = 
									new Memoized<Set<UrlQueryStringParam>>() {
											@Override
											public Set<UrlQueryStringParam> supply() {
												Set<UrlQueryStringParam> outParams = null;
												if (UrlQueryString.this.hasParams()) {
													outParams = Sets.newHashSetWithExpectedSize(_params.size());
													for (Map.Entry<String,String> me : _params.entrySet()) {
														outParams.add(new UrlQueryStringParam(me.getKey(),me.getValue()));
													}
												} 
												return outParams;
											}
									 };
	/**
	 * Returns the query string params
	 * @return
	 */
	public Set<UrlQueryStringParam> getQueryStringParams() {
		return _qryStrParams.get();
	}
	/**
	 * Return the query string params ordered by name
	 * @return
	 */
	public Set<UrlQueryStringParam> getQueryStringParamsOrdered() {
		Set<UrlQueryStringParam> unordered = this.getQueryStringParams();
		return CollectionUtils.hasData(unordered)
					? Sets.newLinkedHashSet(Ordering.from(new Comparator<UrlQueryStringParam>() {
																@Override
																public int compare(final UrlQueryStringParam p1,final UrlQueryStringParam p2) {
																	return p1.getName().compareTo(p2.getName());
																}
												   		  })
					    					 		.sortedCopy(unordered))
					: unordered;
	}
	/**
	 * Returns a query string param
	 * @param paramName
	 * @return
	 */
	public UrlQueryStringParam getQueryStringParam(final String paramName) {
		return FluentIterable.from(this.getQueryStringParams())
							 .filter(new Predicate<UrlQueryStringParam>() {
																	@Override
																	public boolean apply(final UrlQueryStringParam aParam) {
																		return aParam.getName().equals(paramName);
																	}
												  		  	  })
							 .first().orNull();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JOIN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an NEW instance of an {@link UrlQueryString} object joining 
	 * this query string with the given one
	 * @param other
	 * @return
	 */
	public UrlQueryString joinWith(final UrlQueryString other) {
		if (other == null) return this;
		return ParametersWrapperBase.join(FACTORY,
										  this,other);
	}
	/**
	 * Adds a param to the query string
	 * @param name
	 * @param value
	 * @return
	 */
	public UrlQueryString add(final String name,final String value) {
		return ParametersWrapperBase.join(FACTORY,
										  this,
										  name,value);
	}
	/**
	 * Adds a param to the query string
	 * @param name
	 * @param paramValueProvider
	 * @return
	 */
	public UrlQueryString add(final String name,final Provider<String> paramValueProvider) {
		return ParametersWrapperBase.join(FACTORY,
										  this,
										  name,paramValueProvider.provideValue());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a NEW instance of an {@link UrlQueryString} object without
	 * the given param (if it's present)
	 * @param paramName
	 * @return
	 */
	public UrlQueryString withoutParam(final String paramName) {
		boolean hasParam = this.hasParam(paramName);
		if (!hasParam ) return this;
		
		// create a new UrlQueryString without the given param
		Collection<UrlQueryStringParam> params = FluentIterable.from(this.getQueryStringParams())
															   .filter(new Predicate<UrlQueryStringParam>() {
																				@Override
																				public boolean apply(final UrlQueryStringParam param) {
																					return !param.getName().equals(paramName);
																				}
															   		   })
															   .toList();
		return UrlQueryString.fromParams(params);
	}
	/**
	 * Creates a NEW instance of an {@link UrlQueryString} object without
	 * the given param (if it's present)
	 * @param paramName
	 * @return
	 */
	@GwtIncompatible
	public UrlQueryString withoutParamsMatching(final Pattern p) {
		if (CollectionUtils.isNullOrEmpty(this.getQueryStringParams())) return this;
		
		// create a new UrlQueryString without the given param
		Collection<UrlQueryStringParam> params = FluentIterable.from(this.getQueryStringParams())
															   .filter(new Predicate<UrlQueryStringParam>() {
																				@Override
																				public boolean apply(final UrlQueryStringParam param) {
																					Matcher m = p.matcher(param.getName());
																					return !m.find();
																				}
															   		   })
															   .toList();
		return UrlQueryString.fromParams(params);
	}
}
