package r01f.persistence.index.document;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.patterns.Memoized;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public class IndexDocumentFieldValueSet 
     extends HashSet<IndexDocumentFieldValue<?>> {

	private static final long serialVersionUID = -3537667625074831726L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public IndexDocumentFieldValueSet() {
		super();
	}
	public IndexDocumentFieldValueSet(final int expectedSize) {
		super(expectedSize);
	}
	public IndexDocumentFieldValueSet(final IndexDocumentFieldValue<?>... fields) {
		this(fields.length);		
		for (IndexDocumentFieldValue<?> field : fields) this.add(field);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static IndexDocumentFieldValueSet create() {
		return new IndexDocumentFieldValueSet();
	}
	public static IndexDocumentFieldValueSet create(final IndexDocumentFieldValue<?>... fields) {
		return CollectionUtils.hasData(fields) ? new IndexDocumentFieldValueSet(fields)
											   : new IndexDocumentFieldValueSet();
	}
	public static IndexDocumentFieldValueSet createWithExpectedSize(final int expectedSize) {
		return new IndexDocumentFieldValueSet(expectedSize);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are {@link IndexDocumentFieldValue}s in the {@link Set}
	 */
	public boolean hasData() {
		return this.isEmpty() == false;
	}
	/**
	 * Returns the {@link IndexDocumentFieldValue} bound to the provided field name
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> IndexDocumentFieldValue<T> get(final FieldID id) {
		IndexDocumentFieldValue<T> outValue = null;
		for (IndexDocumentFieldValue<?> fieldValue : this) {
			if (fieldValue.getIndexableFieldId().equals(id)) {
				outValue = (IndexDocumentFieldValue<T>)fieldValue;
				break;
			}
		}
		return outValue;
	}
	/**
	 * Returns a collection of the field names
	 * @return
	 */
	public Collection<FieldID> fieldIndexableFieldsIds() {
		Collection<FieldID> outFields = null;
		if (CollectionUtils.hasData(this)) {
			outFields = Lists.newArrayListWithExpectedSize(this.size());
			for (IndexDocumentFieldValue<?> field : this) {
				outFields.add(field.getIndexableFieldId());
			}
		}
		return outFields;
	}
	/**
	 * A memoized view of the ids
	 */
	@Getter private Memoized<String> _fieldIdsAsString = new Memoized<String>() {
																@Override
																protected String supply() {
																	return CollectionUtils.of(IndexDocumentFieldValueSet.this.fieldIndexableFieldsIds())
																				  		  .toStringCommaSeparated();
																}
		
														 };
}
