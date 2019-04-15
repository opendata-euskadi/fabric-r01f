package r01f.persistence.db.config;

import java.util.Properties;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.w3c.dom.Node;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.debug.Debuggable;
import r01f.guids.CommonOIDs.AppCode;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class DBConnectionPoolSize
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _initial;
	private final String _min;
	private final String _max;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBConnectionPoolSize(final AppCode appCode,
							    final PersistenceUnitType unitType,final DBSpec dbSpec,
							    final XMLPropertiesForAppComponent xmlProps) {
		String initial = null;
		String min = null;
		String max = null;
		String xPath = "persistence/unit[@type='" + unitType + "']/connectionPool";
		Node node = xmlProps.propertyAt(xPath).node();
		if (node != null && node.getAttributes() != null) {
			initial = node.getAttributes().getNamedItem("initial") != null ? node.getAttributes().getNamedItem("initial").getNodeValue() : null;
			min = node.getAttributes().getNamedItem("min") != null ? node.getAttributes().getNamedItem("min").getNodeValue() : null;
			max = node.getAttributes().getNamedItem("max") != null ? node.getAttributes().getNamedItem("max").getNodeValue() : null;

			if (!Numbers.isInteger(initial)) log.error("The connection pool initial size at {} is NOT valid: {}",
						  								xPath,initial);
			if (!Numbers.isInteger(min)) log.error("The connection pool min size at {} is NOT valid: {}",
						  						   xPath,min);
			if (!Numbers.isInteger(max)) log.error("The connection pool max size at {} is NOT valid: {}",
						  							xPath,initial);
		} else {
			log.error("The connection pool data is NOT available at {} in properties file for {}.{}",
					  xPath,
					  xmlProps.getAppCode(),xmlProps.getAppComponent());
		}
		_initial = initial;
		_min = min;
		_max = max;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("initial/min/max)={}/{}/{}",
								  _initial,_min,_max);
	}
	public Properties asProperties() {
		Properties props = new Properties();
		if (_initial != null) props.put("eclipselink.jdbc.connection_pool.default." + PersistenceUnitProperties.CONNECTION_POOL_INITIAL,_initial);
		if (_min != null) 	  props.put("eclipselink.jdbc.connection_pool.default." + PersistenceUnitProperties.CONNECTION_POOL_MIN,_min);
		if (_max != null) 	  props.put("eclipselink.jdbc.connection_pool.default." + PersistenceUnitProperties.CONNECTION_POOL_MAX,_max);
		return props;
	}
}

