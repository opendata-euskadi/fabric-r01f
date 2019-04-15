package r01f.persistence.index.document;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import lombok.RequiredArgsConstructor;
import r01f.model.IndexableModelObject;
import r01f.persistence.index.document.IndexDocumentFieldConfigFactories.IndexDocumentFieldConfigFactory;
import r01f.util.types.collections.CollectionUtils;

/**
 * Creates {@link IndexDocumentFieldConfig} using the list of {@link IndexDocumentFieldConfigFactory}
 * and caches the created object by it's ID
 */
@RequiredArgsConstructor
class IndexDocumentFieldConfigById<M extends IndexableModelObject> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A list of factories that creates config for index document's fields
	 * This list is traversed when there's the need to create a field config
	 * for a field that was not already created (it's not in the cache)
	 */
	private final List<IndexDocumentFieldConfigFactory> _fieldFactories;	
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Cached fields config
	 */
	private Map<IndexDocumentFieldID,IndexDocumentFieldConfig<?>> _fieldConfigById;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the index document's field config for a field
	 * @param fieldId
	 * @return
	 */
	public IndexDocumentFieldConfig<?> of(final IndexDocumentFieldID fieldId) {
		return this.getConfigFor(fieldId);
	}
	/**
	 * Returns the index document's field config for the field which id is provided
	 * @param id
	 * @return
	 */
	public IndexDocumentFieldConfig<?> getConfigFor(final IndexDocumentFieldID fieldId) {
		// check the cache
		IndexDocumentFieldConfig<?> outCfg = this.getConfigOrNullFor(fieldId);
		if (outCfg == null) _throwWhenFieldNotFound(fieldId);
		return outCfg;
	}
	/**
	 * Returns the index document's field config for the field which id is provided
	 * @param id
	 * @return
	 */
	public IndexDocumentFieldConfig<?> getConfigOrNullFor(final IndexDocumentFieldID fieldId) {
		// check the cache
		IndexDocumentFieldConfig<?> outCfg = _fieldConfigById != null ? _fieldConfigById.get(fieldId)
							   						  		 : null;
		// The field is NOT in the cache... try to find a suitable factory
		if (outCfg == null) {
			if (CollectionUtils.isNullOrEmpty(_fieldFactories)) throw new IllegalStateException("There's NO index document's field factory registered so the config for the required field '" + fieldId + "' could NOT be created!");
			for (IndexDocumentFieldConfigFactory factory : _fieldFactories) {
				if (factory.isUsableFor(fieldId)) {
					outCfg = factory.createFieldConfigFor(fieldId);
					this.add(outCfg);	// cache...
					break;
				} 
			}
		} 
		return outCfg;
	}
	private void _throwWhenFieldNotFound(final IndexDocumentFieldID fieldId) {
		// a bit of debugging...
		StringBuilder dbgErr = new StringBuilder(_fieldFactories.size() * 50);
		dbgErr.append("NO suitable index document's field factory were found for field '")
			  .append(fieldId)
			  .append("' ");
		dbgErr.append("; tried: ");	
		for (Iterator<IndexDocumentFieldConfigFactory> it = _fieldFactories.iterator(); it.hasNext(); ) {
			IndexDocumentFieldConfigFactory factory = it.next();
			dbgErr.append(factory.getId());
			if (it.hasNext()) dbgErr.append(", ");
		}
		throw new IllegalStateException(dbgErr.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE ADD 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds some index document's fields config to the set
	 * @param fields 
	 * @return
	 */
	public IndexDocumentFieldConfigById<M> add(final IndexDocumentFieldConfig<?>... fields) {
		Preconditions.checkArgument(CollectionUtils.hasData(fields),
									"Provided index document's fields config cannot be empty");
		if (_fieldConfigById == null) _fieldConfigById = Maps.newHashMap();
		for (IndexDocumentFieldConfig<?> field : fields) {
			_fieldConfigById.put(field.getId(),
						field);
		}
		return this;
	}
	/**
	 * Adds some index document's fields config to the set
	 * @param fields
	 * @return
	 */
	public IndexDocumentFieldConfigById<M> add(final Collection<IndexDocumentFieldConfig<?>> fields) {
		Preconditions.checkArgument(CollectionUtils.hasData(fields),
									"Provided index document's fields config cannot be empty");
		if (_fieldConfigById == null) _fieldConfigById = Maps.newHashMap();
		for (IndexDocumentFieldConfig<?> field : fields) {
			_fieldConfigById.put(field.getId(),
						field);
		}
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RETRIEVE
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasData() {
		return CollectionUtils.hasData(_fieldConfigById);
	}
	/**
	 * @return the number of index document's Field config at the Set
	 */
	public int size() {
		return _fieldConfigById != null ? _fieldConfigById.size()
							   : 0;
	}
	/**
	 * @return a map of index document's fields config indexed by field name
	 */
	public Map<IndexDocumentFieldID,IndexDocumentFieldConfig<?>> getConfigForFieldsMap() {
		return _fieldConfigById;
	}
	/**
	 * @return all the index document's fields config
	 */
	public Collection<IndexDocumentFieldConfig<?>> getConfigForFields() {
		return _fieldConfigById != null ? _fieldConfigById.values()
							   : null;
	}

}
