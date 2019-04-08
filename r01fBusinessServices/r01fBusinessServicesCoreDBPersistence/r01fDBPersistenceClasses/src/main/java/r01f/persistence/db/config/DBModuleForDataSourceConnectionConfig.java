package r01f.persistence.db.config;

import java.util.Properties;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
public class DBModuleForDataSourceConnectionConfig
	 extends DBModuleConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Properties _properties;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBModuleForDataSourceConnectionConfig(final XMLPropertiesForAppComponent xmlProps) {
		super(xmlProps,
			  PersistenceUnitType.DATASOURCE);

		String xPath = "persistence/unit[@type='" + _unitType + "']/connection[@vendor='" + _dbSpec.getVendor() + "']";
		Properties props = xmlProps.propertyAt(xPath)
							   	   .asProperties();
		if (props == null) {
			log.error("Could NOT find persistence unit properties at {} in {}.{} properties!",
					  xPath,
					  xmlProps.getAppCode(),xmlProps.getAppComponent());
			props = new Properties();
		}
		// IMPORTANT
		//		At least you must provide a name of datasource. If it is not provided it will generate one by default
		if (!props.containsKey(PersistenceUnitProperties.NON_JTA_DATASOURCE)) {
			log.error("Could NO load the DB connection properties FOR DATASOURCE at {} at properties file {}: does it contains a connection section?",
					  xPath,super.getAppCode());
			props.put(PersistenceUnitProperties.NON_JTA_DATASOURCE,Strings.customized("{}.{}DataSource",
																					  xmlProps.getAppCode(),xmlProps.getAppCode()));
		}
		_properties = props;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  AS PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Properties asProperties() {
		Properties commonProps = super.asProperties();

		if (_properties != null) _logProps("DATASOURCE",_properties);

		Properties outProps = new Properties();
		if (commonProps != null) outProps.putAll(commonProps);
		if (_properties != null) outProps.putAll(_properties);
		return outProps;
	}
}
