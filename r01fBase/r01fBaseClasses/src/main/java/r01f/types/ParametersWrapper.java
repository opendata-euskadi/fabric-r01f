package r01f.types;

import java.util.Map;

import r01f.annotations.Immutable;
import r01f.patterns.Provider;

/**
 * Helper type to build string-encoded parameters that encapsulates all the string building stuff offering an api
 * that isolates user from string concat errors
 */
@Immutable
public class ParametersWrapper 
	 extends ParametersWrapperBase<ParametersWrapper> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public ParametersWrapper(final Map<String,String> params) {
		super(params);
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	private static ParametersWrapperFactory<ParametersWrapper> FACTORY = new ParametersWrapperFactory<ParametersWrapper>() {
																				@Override
																				public ParametersWrapper createParametersWrapperFrom(final Map<String,String> params) {
																					return new ParametersWrapper(params);
																				}
																		 };
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new instance form a full params string
	 * @param paramsStr
	 * @param paramsParser
	 * @return
	 */
	public static ParametersWrapper fromParamsString(final String paramsStr,
													 final ParametersParser paramsParser) {
		return ParametersWrapperBase._loadFromString(FACTORY,paramsParser,
											  		 paramsStr,
											  		 false);
	}
	/**
	 * Creates a new instance form a full params string
	 * @param paramsStr
	 * @return
	 */
	public static ParametersWrapper fromParamsString(final String paramsStr) {
		return ParametersWrapper.fromParamsString(paramsStr,
											  	  new ParametersParserRegexBased());	// not gwt-compatible
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Joins this params with the new one
	 * BEWARE that this type is immutable so a new ParametersWrapper instance is created
	 * @param paramName
	 * @param paramValue
	 * @return
	 */
	public ParametersWrapper join(final String paramName,final String paramValue) {
		return ParametersWrapperBase.join(FACTORY,
					 					  this,
					 					  paramName,paramValue);	
	}
	/**
	 * Joins this params with the new one
	 * BEWARE that this type is immutable so a new ParametersWrapper instance is created
	 * @param paramName
	 * @param paramValueProvider
	 * @return
	 */
	public ParametersWrapper join(final String paramName,final Provider<String> paramValueProvider) {
		return ParametersWrapperBase.join(FACTORY,
					 					  this,
					 					  paramName,paramValueProvider);	
	}
}
