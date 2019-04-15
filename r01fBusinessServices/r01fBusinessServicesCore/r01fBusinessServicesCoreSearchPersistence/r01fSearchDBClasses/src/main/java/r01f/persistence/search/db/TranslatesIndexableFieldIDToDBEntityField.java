package r01f.persistence.search.db;

import r01f.model.metadata.FieldID;
import r01f.model.search.SearchFilter;

/**
 * Any type that translates an indexable field id to a DB entity field
 */
public interface TranslatesIndexableFieldIDToDBEntityField<F extends SearchFilter> {
	/**
	 * Returns the db entity field name from the given indexable field id
	 * The filter is also handed as param since the db entity field might depend on other
	 * filter conditions such as the filtering language
	 * @param fieldId
	 * @param filter
	 * @return
	 */
	public String dbEntityFieldNameFor(final FieldID fieldId,
									   final F filter);
}
