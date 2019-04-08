package r01f.persistence.index.document;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.model.IndexableModelObject;
import r01f.model.metadata.FieldID;
import r01f.patterns.Memoized;

@Slf4j
@RequiredArgsConstructor
public abstract class IndexDocumentBase<M extends IndexableModelObject> 
		   implements IndexDocument<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Caches field values
	 */
	private Memoized<Map<FieldID,IndexDocumentFieldValue<?>>> _fields = new Memoized<Map<FieldID,IndexDocumentFieldValue<?>>>() {
																				@Override
																				protected Map<FieldID,IndexDocumentFieldValue<?>> supply() {
																					return IndexDocumentBase.this.getFields();
																				}
																		};
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <T> IndexDocumentFieldValue<T> getField(final FieldID metaDataId) {
		return (IndexDocumentFieldValue<T>)_fields.get()
					  							  .get(metaDataId);
	}
	@Override
	public <T> T getFieldValue(final FieldID metaDataId) {
		IndexDocumentFieldValue<T> indexedField = this.getField(metaDataId);
		return indexedField != null ? indexedField.getValue() : null;
	}
	@Override @SuppressWarnings("unchecked")
	public <T> IndexDocumentFieldValue<T> getFieldOrThrow(final FieldID metaDataId) {
		IndexDocumentFieldValue<T> outField = (IndexDocumentFieldValue<T>)_fields.get()
					  							  								 .get(metaDataId);
		if (outField == null) {
			log.error("The indexed document does NOT contains a field with name {}",metaDataId);
			log.error("The available field ids are:");
			Map<FieldID,IndexDocumentFieldValue<?>> docFields = _fields.get();
			for (Map.Entry<FieldID,IndexDocumentFieldValue<?>> me : docFields.entrySet()) {
				log.error("\t-{}: {}",me.getKey(),me.getValue() != null ? me.getValue().getValue() != null ? me.getValue().getValue().getClass() 
																										   : "null value"
																	    : "null");
			}
			throw new IllegalStateException(Throwables.message("The indexed document does NOT contains a field with name {}",
															   metaDataId));
		}
		return outField;
	}
	@Override
	public <T> T getFieldValueOrThrow(final FieldID metaDataId) {
		return this.<T>getFieldOrThrow(metaDataId)
				   .getValue();
	}

}
