package r01f.util.types;

import java.util.Map;
import java.util.Properties;

import r01f.util.types.collections.CollectionUtils;

/**
 * Utils used to customize an string
 */
public class StringCustomizeUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING SUBSTITUTION
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Replaces all string occurrences inside an string
     * @param inStr 
     * @param replacedStr the string to be replaced
     * @param replacingStr the replacing string
     * @return the string with all replaces
     */
    public static String replaceString(final String inStr,
    								   final String replacedStr,final String replacingStr) {
        // Es una funcion un poco rupestre pero... no hay tiempo
        StringBuffer outBuff = new StringBuffer("");
        int p1 = 0;
        int p2 = 0;
        boolean substitution = false;
        do {
            substitution = false;

            p2 = inStr.indexOf(replacedStr, p1);
            if (p2 >= p1) {
                if (p2 > p1) outBuff.append(inStr.substring(p1, p2));
                outBuff.append(replacingStr);
                p1 = p2 + replacedStr.length();
                substitution = true;
            }
        } while (substitution);
        // the rest of the string
        if (p1 < inStr.length()) outBuff.append(inStr.substring(p1));
        return outBuff.toString();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  VARIABLE INTERPOLATION USING String.replaceAll()
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Interpolates vas inside a string with their values
     * The vars are delimited by a marker char like $varName$
     * BEWARE!!	Regular expression are used so the var values MUST be escaped!
     * @param inStr the string to interpolate
     * @param varDelim the var delimiter string
     * @param varValues a Map containing the var values indexed by var name
     * @return the interpolated string
     */
    public static String replaceVariableValues(final String inStr,
    										   final char varDelim,
    										   final Properties varValues) {
    	return StringCustomizeUtils.replaceVariableValues(inStr, 
    													  varDelim,
    													  CollectionUtils.toMap(varValues));
    }
    /**
     * Interpolates vas inside a string with their values
     * The vars are delimited by a marker char like $varName$
     * BEWARE!!	Regular expression are used so the var values MUST be escaped!
     * @param inStr the string to interpolate
     * @param varDelim the var delimiter string
     * @param varValues a Map containing the var values indexed by var name
     * @return the interpolated string
     */
    public static String replaceVariableValues(final String inStr,
    										   final char varDelim,
    										   final Map<String,String> varValues) {
        return StringCustomizeUtils.replaceVariableValues(inStr,
        												  varDelim,
        												  varValues,
        												  false);	// do not interpolate
    }
    /**
     * Interpolates vas inside a string with their values
     * The vars are delimited by a marker char like $varName$
     * BEWARE!!	Regular expression are used so the var values MUST be escaped!
     * @param inStr the string to interpolate
     * @param varDelimStar the var delimiter start char
     * @param varDelimEnd the var delimiter end char
     * @param varValues a Map containing the var values indexed by var name
     * @return the interpolated string
     */
    public static String replaceVariableValues(final String inStr,
    										   final char varDelimStart,final char varDelimEnd,
    										   final Map<String,String> varValues) {
        return StringCustomizeUtils.replaceVariableValues(inStr,
        												  varDelimStart,varDelimEnd,
        												  varValues,
        												  false);	// do not interpolate
    }
    /**
     * Interpolates vas inside a string with their values
     * The vars are delimited by a marker char like $varName$
     * BEWARE!!	The var name is searched using regular expressions so they MUST NOT contain regEx reserved chars or they MUST be escaped!
     * 			(use _filterRegexChars)
     * @param inStr the string to interpolate
     * @param varDelim the var delimiter char
     * @param varValues a Map containing the var values indexed by var name
     * @param interpolateVarValues interpolate the var values before replacing the var
     * @return the interpolated string
     */
    public static String replaceVariableValues(final String inStr,
    										   final char varDelim,
    										   final Map<String,String> varValues,
    										   final boolean interpolateVarValues) {
    	return StringCustomizeUtils.replaceVariableValues(inStr,
    													  varDelim,varDelim,	// the same char is used as start & end delimiter
    													  varValues,
    													  interpolateVarValues);
    }
    /**
     * Interpolates vas inside a string with their values
     * The vars are delimited by a marker char like $varName$
     * BEWARE!!	The var name is searched using regular expressions so they MUST NOT contain regEx reserved chars or they MUST be escaped!
     * 			(use _filterRegexChars)
     * @param inStr the string to interpolate
     * @param varDelimStart the var delimiter start char
     * @param varDelimEnd the var delimiter end char
     * @param varValues a Map containing the var values indexed by var name
     * @param interpolateVarValues interpolate the var values before replacing the var
     * @return the interpolated string
     */
    public static String replaceVariableValues(final String inStr,
    										   final char varDelimStart,final char varDelimEnd,
    										   final Map<String,String> varValues,
    										   final boolean interpolateVarValues) {
        // Escape the delimiter char because it can contain a regular expression-reserved char
        String outString = inStr;
        if (varValues != null) {
          	for (Map.Entry<String,String> me : varValues.entrySet()) {          		
                String currStrToReplace = varDelimStart + me.getKey() + varDelimEnd;
                String currVarValue = me.getValue();
                if (currVarValue == null) continue;
                               
                if (interpolateVarValues) {	// the var values might also contain variables  
                	int varDelimStartPos = currVarValue.indexOf(varDelimStart);
                	int varDelimEndPos = currVarValue.lastIndexOf(varDelimEnd); 
					if (varDelimStartPos >= 0 && varDelimEndPos > varDelimStartPos+1) {
	                    // Interpolate the var value
	                    currVarValue = StringCustomizeUtils.replaceVariableValues(currVarValue,
	                    														  varDelimStart,varDelimEnd,
	                    														  varValues,
	                    														  false);	// do not interpolate (not recursive)
					}
                }
                // ReplaceAll: the first param is a REGULAR EXPRESION so regEx-reserved chars MUST be escaped
                outString = outString.replaceAll(_filterRegexChars(currStrToReplace),	// beware!!
                								 currVarValue);
            }
        }
        return outString;
    }
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////    
    /**
	 * Escapes regEx reserved chars
	 * @param strToBeFiltered
	 * @return
	 */
    private static String _filterRegexChars(String strToBeFiltered) {
    	char[] charsToFilter = { '.', '?', '*', '+', '^', '$','(',')','{','}' };	// Filtered chars
    	
        char content[] = new char[strToBeFiltered.length()];
        strToBeFiltered.getChars(0, strToBeFiltered.length(), content, 0);
        StringBuffer result = new StringBuffer(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            boolean replaced = false;
         
			for (int j = 0; j < charsToFilter.length; j++) {
                if (content[i] == charsToFilter[j]) {
                    result.append("\\"+content[i]);
                    replaced = true;
                }
            }
            if (!replaced)
                result.append(content[i]);
        }
        return (result.toString());
    }
	
}
