package r01f.persistence.index.document;

import r01f.model.metadata.FieldMetaData;

/**
 * Interface used to give applications chance to customize the generated {@link IndexDocumentFieldConfig}
 * object from a {@link FieldMetaData} at {@link IndexDocumentFieldConfigSet}
 */
public interface IndexDocumentFieldConfigCustomizer {
	/**
	 * Customizes the {@link IndexDocumentFieldConfig} object generated from the {@link FieldMetaData}
	 * @param fieldConfig
	 */
	public void customize(final IndexDocumentFieldConfig<?> fieldConfig);
	
}
