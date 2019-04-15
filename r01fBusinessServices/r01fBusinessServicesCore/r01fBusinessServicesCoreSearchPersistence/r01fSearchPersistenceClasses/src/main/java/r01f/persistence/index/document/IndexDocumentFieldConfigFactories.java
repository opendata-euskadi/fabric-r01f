package r01f.persistence.index.document;

import java.util.regex.Pattern;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.OIDBase;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Factories for {@link IndexDocumentFieldConfig}
 * @see IndexDocumentFieldConfigSet
 */
public class IndexDocumentFieldConfigFactories {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IndexDocumentFieldConfigFactory {
		/**
		 * @return an id of the factory
		 */
		public IndexDocumentFieldCfgFactoryId getId();
		/**
		 * Checks if this factory is usable for the provided field
		 * @param fieldId the field id
		 * @return the field's config
		 */
		public boolean isUsableFor(IndexDocumentFieldID fieldId);
		/**
		 * Creates the field config for the provided name
		 * @param fielId
		 * @return
		 */
		public IndexDocumentFieldConfig<?> createFieldConfigFor(IndexDocumentFieldID fielId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Identifier for a {@link IndexDocumentFieldConfigFactory}
	 */
	@MarshallType(as="indexDocumentFieldCfgFactoryId")
	@EqualsAndHashCode(callSuper=true)
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static class IndexDocumentFieldCfgFactoryId 
	     		extends OIDBase<String> {
	
		private static final long serialVersionUID = -3176550010030348277L;
	/////////////////////////////////////////////////////////////////////////////////////////
	//  FIELDS
	/////////////////////////////////////////////////////////////////////////////////////////
		@Getter private final String _id; 
	/////////////////////////////////////////////////////////////////////////////////////////
	//  
	/////////////////////////////////////////////////////////////////////////////////////////
		public static IndexDocumentFieldCfgFactoryId forId(final String id) {
			return new IndexDocumentFieldCfgFactoryId(id);
		}
		public static IndexDocumentFieldCfgFactoryId forId(final FieldID id) {
			return IndexDocumentFieldCfgFactoryId.forId(id.asString());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * {@link IndexDocumentFieldConfigFactory} to use when the usability is going to be 
	 * checking if the fieldName equals a given one
	 */
	public static abstract class IndexDocumentFieldConfigFactoryMatchingFieldNameByEquality 
			 		  implements IndexDocumentFieldConfigFactory {
		private final IndexDocumentFieldCfgFactoryId _fieldCfgId;
		
		public IndexDocumentFieldConfigFactoryMatchingFieldNameByEquality(final FieldID fieldId) {
			_fieldCfgId = IndexDocumentFieldCfgFactoryId.forId(fieldId);
		}
		@Override
		public IndexDocumentFieldCfgFactoryId getId() {
			return _fieldCfgId;
		}
		@Override
		public boolean isUsableFor(final IndexDocumentFieldID fieldId) {
			return _fieldCfgId.asString().equals(fieldId.asString());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * {@link IndexDocumentFieldConfigFactory} to use when the usability is going to be 
	 * checking if the fieldName matches a given {@link Pattern}
	 */
	public static abstract class IndexDocumentFieldConfigFactoryMatchingFieldNameByPattern 
			 		  implements IndexDocumentFieldConfigFactory {
		private final Pattern _fieldIdPattern;
		private final IndexDocumentFieldCfgFactoryId _fieldCfgId;

		public IndexDocumentFieldConfigFactoryMatchingFieldNameByPattern(final Pattern pattern) {
			_fieldIdPattern = pattern;
			_fieldCfgId = IndexDocumentFieldCfgFactoryId.forId(_fieldIdPattern.pattern());		// the id is the pattern
		}
		@Override
		public IndexDocumentFieldCfgFactoryId getId() {
			return _fieldCfgId;
		}
		@Override
		public boolean isUsableFor(final IndexDocumentFieldID fieldId) {
			return fieldId.asString()
						  .matches(_fieldIdPattern.pattern());
		}		
	}
}
