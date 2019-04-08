package r01f.model.metadata;

import java.util.Collection;
import java.util.Date;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.metadata.annotations.Storage;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.reflection.ReflectionUtils;
import r01f.types.IsPath;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.types.summary.Summary;
import r01f.types.url.Url;
import r01f.util.types.collections.Lists;

/**
 * Search engine indexing config used when defined a {@link FieldMetaData} config
 * This config is later translated into a IndexDocumentFieldConfig when defining the search-engine stored document
 */
@MarshallType(as="metaDataSearchEngineIndexingConfig")
@GwtIncompatible
@Accessors(prefix="_")
public class FieldMetaDataSearchEngineIndexingConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * If the field's value is true the value is stored "as-is"
	 * This field's value could be used to be shown in the search results
	 */
	@MarshallField(as="store",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _stored;
	/**
	 * Boosting value
	 */
	@MarshallField(as="boost",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private float _boost = 1.0f;
	/**
	 * Is the field value indexed?
	 */
	@MarshallField(as="indexed",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _indexed = true;
	/**
	 * Is the field indexed value tokenized?
	 */
	@MarshallField(as="tokenized",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _tokenized;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static FieldMetaDataSearchEngineIndexingConfig forConfig(final Storage storage) {
		if (storage == null) return new FieldMetaDataSearchEngineIndexingConfig();
		
		FieldMetaDataSearchEngineIndexingConfig outCfg = new FieldMetaDataSearchEngineIndexingConfig();
		outCfg.setIndexed(storage.indexed());
		outCfg.setStored(storage.stored());
		outCfg.setTokenized(storage.tokenized());
		outCfg.setBoost(storage.boosting());
		return outCfg;
	}
	public static FieldMetaDataSearchEngineIndexingConfig forFieldTypeWithConfig(final Class<?> fieldType,
																				 final Storage storage) {
		FieldMetaDataSearchEngineIndexingConfig outCfg = FieldMetaDataSearchEngineIndexingConfig.forConfig(storage);
		if (FieldMetaDataSearchEngineIndexingConfig.isNEVERTokenized(fieldType)) {
			outCfg.setTokenized(false);		// NEVER tokenized
		} else if (FieldMetaDataSearchEngineIndexingConfig.isALWAYSTokenized(fieldType)) {
			outCfg.setTokenized(true);		// ALWAYS tokenized
		} else if (storage != null) {
			outCfg.setTokenized(storage.tokenized());	
		}
		return outCfg;
	}
	public static FieldMetaDataSearchEngineIndexingConfig notTokenizable() {
		FieldMetaDataSearchEngineIndexingConfig outCfg = new FieldMetaDataSearchEngineIndexingConfig();
		outCfg.setTokenized(false);		// cannot be tokenized
		return outCfg;
	}
	public static FieldMetaDataSearchEngineIndexingConfig tokenizable() {
		FieldMetaDataSearchEngineIndexingConfig outCfg = new FieldMetaDataSearchEngineIndexingConfig();
		outCfg.setTokenized(true);		// can be tokenized
		return outCfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static Collection<Class<?>> NEVER_TOKENIZABLE_FIELD_TYPES = Lists.<Class<?>>newArrayList(
																			Class.class,	// java type
																	    	Integer.class,Long.class,Double.class,Float.class,
																	    	Date.class,
																	    	Year.class,MonthOfYear.class,DayOfMonth.class,
																	    	Boolean.class,
																	    	Language.class,
																	    	Url.class);
	public static boolean isNEVERTokenized(final Class<?> fieldType) {
		return fieldType.isEnum()
		    || NEVER_TOKENIZABLE_FIELD_TYPES.contains(fieldType)
		    || ReflectionUtils.isImplementing(fieldType,OID.class)
		    || ReflectionUtils.isImplementing(fieldType,IsPath.class);
	}
	public static boolean isALWAYSTokenized(final Class<?> fieldType) {
		return ReflectionUtils.isImplementing(fieldType,Summary.class)
			|| ReflectionUtils.isImplementing(fieldType,LanguageTexts.class);
	}
}
