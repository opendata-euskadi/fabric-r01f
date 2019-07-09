package r01f.xmlproperties;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.Environment;
import r01f.types.Path;
import r01f.util.types.Strings;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
@Slf4j
abstract class XMLPropertiesEnv {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Guess the {@link Environment} value from a system wide property
     * @return
     */
    public static Environment guessEnvironmentFromSystemEnvProp() {
        String envProp = null;  
        // 1... Try to Read from virtual machine properties.      
        envProp = _readEnvPropFrom(System.getProperties());       
        // 2. ... Try Read from machine environment  
        //(  	...another option could be to pass , a env name to : System.getenv("env_type")..
        if (Strings.isNullOrEmpty(envProp)) {
        	envProp = _readEnvPropFrom(System.getenv());
        }
        // 3. ... Try Read from files at resoure stream into classloader.
        if (Strings.isNullOrEmpty(envProp)) {
        	envProp = _readEnvPropFrom();
        	log.warn("\n >> Read OK from file (r01Env.properties | R01Env.properties |  R01Env.properties ......... ) on classloader  !!!");
        }    
        if (  Strings.isNOTNullOrEmpty(envProp) ) {
            log.warn( Strings.customized(" \n\n R01Env propertie is SET to {}", envProp));
            return Environment.forId(envProp);
        } else {
            return Environment.DEFAULT;
        }
    }
    /**
     * Guess the {@link Environment} value from an attribute at the component's xml's root node
     * @param compProps component properties
     * @return
     */
    public static Environment guessEnvironmentFromXMLProperties(final ComponentProperties compProps) {
        String envProp = null;
                                             envProp = compProps.getString(Path.from("/*/@env"));
        if (Strings.isNullOrEmpty(envProp)) envProp = compProps.getString(Path.from("/*/@r01Env"));

        return Strings.isNOTNullOrEmpty(envProp) ? Environment.forId(envProp)
                                                      : Environment.DEFAULT;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    private static String _readEnvPropFrom(final Properties properties) {
    	  String envProp = null;
          envProp = System.getProperty("R01ENV");
		  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("r01Env");
		  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("R01Env");
		  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("R01_ENV");
		  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("r01_env");
		  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("ENV");
		  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("env");
		  return envProp;
    }
    private static String _readEnvPropFrom(final Map<String,String> propertiesAsMap) {
	  	  String envProp = null;
	      envProp = System.getProperty("R01ENV");
		  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("r01Env");
		  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("R01Env");
		  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("R01_ENV");
		  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("r01_env");
		  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("ENV");
		  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("env");
		  return envProp;
    }
    private static String _readEnvPropFrom() {
	  	  String envProp = null;
	      envProp = _readEnvPropFrom("R01ENV");
		  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("r01Env.properties");
		  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("R01Env.properties");
		  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("R01_ENV.properties");
		  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("r01_env.properties");
		  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("ENV.properties");
		  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("env.properties");
		  return envProp;
    }
    private static String _readEnvPropFrom(final String fileNameAsResourceStream) {
    	try {
        	InputStream is = XMLPropertiesEnv.class.getClassLoader().getResourceAsStream(fileNameAsResourceStream);
        	if ( is != null ) {
        		Properties propFromFile = new Properties();
        		propFromFile.load(is);
        		propFromFile.list(System.out);
        		return  _readEnvPropFrom(propFromFile);
        	}
    	}catch (final Throwable th) {
        	//log
    		log.error(" Unable to read : " + fileNameAsResourceStream  + th.getLocalizedMessage());
        }
		return null;	   
     }
    
    
}
