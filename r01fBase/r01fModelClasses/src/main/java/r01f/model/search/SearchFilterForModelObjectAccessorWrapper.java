package r01f.model.search;

import com.google.common.annotations.GwtIncompatible;

import lombok.RequiredArgsConstructor;
import r01f.exceptions.Throwables;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.HasTypesMetaData;
import r01f.model.metadata.MetaDataDescribable;
import r01f.model.metadata.TypeFieldMetaData;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.QueryClause;
import r01f.patterns.Memoized;
import r01f.util.types.collections.CollectionUtils;

@GwtIncompatible
@RequiredArgsConstructor
public class SearchFilterForModelObjectAccessorWrapper<F extends SearchFilterForModelObjectBase<F>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final F _wrappedFilter;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public class SearchFilterQueryClausesAccessorWrapper {
		@SuppressWarnings("unchecked")
		public <T> T getValueOrNull(final FieldID fieldId) {
			final QueryClause clause = this.find(fieldId);
			return clause != null ? (T)clause.getValue()
								  : null;
		}
		@SuppressWarnings("unchecked")
		public <T> T getValueOrNull(final FieldID fieldId,
				 					final QueryClauseOccur occur) {
			final QueryClause clause = this.find(fieldId,occur);
			return clause != null ? (T)clause.getValue()
								  : null;
		}
		@SuppressWarnings("unchecked")
		public <T> T getValueOrNull(final FieldID fieldId,
									final Class<? extends QueryClause> clauseType) {
			final QueryClause clause = this.findOfType(fieldId,clauseType);
			return clause != null ? (T)clause.getValue()
							  	  : null;
		}
		@SuppressWarnings("unchecked")
		public <T> T getValueOrNull(final FieldID fieldId,
									final Class<? extends QueryClause> clauseType,
									final QueryClauseOccur occur) {
			final QueryClause clause = this.findOfType(fieldId,clauseType,occur);
			return clause != null ? (T)clause.getValue()
							  	  : null;
		}  
		// ----------------------------------------------------------------------------------------
		public boolean check(final FieldID fieldId) {
			final QueryClause clause = _wrappedFilter.getBooleanQuery() != null ? _wrappedFilter.getBooleanQuery().findQueryClause(fieldId)
										 			   					  		: null;
			return clause != null;
		}
		// ----------------------------------------------------------------------------------------  
		public QueryClause find(final FieldID fieldId) {
			final QueryClause outClause = _wrappedFilter.getBooleanQuery() != null ? _wrappedFilter.getBooleanQuery().findQueryClause(fieldId)
										 				  					 	   : null;
			return outClause;
		}
		public QueryClause find(final FieldID fieldId,
							    final QueryClauseOccur occur) {
			final QueryClause outClause = _wrappedFilter.getBooleanQuery() != null ? _wrappedFilter.getBooleanQuery().findQueryClause(fieldId,
																																	  occur)
										 				  					 	   : null;
			return outClause;
		}
		// ----------------------------------------------------------------------------------------  
		public QueryClause findOfType(final FieldID fieldId,
									  final Class<? extends QueryClause> clauseType) {
			final QueryClause outClause = _wrappedFilter.getBooleanQuery() != null ? _wrappedFilter.getBooleanQuery().findQueryClauseOfType(fieldId,
																																			clauseType)
														  					 	   : null;
			return outClause;
		}
		public QueryClause findOfType(final FieldID fieldId,
								 	  final Class<? extends QueryClause> clauseType,
									  final QueryClauseOccur occur) {
			final QueryClause outClause = _wrappedFilter.getBooleanQuery() != null ? _wrappedFilter.getBooleanQuery().findQueryClauseOfType(fieldId,
																																			clauseType,occur)
										 				  					 	   : null;
			return outClause;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Memoized<SearchFilterQueryClausesAccessorWrapper> _queryClausesAccessor = new Memoized<SearchFilterQueryClausesAccessorWrapper>() {
																									@Override
																									public SearchFilterQueryClausesAccessorWrapper supply() {
																										return new SearchFilterQueryClausesAccessorWrapper();
																									}
																							};
	public SearchFilterQueryClausesAccessorWrapper queryClauses() {
		return _queryClausesAccessor.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public class SearchFilterFiledMetaDataAccessorWrapper {
		
		private final HasTypesMetaData _hasTypesMetadata;
		
		/**
		 * Finds a field's metadata when multiple object types are returned all of them with a common {@link FieldID}
		 * For example, if TypeA and TypeB are searched and both have a summary metadata, 
		 * the summary field must be mapped to the same {@link FieldID} (ie SUMMARY) with the same type
		 * 
		 * This method ensures that all searched object types have the summary field and that the metadata type is the same
		 * @param modelObjMetaDataType 
		 * @return
		 */
		public FieldMetaData findOrNull(final FieldID metaDataId) {
			return this.find(metaDataId,
							 false);	// return null 
		}
		/**
		 * Finds a field's metadata when multiple object types are returned all with a common {@link FieldID}
		 * For example, if TypeA and TypeB are searched and both have a summary metadata, 
		 * the summary field must be mapped to the same {@link FieldID} (ie SUMMARY) with the same type
		 * 
		 * This method ensures that all searched object types have the summary field and that the metadata type is the same
		 * @param modelObjMetaDataType 
		 * @return
		 */
		public FieldMetaData findOrThrow(final FieldID metaDataId) {
			return this.find(metaDataId,
							 true);	// throw an exception if metadata not found
		}
		public FieldMetaData find(final FieldID metaDataId,
								  final boolean strict) {
			// If multiple model object types are set, ensure that all them have the provided metadata id
			FieldMetaData outFieldMetaData = null;
			for (Class<? extends MetaDataDescribable> modelObjectType : _wrappedFilter.getFilteredModelObjectTypes()) {
				TypeFieldMetaData typeFieldMetaData = _hasTypesMetadata.getTypeMetaDataFor(modelObjectType)
																  		   .findFieldByIdOrThrow(metaDataId);
				
				// check that this model object contains the field
				FieldMetaData thisModelObjFieldMetaData = typeFieldMetaData.asFieldMetaData();
				if (outFieldMetaData != null					// other model object has the field to be found
				 && thisModelObjFieldMetaData == null) {	// ... but not this one
					throw new IllegalStateException(Throwables.message("The model object metadata for the type {} set at the search filter DOES NOT have the {} field sob the clause cannot be set to the filter because NOT all filtered types ({}) have this metadata",
												  		  		       modelObjectType,
												  		  		       metaDataId,CollectionUtils.of(_wrappedFilter.getFilteredModelObjectTypes())
												  		  		       							 .toStringCommaSeparated()));
				}
				
				// check the field data type
				if (thisModelObjFieldMetaData != null) {
					if (outFieldMetaData == null) {
						outFieldMetaData = thisModelObjFieldMetaData;
					} else if (outFieldMetaData.getDataType() != thisModelObjFieldMetaData.getDataType()) {	// check the field data type is the same 
						throw new IllegalStateException(Throwables.message("Multiple model object types were set at the filter ({}) BUT not all of them uses the same type for the field with id={}",
																		   CollectionUtils.of(_wrappedFilter.getFilteredModelObjectTypes())
																		   								    .toStringCommaSeparated(),
																		   outFieldMetaData.getIndexableFieldId()));
					}
				}
			}
			if (strict && outFieldMetaData == null) throw new IllegalStateException(Throwables.message("Any of the search filter model objects ({}) has a metadata with id={}",
																									   CollectionUtils.of(_wrappedFilter.getFilteredModelObjectTypes())
																									   				  .toStringCommaSeparated(),
																					   				   metaDataId));
			return outFieldMetaData;
		}
	}
	public SearchFilterFiledMetaDataAccessorWrapper fieldMetaDataUsing(final HasTypesMetaData hasTypesMetadata) {
		return new SearchFilterFiledMetaDataAccessorWrapper(hasTypesMetadata);
	}
}
