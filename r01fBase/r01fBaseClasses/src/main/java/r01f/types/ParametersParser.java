package r01f.types;

import java.util.Map;

/**
 * Parameters parser inteface 
 */
public interface ParametersParser {
	public ParamValueEncoderDecoder getParamValueEncoderDecoder();
	public Map<String,String> parse(final String paramsStr,
									final boolean decodeParamValues);
}