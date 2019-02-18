package r01f.reflection.types;

import java.util.Map;

import r01f.reflection.ReflectionUtils;


public class StringReflectionInterpolation {
///////////////////////////////////////////////////////////////////////////////
// 	VARIABLE INTERPOLATION USING Reflection
///////////////////////////////////////////////////////////////////////////////    
    /**
     * Interpolates vas inside a string with their values; the values are taken from a value object
     * The vars are delimited by a marker char like $varName$
     * BEWARE: If the var value contains the delimiter char (ie: $) double it (ie: '10$$ dolars')
     * @param inStr the string to interpolate
     * @param varDelim the var delimiter string
     * @param obj the value object that contains the var values
     * @param varPaths a Map containing the var values indexed by var name
     * @param interpolateVarValues interpolate the var values before replacing the var
     * @return the interpolated string
     */
    public static String replaceVariableValuesUsingReflection(final String inStr,final String varDelim,
    														  final Object obj,final Map<String,String> varPaths) {
        StringBuilder outBuff = new StringBuilder(inStr.length());

        // Search for the vars
        int p1 = 0;
        int v1 = -1;
        int v2 = -1;
        String varName = null;
        String varPath = null;
        String varValue = null;
        boolean substitution = false;

        do {
            substitution = false;
            v1 = inStr.indexOf(varDelim, p1);
            if (v1 >= 0) {
                v2 = inStr.indexOf(varDelim, v1 + 1);
            }
            if (v2 > v1) {
                outBuff.append(inStr.substring(p1, v1)); // add text before the var
                varName = inStr.substring(v1 + 1, v2);
                if (varName.length() > 0) {
                    varPath = varPaths.get(varName);
                    if (varPath == null) {
                        outBuff.append("null");
                    } else {
                        varValue = (String)ReflectionUtils.fieldValueUsingPath(obj,varPath,false);
                        outBuff.append(varValue);
                    }
                } else if (varName.length() == 0) {
                    outBuff.append(varDelim);
                    outBuff.append(varDelim);
                }
                p1 = v2 + 1;
                v1 = -1;
                v2 = -1;
                substitution = true;
            }
        } while (substitution);
        // add the rest of the string
        if (p1 < inStr.length())
            outBuff.append(inStr.substring(p1));
        return outBuff.toString();
    }  	
}
