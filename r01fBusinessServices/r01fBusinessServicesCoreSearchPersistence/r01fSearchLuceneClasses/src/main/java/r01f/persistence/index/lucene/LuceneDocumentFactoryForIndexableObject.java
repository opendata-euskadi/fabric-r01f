package r01f.persistence.index.lucene;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.exceptions.Throwables;
import r01f.model.IndexableModelObject;
import r01f.persistence.index.IndexableFieldValuesExtractor;
import r01f.persistence.index.document.IndexDocumentFieldConfig;
import r01f.persistence.index.document.IndexDocumentFieldConfigSet;
import r01f.persistence.index.document.IndexDocumentFieldID;

/**
 * Factory of a lucene's {@link Document} and its {@link Field}s from a {@link IndexDocumentFieldConfigSet}
 * that defines each field's name, whether it's value is stored, indexed, etc
 * @see IndexDocumentFieldConfigSet
 * 
 * The normal usage is:
 * <ol>
 * 		<li>Provide a {@link IndexDocumentFieldConfigSet}.
 * 			<pre class='brush:java'>
 * 				LuceneDocumentFactoryForIndexableModelObject fieldsFactory = LuceneDocumentFactoryForIndexableModelObject.of(fieldsConfigSet);	
 * 			</pre>
 * 		</li>
 * 		<li>Extract the field's values using some {@link IndexableFieldValuesExtractor}
 * 			<pre class='brush:java'> 
 *				IndexableFieldValuesExtractor<ModelObjectType> indexableFieldValuesExtractor = _indexableFieldValuesExtractorProvider.get();
 *				IndexableFieldValueSet indexableFieldsValues = indexableFieldValuesExtractor.extractFields(securityContext,
 *										 			 													   modelObject);
 *			</pre>
 * 		</li>
 * 		<li>Create a template for each of the extracted fields using {@link LuceneDocumentFactoryForIndexableObject}
 * 			previously created from the {@link IndexDocumentFieldConfigSet}
 * 			<pre class='brush:java'>
 * 				if (indexableFieldsValues.hasData()) {
 * 					for (IndexableFieldValue<?> fieldValue : fieldsValues) {
 * 						_createField(fieldsFactory,		// the previously created fields factory
 *								 	 fieldValue);		// the previously extracted fields values
 * 					}
 * 				}	
 * 			</pre>
 * 		</li>
 * 		<li>Create the document
 * 			<pre class='brush:java'>
 * 				Document doc = fieldsFactory.createDocument();	
 * 			</pre>
 * 		</li>
 * </ol>
 * The _createField() method is where the lucene's {@link Field} is created from the {@link IndexDocumentFieldConfig} using 
 * the {@link LuceneDocumentFactoryForIndexableObject} like:
 * <pre class='brush:java'>
 *		private <T> void _createField(final LuceneDocumentFactoryForIndexableModelObject fieldsFactory,
 *								  	  final IndexableFieldValue<T> indexableField) {
 *			IndexableFieldId fieldId = indexableField.getId();
 *			T fieldValue = indexableField.getValue();
 *			
 *			if (fieldId.is(IndexDocumentFieldConfigSetBase.LUCENE_ID_FIELD)) {
 *				fieldsFactory.createField(luceneFieldId)
 *					 		 .setStringValue(indexableField.getValue());
 *			} else if (fieldValue instanceof OID) {
 *				fieldsFactory.createField(luceneFieldId)
 *					 		 .setStringValue(indexableField.getValue()
 *														   .asString());	// it's an oid
 *			} else if (fieldValue instanceof String) {
 *				fieldsFactory.createField(luceneFieldId)
 *					 		 .setStringValue(indexableField.getValue());
 *			} else {
 *				...
 *			}
 *		}
 * </pre>
 * @see LuceneIndexerBase
 * 
 * IMPORTANT: This type is NOT thread safe!!!!!!!!!!!!!!!!!!
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
  class LuceneDocumentFactoryForIndexableObject 
extends LuceneDocumentFactoryBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * index document field's config
	 */
	private final IndexDocumentFieldConfigSet<? extends IndexableModelObject> _fieldsConfigSet;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static LuceneDocumentFactoryForIndexableObject of(final IndexDocumentFieldConfigSet<? extends IndexableModelObject> cfgSet) {
		return new LuceneDocumentFactoryForIndexableObject(cfgSet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Lazily creates a field
	 * @param fieldId
	 * @return
	 */
	public Field createField(final IndexDocumentFieldID fieldId) {
		IndexDocumentFieldConfig<?> fieldCfg = _fieldsConfigSet.getConfigOrThrowFor(fieldId);
		if (fieldCfg == null) throw new IllegalStateException(Throwables.message("The Lucene field config for field '{}' was NOT found at the config set",
																				 fieldId));
		
		Field field = LuceneDocumentFactoryForIndexableObject.createField(fieldCfg);
		if (field != null) {
			if (_createdFields == null) _createdFields = Lists.newArrayList();
			_createdFields.add(field);
		}
		return field;
	}
	/**
	 * Returns a Lucene {@link Field} for the provided field based on the {@link IndexDocumentFieldConfig}
	 * but with certain boosting value that overrides the boosting value of the {@link IndexDocumentFieldConfig}
	 * @param fieldId
	 * @param boost
	 * @return
	 */
	public Field createField(final IndexDocumentFieldID fieldId,
							 final float boost) {
		Field outField = this.createField(fieldId);
		outField.setBoost(boost);
		return outField;
	}
}
