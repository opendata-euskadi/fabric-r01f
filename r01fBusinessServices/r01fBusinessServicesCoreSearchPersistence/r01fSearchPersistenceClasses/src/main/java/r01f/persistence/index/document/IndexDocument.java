package r01f.persistence.index.document;

import java.util.Map;

import r01f.model.IndexableModelObject;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.TypeMetaData;

/**
 * Model a search engine's index document (for example a wrapper of a Lucene Document)
 */
public interface IndexDocument<M extends IndexableModelObject> {
	/**
	 * Gets the model object's type this document is about
	 * (a model object is defined by two types: a base (generic) type and a concrete one)
	 * @return
	 */
	public TypeMetaData<M> getModelObjectMetaData();
	/**
	 * Transform all lucene indexed fields ({@link IndexableField}s) to instances of {@link IndexDocumentFieldValue}
	 * Beware that sometimes:
	 * 	- a single metadata is stored in multiple lucene-indexed fields (ie language-dependent summaries or ranges)
	 * 	- a metadata can contain multiple values
	 * @return
	 */
	public Map<FieldID,IndexDocumentFieldValue<?>> getFields();
	/**
	 * Returns a document's field by it's id
	 * @param metaDataId
	 * @return
	 */
	public <T> IndexDocumentFieldValue<T> getField(final FieldID metaDataId);
	/**
	 * Returns a document's field value by it's id
	 * @param metaDataId
	 * @return
	 */
	public <T> T getFieldValue(final FieldID metaDataId);
	/**
	 * Returns a document's field by it's id or throw an {@link IllegalStateException}
	 * if the field is NOT found
	 * @param metaDataId
	 * @return
	 */
	public <T> IndexDocumentFieldValue<T> getFieldOrThrow(final FieldID metaDataId);
	/**
	 * Returns a document's field value by it's id or throw an {@link IllegalStateException}
	 * if the field is NOT found
	 * @param metaDataId
	 * @return
	 */
	public <T> T getFieldValueOrThrow(final FieldID metaDataId);
}
