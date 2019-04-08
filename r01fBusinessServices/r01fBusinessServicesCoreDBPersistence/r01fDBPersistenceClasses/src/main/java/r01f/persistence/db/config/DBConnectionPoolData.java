package r01f.persistence.db.config;

import java.util.Map;
import java.util.Properties;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.debug.Debuggable;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.JavaTypeName;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class DBConnectionPoolData
  implements Debuggable {
	@Getter private final DBConnectionPoolSize _poolSize;

	@Getter private final UserCode _user;
	@Getter private final Password _password;

	@Getter private final JavaTypeName _driverClassName;
	@Getter private final String _connectionUrl;

	@Getter private final Properties _otherProps;

	public DBConnectionPoolData(final AppCode appCode,
								final PersistenceUnitType unitType,final DBSpec dbSpec,
							   	final XMLPropertiesForAppComponent xmlProps) {
		String user = null;
		String pwd = null;
		String driverJavaType = null;
		String connectionUrl = null;
		Properties otherProps = null;

		String xPath = "persistence/unit[@type='" + unitType + "']/connection[@vendor='" + dbSpec.getVendor() + "']";
		Properties props = xmlProps.propertyAt(xPath)
							   			.asProperties();
		if (CollectionUtils.isNullOrEmpty(props)) {
			log.error("Could NO load the DB connection properties at {} at properties file {}: does it contains a connection section?",
					  xPath,appCode);
		} else {
			Map<String,String> propsMap = CollectionUtils.toMap(props);

			// Driver User / password
			user = propsMap.remove(PersistenceUnitProperties.JDBC_USER);
			pwd = propsMap.remove(PersistenceUnitProperties.JDBC_PASSWORD);
			driverJavaType = propsMap.remove(PersistenceUnitProperties.JDBC_DRIVER);
			connectionUrl = propsMap.remove(PersistenceUnitProperties.JDBC_URL);
			// the rest of the properties
			if (!propsMap.isEmpty()) otherProps = CollectionUtils.toProperties(propsMap);
		}
		_poolSize = new DBConnectionPoolSize(appCode,
									     	 unitType,dbSpec,
									     	 xmlProps);
		_user = Strings.isNOTNullOrEmpty(user) ? UserCode.forId(user)
											   : UserCode.forId(appCode.asString());
		_password = Strings.isNOTNullOrEmpty(pwd) ? Password.forId(pwd)
												  : Password.forId(appCode.asString());
		_driverClassName = Strings.isNOTNullOrEmpty(driverJavaType) ? new JavaTypeName(driverJavaType)
																	: new JavaTypeName("com.mysql.jdbc.Driver");
		_connectionUrl = Strings.isNOTNullOrEmpty(connectionUrl) ? connectionUrl
																 : "jdbc:mysql://localhost:3306/pci";
		_otherProps = otherProps;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("DB connection pool: size={}, usr/pwd={}/{}, driver={}",
								  _poolSize.debugInfo(),
								  _user,_password,
								  _driverClassName);
	}
	public Properties asProperties() {
		Properties props = new Properties();
		props.put(PersistenceUnitProperties.JDBC_USER,_user.asString());
		props.put(PersistenceUnitProperties.JDBC_PASSWORD,_password.asString());
		props.put(PersistenceUnitProperties.JDBC_DRIVER,_driverClassName.asString());		// BEWARE to copy mySql jconector at $CATALINA_HOME/lib
		props.put(PersistenceUnitProperties.JDBC_URL,_connectionUrl);
		if (_poolSize != null) props.putAll(_poolSize.asProperties());
		if (_otherProps != null) props.putAll(_otherProps);
		return props;
	}
}

