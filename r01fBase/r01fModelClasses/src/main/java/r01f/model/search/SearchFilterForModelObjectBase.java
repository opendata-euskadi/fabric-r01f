package r01f.model.search;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.generics.TypeRef;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.ModelObject;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.HasMetaDataForHasFullTextSummaryModelObject;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.HasMetaDataForHasTrackableFacetForModelObject;
import r01f.model.metadata.HasTypesMetaData;
import r01f.model.metadata.MetaDataDescribable;
import r01f.model.metadata.TypeMetaDataForModelObjectBase;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.BooleanQueryClause.QualifiedQueryClause;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.ContainedInQueryClause;
import r01f.model.search.query.ContainsTextQueryClause;
import r01f.model.search.query.EqualsQueryClause;
import r01f.model.search.query.QueryClause;
import r01f.model.search.query.RangeQueryClause;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

@GwtIncompatible
@Slf4j
@Accessors(prefix="_")
public abstract class SearchFilterForModelObjectBase<SELF_TYPE extends SearchFilterForModelObjectBase<SELF_TYPE>>
    	   implements SearchFilterForModelObject {

	private static final long serialVersionUID = -6979312697491380544L;
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The language in which the user is doing the filter
	 */
	@MarshallField(as="uiLanguage",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _UILanguage;
	/**
	 * The query that wraps all the clauses
	 */
	@MarshallField(as="booleanQuery")
	@Getter @Setter private BooleanQueryClause _booleanQuery;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  NOT SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	private transient Collection<Class<? extends ModelObject>> _modelObjectTypes;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  WRAPPERS & UTILs
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	@Getter protected transient final SearchFilterForModelObjectAccessorWrapper<SELF_TYPE> _accessorWrapper;
	
	@MarshallIgnoredField
	@Getter protected transient final SearchFilterForModelObjectModifierWrapper<SELF_TYPE> _modifierWrapper;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SearchFilterForModelObjectBase() {
		_accessorWrapper = new SearchFilterForModelObjectAccessorWrapper<SELF_TYPE>((SELF_TYPE)this);
		_modifierWrapper = new SearchFilterForModelObjectModifierWrapper<SELF_TYPE>((SELF_TYPE)this);
	}
	public <M extends ModelObject> SearchFilterForModelObjectBase(final Class<? extends M> modelObjectType) {
		this();
		if (modelObjectType != null) {
			Collection<Class<? extends ModelObject>> modelObjectTypes = new HashSet<Class<? extends ModelObject>>(1);
			modelObjectTypes.add(modelObjectType);
			this.setModelObjectTypesToBeFiltered(modelObjectTypes);
		}
	}
	public <M extends ModelObject> SearchFilterForModelObjectBase(final Class<? extends M>... modelObjectTypes) {
		this();
		if (CollectionUtils.hasData(modelObjectTypes)) {
			Collection<Class<? extends ModelObject>> col = Arrays.<Class<? extends ModelObject>>asList(modelObjectTypes);
			this.setModelObjectTypesToBeFiltered(col);
		}
	}
	public <M extends ModelObject> SearchFilterForModelObjectBase(final Collection<Class<? extends M>> modelObjectTypes) {
		this();
		if (CollectionUtils.hasData(modelObjectTypes)) {
			Collection<Class<? extends ModelObject>> col = FluentIterable.from(modelObjectTypes)
																  .transform(new Function<Class<? extends M>,Class<? extends ModelObject>>() {
																					@Override @SuppressWarnings("cast")
																					public Class<? extends ModelObject> apply(final Class<? extends M> type) {
																						return type;
																					}
																  			 })
																  .toList();
			this.setModelObjectTypesToBeFiltered(col);
		}
	}
	protected <F extends SearchFilterForModelObjectBase<F>> void _copy(final F other) {
		_UILanguage = other.getUILanguage();
		_booleanQuery = other.getBooleanQuery();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NO QUERY CLAUSES
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isEmpty() {
		return _booleanQuery == null
			|| CollectionUtils.isNullOrEmpty(_booleanQuery.getClauses());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UI LANGUAGE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the language the user is searching in... this could NOT be the same
	 * as the language the user is filtering objects
	 * @param lang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE theUserIsSearchingIn(final Language lang) {
		_UILanguage = lang;
		return (SELF_TYPE)this;
	}
	/**
	 * Returns the UI language or the provided default one if the stored UI language is null
	 * @param def
	 * @return
	 */
	public Language getUILanguageOrDefault(final Language def) {
		return _UILanguage != null ? _UILanguage : def;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FILTERED OBJECT TYPES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<Class<? extends ModelObject>> getFilteredModelObjectTypes() {
		return _modelObjectTypes;
	}
	@Override
	public Collection<Long> getFilteredModelObjectTypesCodesUsing(final HasTypesMetaData hasTypesMetaData) {
		return FluentIterable.from(this.getFilteredModelObjectTypes())
									   .transform(new Function<Class<? extends MetaDataDescribable>,Long>() {
														@Override 
														public Long apply(final Class<? extends MetaDataDescribable> modelObjType) {
															return hasTypesMetaData.getTypeMetaDataFor(modelObjType)
																								.getTypeMetaData()
																									.modelObjTypeCode();
														}
									 			 })
									   .toSet();
	}
	/**
	 * Sets the model object types to filter by
	 * @param modelObjTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE setModelObjectTypesToFilterBy(final Collection<Class<? extends ModelObject>> modelObjTypes) {
		this.setModelObjectTypesToBeFiltered(modelObjTypes);
		return (SELF_TYPE)this;
	}
	/**
	 * Sets the model object types to filter by
	 * @param modelObjTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE setModelObjectTypesToFilterBy(final Class<? extends ModelObject>... modelObjTypes) {
		Collection<Class<? extends ModelObject>> colTypes = Sets.newLinkedHashSet(Lists.newArrayList(modelObjTypes));
		this.setModelObjectTypesToBeFiltered(colTypes);
		return (SELF_TYPE)this;
	}
	@Override
	public void setModelObjectTypesToBeFiltered(final Collection<Class<? extends ModelObject>> modelObjectTypes) {
		Preconditions.checkArgument(CollectionUtils.hasData(modelObjectTypes),"The model object types to be filtered MUST not be null or empty");
		_modelObjectTypes = modelObjectTypes;
		
		// All the type's facets are stored at the TYPE_FACETS_FIELD_ID as a multi-valued field
		Collection<Long> typesCodes = this.getFilteredModelObjectTypesCodesUsing(TypeMetaDataInspector.singleton());
		FieldID fieldId = FieldID.from(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.TYPE_FACETS);
		
		QueryClause clause = _accessorWrapper.queryClauses()
											 .find(fieldId);
		if (clause != null) {
			@SuppressWarnings({ "unchecked" })
			ContainedInQueryClause<Long> typeContainedIn = clause.as(ContainedInQueryClause.class);
			typeContainedIn.setSpectrumFrom(typesCodes,
											Long.class);
		} else {
			ContainedInQueryClause<Long> typeContainedIn = ContainedInQueryClause.<Long>forField(fieldId)
									   					   						 .within(typesCodes.toArray(new Long[typesCodes.size()]));
			_modifierWrapper.addClause(typeContainedIn,QueryClauseOccur.MUST);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OID
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> O getOid() {
		// obviously, only a single model object type can be set to be filtered if also an oid is set
		FieldID fieldId = FieldID.from(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID);
		
		// Get the equals query clause if it exists
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		EqualsQueryClause<OID> oidClause = clause != null ? (EqualsQueryClause<OID>)clause : null;
		return oidClause != null ? (O)oidClause.getEqValue() : null;
	}
	@Override
	public <O extends OID> void setOid(final O oid) {
		if (oid == null) return;
		
		// obviously, only a single model object type can be set to be filtered if also an oid is set
		FieldID fieldId = FieldID.from(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID);
		
		// If there exists an equals query clause, modify it; if not set it
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			// existing clause
			if (clause instanceof EqualsQueryClause) {
				EqualsQueryClause<O> oidClause = clause.as(new TypeRef<EqualsQueryClause<O>>() { /* nothing */ });
				oidClause.setEqValue(oid);
			} else {
				log.warn(Strings.customized("The oid clause is NOT of the expected type {} or is null",
											EqualsQueryClause.class));
			}
		} else {
			// Non existing clause
			EqualsQueryClause<O> oidClause = EqualsQueryClause.forField(fieldId)
															  .of(oid);
			_modifierWrapper.addClause(oidClause,QueryClauseOccur.MUST);
		}
	}
	/**
	 * Fluent-API to set the oid 
	 * @param oid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <O extends OID> SELF_TYPE withOid(final O oid) {
		this.setOid(oid);
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TEXT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasTextFilter() {
		return this.getTextFilter() != null;
	}
	@Override
	public ContainsTextQueryClause getTextFilter() {
		return _getTextFilter(false);	// not strict
	}
	private ContainsTextQueryClause _getTextFilter(final boolean strict) {
		FieldID fieldId = FieldID.from(HasMetaDataForHasFullTextSummaryModelObject.SEARCHABLE_METADATA.FULL_TEXT);
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		ContainsTextQueryClause textClause = clause != null ? clause.as(ContainsTextQueryClause.class)
															: null;
		if (strict) throw new IllegalStateException("NO text filter for field " + fieldId + " at query=" + this.toCriteriaString());
		return textClause;
	}
	public String getText() {
		ContainsTextQueryClause textClause = _getTextFilter(false);	// not strict
		return textClause != null ? textClause.getText() : null;
	}
	public Language getTextLanguage() {
		ContainsTextQueryClause textClause = _getTextFilter(false);	// not strict
		return textClause != null ? textClause.getLang() : null;
	}
	public void setText(final String text) {
		this.setText(text,
					 null);		// language independent
	}
	public void setText(final String text,
						final Language lang) {
		if (Strings.isNullOrEmpty(text)) return;
		
		FieldID fieldId = FieldID.from(HasMetaDataForHasFullTextSummaryModelObject.SEARCHABLE_METADATA.FULL_TEXT);
		
		Preconditions.checkState(CollectionUtils.hasData(_modelObjectTypes),
								 "No model object type was set at fieldId filter so a text clause cannot be set");
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			// existing clause
			if (clause instanceof ContainsTextQueryClause) {
				ContainsTextQueryClause textClause = clause.as(ContainsTextQueryClause.class);
				textClause.setText(text);
			} else {
				log.warn(Strings.customized("The text clause is NOT of the expected type {} or is null",
										    ContainsTextQueryClause.class));
			}
		} else {
			// Non existing clause
			ContainsTextQueryClause textClause = ContainsTextQueryClause.forField(fieldId)
															  			.fullText(text)
															  			.in(lang);
			_modifierWrapper.addClause(textClause,QueryClauseOccur.MUST);
		}
	}
	/**
	 * Fluent-API to set the full text search 
	 * @param oid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchFilterTextLangStep<SELF_TYPE> withText(final String text) {
		return new SearchFilterTextLangStep<SELF_TYPE>((SELF_TYPE)this,
													   text);
	}	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class SearchFilterTextLangStep<FILTER_TYPE extends SearchFilterForModelObjectBase<FILTER_TYPE>> {
		private final FILTER_TYPE _filter;
		private final String _text;
		
		public FILTER_TYPE in(final Language lang) {
			_filter.setText(_text,lang);
			return _filter;
		}
		public FILTER_TYPE languageIndependent() {
			_filter.setText(_text);
			return _filter;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATOR & EDITOR
/////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
	public SELF_TYPE createdBy(final LoginID creator) {
		Preconditions.checkArgument(creator != null,
									"The creator cannot be null");
		
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATOR);
		
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			EqualsQueryClause<LoginID> eqClause = (EqualsQueryClause<LoginID>)clause; 	// clause.as(new TypeRef<EqualsQueryClause<LoginID>>() { /* nothing */ });
			eqClause.setEqValue(creator);
		} else {
			EqualsQueryClause<LoginID> eqClause = EqualsQueryClause.forField(fieldId)	
																	.of(creator);
			_modifierWrapper.addClause(eqClause,QueryClauseOccur.MUST);
		}
		return (SELF_TYPE)this;
    }
	public LoginID getCreator() {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATOR);
		return _accessorWrapper.queryClauses().getValueOrNull(fieldId);
	}
    @SuppressWarnings("unchecked")
	public SELF_TYPE lastEditedBy(final LoginID lastEditor) {
		Preconditions.checkArgument(lastEditor != null,"The creator cannot be null");
		
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATOR);
		
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			EqualsQueryClause<LoginID> eqClause = (EqualsQueryClause<LoginID>)clause; 	// clause.as(new TypeRef<EqualsQueryClause<LoginID>>() { /* nothing */ });
			eqClause.setEqValue(lastEditor);
		} else {
			EqualsQueryClause<LoginID> eqClause = EqualsQueryClause.forField(fieldId)	
																	.of(lastEditor);
			_modifierWrapper.addClause(eqClause,QueryClauseOccur.MUST);
		}
		return (SELF_TYPE)this;
    }
	public LoginID getLastEditor() {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATOR);
		return _accessorWrapper.queryClauses().getValueOrNull(fieldId);
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE createdOrEditedBy(final LoginID lastCreatorOrUpdator) {
		Preconditions.checkArgument(lastCreatorOrUpdator != null,"The creator or updator cannot be null");

		// creator = userCode OR lastUpdator = userCode
		FieldID creatorFieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATOR);
		FieldID updatorFieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATOR);
		BooleanQueryClause booleanClause = BooleanQueryClause.create("createdOrEditedBy")
														.field(creatorFieldId).should().beEqualTo(lastCreatorOrUpdator)
														.field(updatorFieldId).should().beEqualTo(lastCreatorOrUpdator)
														.build();
		_modifierWrapper.addClause(booleanClause,QueryClauseOccur.MUST);
		return (SELF_TYPE)this;
	}
	public LoginID getCreatedOrEditedByUser() {
		QualifiedQueryClause<BooleanQueryClause> bQryClause = _booleanQuery.findBooleanQueryClause("createdOrEditedBy");
		return bQryClause != null ? Iterables.getFirst(bQryClause.getClause().getClauses(),null)	// any clause
											 .getClause().<LoginID>getValue()
								  : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE / LAST UPDATE DATE
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE createdInRange(final Range<Date> range) {
		Preconditions.checkArgument(range != null,"The time range cannot be null");
		// Search for a full day
		Range<Date> newDateRange = range;
		if (range.getLowerBound() != null && 
				range.getUpperBound() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(range.getUpperBound());
			c.add(Calendar.DAY_OF_MONTH, 1);
			newDateRange = Range.openClosed(range.getLowerBound(), c.getTime());
		}
		
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATE_DATE);
		
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			RangeQueryClause<Date> rangeClause = (RangeQueryClause<Date>)clause;	// clause.as(new TypeRef<RangeQueryClause<Date>>() { /* nothing */ });
			rangeClause.setRange(newDateRange);
		} else {
			RangeQueryClause<Date> rangeClause = RangeQueryClause.forField(fieldId)
																 .of(newDateRange);
			_modifierWrapper.addClause(rangeClause,QueryClauseOccur.MUST);
		}
		return (SELF_TYPE)this;
	}
	public Range<Date> getCreatedRange() {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATE_DATE);
		return _accessorWrapper.queryClauses().getValueOrNull(fieldId);
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE lastUpdatedInRange(final Range<Date> range) {
		Preconditions.checkArgument(range != null,"The time range cannot be null");
		
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATE_DATE);
		
		QueryClause clause = _accessorWrapper.queryClauses().find(fieldId);
		if (clause != null) {
			RangeQueryClause<Date> rangeClause = (RangeQueryClause<Date>)clause;	// clause.as(new TypeRef<RangeQueryClause<Date>>() { /* nothing */ });
			rangeClause.setRange(range);
		} else {
			RangeQueryClause<Date> rangeClause = RangeQueryClause.forField(fieldId)
																 .of(range);
			_modifierWrapper.addClause(rangeClause,QueryClauseOccur.MUST);
		}
		return (SELF_TYPE)this;
	}
	public Range<Date> getLastUpdatedRange() {
		FieldID fieldId = FieldID.from(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATE_DATE);
		return _accessorWrapper.queryClauses().getValueOrNull(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
    public SearchFilterAsCriteriaString toCriteriaString() {
		SearchFilterToAndFromCriteriaStringMarshaller<SELF_TYPE> m = new SearchFilterToAndFromCriteriaStringMarshaller<SELF_TYPE>(TypeMetaDataInspector.singleton());
		return m.toCriteriaString((SELF_TYPE)this);
    }
	public static <F extends SearchFilterForModelObject> F fromCriteriaString(final SearchFilterAsCriteriaString criteriaStr) {
		SearchFilterToAndFromCriteriaStringMarshaller<F> m = new SearchFilterToAndFromCriteriaStringMarshaller<F>(TypeMetaDataInspector.singleton());
		return m.fromCriteriaString(criteriaStr);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <F extends SearchFilterForModelObject> F  createCloneOf(final F other) {
		SearchFilterAsCriteriaString serialized = other.toCriteriaString();
		return SearchFilterForModelObjectBase.<F>fromCriteriaString(serialized);
	}
}
