package r01f.persistence.index.document;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;


/**
 * Encapsulates the config a document to be indexed 
 * This config is usually loaded from an xml like:
 * <pre class='brush:java'>
 * 		Marshaller marshaller = SimpleMarshaller.createForPackages("r01fb.index.document")
 *											    .getForSingleUse();
 *		IndexDocumentConfig documentCfg = marshaller.beanFromXml(xml);
 * </pre>
 * ... but it's also possible to create it programmatically:
 * <pre class='brush:java'>
 *	    private static IndexDocumentConfig _buildDocumentCfg() {
 *	    	IndexDocumentConfig outCfg = IndexDocumentConfig.create()
 *	    												  	  .add(_buildDocValueFieldCfg())
 *	    												 	  .add(_buildStandardFieldCfg());
 *	    	return outCfg;
 *	    }
 *	    private static LuceneFieldConfig<IndexDocumentValueFieldType> _buildDocValueFieldCfg() {
 *	    	IndexDocumentConfig<IndexDocumentValueFieldType> outFieldCfg = IndexDocumentConfig.createDocValue("docValueField")
 *	    																			  	  	  .ofKind(IndexDocumentStandardFieldType.Double);
 *	    	
 *	    	return outFieldCfg;
 *	    }
 *	    private static LuceneFieldConfig<IndexDocumentStandardFieldType> _buildStandardFieldCfg() {
 *	    	IndexDocumentConfig<IndexDocumentStandardFieldType> outFieldCfg = LuceneFieldConfig.createStandard("standardFieldType")
 *	    																			  		  .ofKind(IndexDocumentStandardFieldType.String)
 *	    																			  		  .overridenWith(IndexDocumentFieldTypeOptionsOverride.create()
 *	    																					  									   		   		  .indexed());
 *	    	return outFieldCfg;
 *	    }
 * </pre>
 * @see IndexDocumentFieldConfigSet
 */
@MarshallType(as="indexDocumentConfig")
@Accessors(prefix="_")
@NoArgsConstructor
public class IndexDocumentConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The document's fields
	 */
	@MarshallField(as="fields") 
	@Getter @Setter private Map<IndexDocumentFieldID,IndexDocumentFieldConfig<?>> _fields;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static IndexDocumentConfig create() {
		return new IndexDocumentConfig();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <FIELD_TYPE extends IndexDocumentFieldType> IndexDocumentFieldConfig<FIELD_TYPE> get(final IndexDocumentFieldID luceneFieldName) {
		return _fields != null ? (IndexDocumentFieldConfig<FIELD_TYPE>)_fields.get(luceneFieldName) 
							   : null;
	}
	public <FIELD_TYPE extends IndexDocumentFieldType> IndexDocumentConfig add(final IndexDocumentFieldConfig<FIELD_TYPE> cfg) {
		if (_fields == null) _fields = Maps.newHashMap();
		_fields.put(cfg.getId(),cfg);
		return this;
	}
}
