package r01f.persistence.db.config;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetDatabase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.debug.Debuggable;
import r01f.exceptions.Throwables;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

/**
 * Encapsulates the database specification needed when defining the
 * schema generation at persistence.xml
 * For MySql:
 * <pre class='brush:xml'>
 *			<property name="javax.persistence.database-product-name" value="mysql"/>
 *			<property name="javax.persistence.database-major-version" value="5"/>
 *			<property name="javax.persistence.database-minor-version" value="6"/>
 * </pre>
 * For Oracle:
 * <pre class='brush:xml'>
 *			<property name="javax.persistence.database-product-name" value="Oracle"/>
 *			<property name="javax.persistence.database-major-version" value="11"/>
 *			<property name="javax.persistence.database-minor-version" value="2"/>
 * </pre>
 */
@Slf4j
@Accessors(prefix="_")
@RequiredArgsConstructor
public class DBSpec
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public final static String DEFAULT = "MySql 5.7";
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="vendor",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final DBVendor _vendor;
	
	@MarshallField(as="majorVersion",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _majorVersion;
	
	@MarshallField(as="minorVersion",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _minorVersion;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern SPEC_PATTERN = Pattern.compile("(" + TargetDatabase.MySQL + "|" + TargetDatabase.PostgreSQL + "|" + TargetDatabase.Oracle + ")\\s([0-9]+)\\.([0-9]+)");
	/**
	 * Loads the {@link DBSpec} form a {@link String} like 
	 * (mysql|Oracle) {majorVersion}.{minorVersion}
	 * @param code
	 * @return
	 */
	public static DBSpec valueOf(final String code) {
		DBSpec outSpec = null;
		
		Matcher m = SPEC_PATTERN.matcher(code);
		if (m.find()) {
			DBVendor vendor = DBVendor.fromCode(m.group(1));
			int majorVersion = Integer.parseInt(m.group(2));
			int minorVersion = Integer.parseInt(m.group(3));
			outSpec = new DBSpec(vendor,majorVersion,minorVersion);
		} else {
			throw new IllegalArgumentException(Throwables.message("The database spect DOES NOT have a valid format: {}. It MUST match {}",
																  code,SPEC_PATTERN.toString()));
		}
		return outSpec;
	}
	/**
	 * Loads the {@link DBSpec} from the {@link EntityManager} properties
	 * @param em
	 * @return
	 */
	public static DBSpec usedAt(final EntityManager em) {
		// All these properties should have been set at DBGuiceModuleBase
		// using the db properties xml file
		Map<String,Object> emProps = em.getProperties();
		Object dbVendorProp = emProps.get(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME);
		Object majorVersionProp = emProps.get(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION);
		Object minorVersionProp = emProps.get(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION);
		
		// some checks
		if (dbVendorProp == null) throw new IllegalArgumentException(Strings.customized("The {} entity manager property was NOT set using the db xml properties file",
																						PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME));
		if (majorVersionProp == null) throw new IllegalArgumentException(Strings.customized("The {} entity manager property was NOT set using the db xml properties file",
																						     PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION));
		if (minorVersionProp == null) throw new IllegalArgumentException(Strings.customized("The {} entity manager property was NOT set using the db xml properties file",
																						    PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION));		
		if (!(dbVendorProp instanceof String) && !DBVendor.canBeFromCode((String)dbVendorProp)) throw new IllegalArgumentException(Strings.customized("The DBVendor name={} is NOT valid (the valid ones are {})",
																																   					  dbVendorProp,DBVendor.values()));
		if (!Numbers.isInteger(majorVersionProp.toString())) throw new IllegalArgumentException(Strings.customized("The db major version={} is NOT a valid int number",
																													majorVersionProp));
		if (!Numbers.isInteger(minorVersionProp.toString())) throw new IllegalArgumentException(Strings.customized("The db mino version={} is NOT a valid int number",
																													majorVersionProp));
		
		
		DBVendor dbVendor = DBVendor.fromCode(((String)dbVendorProp));		
		int majorVersion = Numbers.toInt(majorVersionProp.toString());				          
		int minorVersion = Numbers.toInt(minorVersionProp.toString());		
		DBSpec outSpec = new DBSpec(dbVendor,
									majorVersion,minorVersion);
		log.debug("DBSpec from entity manager properties:  {}",
				  outSpec.debugInfo());
		return outSpec;	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} {}.{}",_vendor,_majorVersion,_minorVersion);
	}
}
