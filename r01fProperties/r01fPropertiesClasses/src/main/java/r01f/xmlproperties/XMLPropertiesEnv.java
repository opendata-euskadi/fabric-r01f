package r01f.xmlproperties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.Environment;
import r01f.types.Path;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
abstract class XMLPropertiesEnv {
	/**
	 * Guess the {@link Environment} value from a system wide property
	 * @return
	 */
	public static Environment guessEnvironmentFromSystemEnvProp() {
		String envProp = null;
		if (envProp == null) envProp = System.getProperty("R01ENV");
		if (envProp == null) envProp = System.getProperty("R01Env");
		if (envProp == null) envProp = System.getProperty("ENV");
		if (envProp == null) envProp = System.getProperty("env");
		
		return envProp != null ? Environment.forId(envProp)
							   : null;
	}
	/**
	 * Guess the {@link Environment} value from an attribute at the component's xml's root node	
	 * @param compProps component properties
	 * @return
	 */
	public static Environment guessEnvironmentFromXMLProperties(final ComponentProperties compProps) {
		String envProp = null;
		if (envProp == null) envProp = compProps.getString(Path.from("/*/@env"));
		if (envProp == null) envProp = compProps.getString(Path.from("/*/@r01Env"));
		
		return envProp != null ? Environment.forId(envProp)
							   : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
}
