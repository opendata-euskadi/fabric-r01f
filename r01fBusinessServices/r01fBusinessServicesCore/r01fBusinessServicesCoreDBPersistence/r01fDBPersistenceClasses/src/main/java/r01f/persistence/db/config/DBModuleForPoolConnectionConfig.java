package r01f.persistence.db.config;

import java.util.Properties;

import lombok.Getter;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public class DBModuleForPoolConnectionConfig
	 extends DBModuleConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final DBConnectionPoolData _poolData;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBModuleForPoolConnectionConfig(final XMLPropertiesForAppComponent xmlProps) {
		super(xmlProps,
			  PersistenceUnitType.DRIVER_MANAGER);
		_poolData = new DBConnectionPoolData(xmlProps.getAppCode(),
											 _unitType,_dbSpec,
											 xmlProps);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  AS PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Properties asProperties() {
		Properties commonProps = super.asProperties();

		Properties conxProps = _poolData != null ? _poolData.asProperties() : null;
		if (conxProps != null) 	_logProps("DB CONNECTION",conxProps);

		Properties outProps = new Properties();
		if (commonProps != null) outProps.putAll(commonProps);
		if (conxProps != null) outProps.putAll(conxProps);
		return outProps;
	}
}
