package r01f.persistence.index.document;

import java.util.Map;

import com.google.common.collect.Maps;

import r01f.model.IndexableModelObject;
import r01f.model.metadata.MetaDataDescribable;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.util.types.collections.CollectionUtils;

/**
 * Holds a {@link Map} of {@link IndexDocumentFieldConfigSet} indexed by {@link IndexableModelObject} type
 * Mainly it's a means of access indexable objects fields by it's ID
 * 
 * This type is used when the only possible input is the fieldId which is the case of analyzers (ie lucene analyzer)
 * (the type info is NOT available: only the fieldId)
 * 
 * ... the only option is find the {@link IndexDocumentFieldConfig} by fieldId in every type and cache the result  
 * 
 * BEWARE!! do NOT use this type unless there's a good reason
 */
public class IndexDocumentFieldConfigSetByIndexableObjectType {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Stores the {@link IndexDocumentFieldConfigSet} for every {@link IndexableModelObject}
	 */
	private final Map<Class<? extends IndexableModelObject>,IndexDocumentFieldConfigSet<? extends IndexableModelObject>> _fieldConfigSetByIndexableObjType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked","rawtypes" })
	public IndexDocumentFieldConfigSetByIndexableObjectType() {
		// get all the TypeMetaData annotated types
		Map<Class<? extends MetaDataDescribable>,
			TypeMetaData<? extends MetaDataDescribable>> inspectedTypes =  TypeMetaDataInspector.singleton()
																								.getInspectedTypes();
		// Get all @TypeMetaData-annotated types as a Map<Class<? extends IndexableModelObject>,IndexDocumentFieldConfigSet<? extends IndexableModelObject>>
		_fieldConfigSetByIndexableObjType = Maps.newHashMapWithExpectedSize(inspectedTypes.size());
		for (Map.Entry<Class<? extends MetaDataDescribable>,TypeMetaData<? extends MetaDataDescribable>> typeEntry : inspectedTypes.entrySet()) {
			Class<? extends IndexableModelObject> type = (Class<? extends IndexableModelObject>)typeEntry.getKey();
			TypeMetaData<? extends IndexableModelObject> typeMetaData = (TypeMetaData<? extends IndexableModelObject>)typeEntry.getValue();
			
			IndexDocumentFieldConfigSet<? extends IndexableModelObject> indexDocFieldConfigSet = new IndexDocumentFieldConfigSet(type,
											   	  																				 typeMetaData);
			
			_fieldConfigSetByIndexableObjType.put(type,
												  indexDocFieldConfigSet);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Cached fields config
	 */
	private Map<IndexDocumentFieldID,IndexDocumentFieldConfig<?>> _fieldConfigById;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONFIG GET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the index document's field config for the field which id is provided
	 * @param type
	 * @param fieldId
	 * @return
	 */
	public IndexDocumentFieldConfig<?> getConfigOrThrowFor(final Class<? extends IndexableModelObject> type,
														  final IndexDocumentFieldID fieldId) {
		IndexDocumentFieldConfig<?> outCfg = this.getConfigOrNullFor(type,
																	 fieldId);
		if (outCfg == null) throw new IllegalStateException("Could NOT field field with id='" + fieldId + "' config in the indexable object's field config set for " + type);
		return outCfg;
	}
	/**
	 * Returns the index document's field config for the field which id is provided
	 * @param type
	 * @param fieldId
	 * @return
	 */
	public IndexDocumentFieldConfig<?> getConfigOrNullFor(final Class<? extends IndexableModelObject> type,
														  final IndexDocumentFieldID fieldId) {
		// [1] - Try the cached configs by field id
		IndexDocumentFieldConfig<?> outCfg = this.getConfigOrNullFor(fieldId);
		if (outCfg != null) return outCfg;
		
		// [2] - Use the config set for the type
		outCfg = _fieldConfigSetByIndexableObjType.get(type)
												  .getConfigOrNullFor(fieldId);
		// [3] - Cache the value
		if (outCfg != null) {
			if (_fieldConfigById == null) _fieldConfigById = Maps.newHashMap();
			_fieldConfigById.put(fieldId,outCfg);
		}
		// [4] - Return
		return outCfg;
	}
	/**
	 * Returns the index document's field config for the field which id is provided
	 * @param id
	 * @return
	 */
	public IndexDocumentFieldConfig<?> getConfigOrThrowFor(final IndexDocumentFieldID fieldId) {
		IndexDocumentFieldConfig<?> outCfg = this.getConfigOrNullFor(fieldId);
		if (outCfg == null) throw new IllegalStateException("Could NOT field field with id='" + fieldId + "' config in any of the indexable object's field config set: " + _fieldConfigSetByIndexableObjType.keySet());
		return outCfg;
	}
	/**
	 * Returns the index document's field config for the field which id is provided
	 * @param id
	 * @return
	 */
	public IndexDocumentFieldConfig<?> getConfigOrNullFor(final IndexDocumentFieldID fieldId) {
		if (CollectionUtils.isNullOrEmpty(_fieldConfigSetByIndexableObjType)) return null;
		
		IndexDocumentFieldConfig<?> outCfg = null;
		
		// [1] - try the cache
		outCfg = _fieldConfigById != null ? _fieldConfigById.get(fieldId)
										  : null;
		if (outCfg != null) return outCfg;
		
		// [2] - the field was not found at the cache; traverse every objet's field to find the required field config
		for (IndexDocumentFieldConfigSet<? extends IndexableModelObject> indexDocFieldConfigSet : _fieldConfigSetByIndexableObjType.values()) {
			outCfg = indexDocFieldConfigSet.getConfigOrNullFor(fieldId);
			if (outCfg != null) break;
		}
		// [3] - Cache the value
		if (outCfg != null) {
			if (_fieldConfigById == null) _fieldConfigById = Maps.newHashMap();
			_fieldConfigById.put(fieldId,outCfg);
		}
		// [4] - Return
		return outCfg;
	}
}
