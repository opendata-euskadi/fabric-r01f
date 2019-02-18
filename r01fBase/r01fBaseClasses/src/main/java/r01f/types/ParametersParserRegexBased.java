package r01f.types;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * Default parameters parser using regex
 */
public class ParametersParserRegexBased 
     extends ParametersParserBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static final Pattern DEFAULT_PARAM_VALUE_SPLIT_PATTERN = Pattern.compile("([^=]+)=(.+)");
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Map<String,String> parse(final String paramsStr,
									final boolean decodeParamValues) {
		if (Strings.isNullOrEmpty(paramsStr)) return null;
		
		Map<String,String> paramMap = Maps.newHashMap();
		Iterable<String> params = Splitter.on(ParametersWrapperBase.DEFAULT_PARAM_SPLIT_CHAR)
										  .split(paramsStr.trim());
		Iterator<String> paramsIt = params.iterator();
		while(paramsIt.hasNext()) {
			String param = paramsIt.next();
			Matcher m = DEFAULT_PARAM_VALUE_SPLIT_PATTERN.matcher(param);
			if (m.find()) {
				String paramName = m.group(1).trim();
				String paramValue = m.group(2).trim();
				String theParamValue = decodeParamValues ? this.getParamValueEncoderDecoder().decodeValue(paramValue)
														 : paramValue;
				paramMap.put(paramName,theParamValue);
			} else {
				String paramName = param;
				String theParamValue = "";
				paramMap.put(paramName,theParamValue);
			}
		}
		return paramMap;
	}
}
