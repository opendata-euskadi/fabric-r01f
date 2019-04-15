package r01f.persistence.search.db;

import r01f.model.metadata.FieldID;
import r01f.model.search.SearchFilter;

/**
 * Default indexable field id to DB entity field name
 */
public class IndexableFieldIDToDBEntityFieldTranslatorByDefault<F extends SearchFilter>  
  implements TranslatesIndexableFieldIDToDBEntityField<F> {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String dbEntityFieldNameFor(final FieldID fieldId,
									   final F filter) {
		String outFieldName = fieldId.getId();
		if (!outFieldName.startsWith("_")) outFieldName = "_" + outFieldName;
		return outFieldName;
	}

}
