package r01f.persistence.index;

import java.util.Collection;
import java.util.Date;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.facets.FullTextSummarizable.HasFullTextSummaryFacet;
import r01f.facets.HasID;
import r01f.facets.HasLanguage;
import r01f.facets.HasName;
import r01f.facets.HasOID;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.util.Facetables;
import r01f.guids.OID;
import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.IndexableModelObject;
import r01f.model.PersistableModelObject;
import r01f.model.TrackableModelObject.HasTrackableFacet;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;
import r01f.model.facets.Versionable;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.HasMetaDataForHasEntityVersionModelObject;
import r01f.model.metadata.HasMetaDataForHasFullTextSummaryModelObject;
import r01f.model.metadata.HasMetaDataForHasIDModelObject;
import r01f.model.metadata.HasMetaDataForHasLanguageDependentName;
import r01f.model.metadata.HasMetaDataForHasLanguageInDependentName;
import r01f.model.metadata.HasMetaDataForHasLanguageModelObject;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.HasMetaDataForHasSummaryModelObject;
import r01f.model.metadata.HasMetaDataForHasTrackableFacetForModelObject;
import r01f.model.metadata.HasMetaDataForHasVersionInfoModelObject;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataForModelObjectBase;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.persistence.index.document.IndexDocumentFieldValue;
import r01f.persistence.index.document.IndexDocumentFieldValueSet;
import r01f.securitycontext.SecurityContext;
import r01f.types.summary.LangDependentSummary;
import r01f.types.summary.Summary;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Extracts COMMON field values from {@link PersistableModelObject}s
 * This type MUST be extended to extract the type-specific fields
 * 
 * IMPORTANT!! it's NOT thread safe!!!!!
 */
@Slf4j
@Accessors(prefix="_")
public abstract class IndexableFieldValueExtractorBase<M extends IndexableModelObject> 
           implements IndexableFieldValuesExtractor<M> {	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final Class<M> _modelObjectType;
	
	private final IndexDocumentFieldValueSet _fields = IndexDocumentFieldValueSet.create();
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public IndexableFieldValueExtractorBase(final Class<M> modelObjectType) {
		_modelObjectType = modelObjectType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ACCESSORS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Class<M> getSubjectModelObjectType() {
		return _modelObjectType;
	}
	@Override
	public IndexDocumentFieldValueSet getFields() {
		return _fields;
	}
	@Override
	public <T> void addField(final IndexDocumentFieldValue<T> fieldValue) {
		_fields.add(fieldValue);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings({ "cast","unchecked" })
	public void extractFields(final SecurityContext securityContext,
							  final M modelObj,
							  final PersistenceRequestedOperation reqOp) {
		log.debug("Extracting indexable field values of an instance of {}",
				  modelObj.getClass());
		
		TypeMetaData<M> typeMetaData = TypeMetaDataInspector.singleton()
															.getTypeMetaDataFor((Class<M>)modelObj.getClass());
		log.debug("Type MetaData\n{}",typeMetaData.debugShortInfo());
		
		// [TypeMetaDataForModelObject]--------------------------------------------------
		// The java type
		FieldMetaData typeNameField = typeMetaData.findFieldByIdOrThrow(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.TYPE)
												  .asFieldMetaData();
		_fields.add(IndexDocumentFieldValue.forMetaData(typeNameField)
										   .andValue(modelObj.getClass()));
		log.debug("\t-{}={}",typeNameField.getIndexableFieldId(),modelObj.getClass());
		
		// The concrete type in a single-valued field
		FieldMetaData typeField = typeMetaData.findFieldByIdOrThrow(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.TYPE_CODE)
											  .asFieldMetaData();
		long typeCode = typeMetaData.getTypeMetaData().modelObjTypeCode(); 
		_fields.add(IndexDocumentFieldValue.forMetaData(typeField)
									       .andValue(typeCode));
		log.debug("\t-{}={}",typeField.getIndexableFieldId(),typeCode);
		
		// The type facets in a multi-valued field
		FieldMetaData facetTypesField = typeMetaData.findFieldByIdOrThrow(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.TYPE_FACETS)
													.asFieldMetaData();
		Collection<Long> facetTypesCodes = typeMetaData.getTypeFacetsCodes();	// all the type codes associated to the type
		if (CollectionUtils.hasData(facetTypesCodes)) _fields.add(IndexDocumentFieldValue.forMetaData(facetTypesField)
								       	   												 .andValues(facetTypesCodes));
		log.debug("\t-{}={}",facetTypesField.getIndexableFieldId(),facetTypesCodes);
		
		
		// [TypeMetaDataForPersistableModelObject]---------------------------------------
		// oid
		if (modelObj instanceof HasOID) {
			OID oid = Facetables.asFacet(modelObj,HasOID.class)
								.getOid();
			if (oid != null) {
				FieldMetaData oidField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID)
													 .asFieldMetaData();
				log.debug("\t-{}={}",oidField,oid);
				_fields.add(IndexDocumentFieldValue.forMetaData(oidField)
										       	   .andValue(oid));
			}
		}
		// Entity Version
		if (modelObj instanceof HasEntityVersion) {
			long entityVersion = modelObj.getEntityVersion();
			if (entityVersion >= 0) {
				FieldMetaData entityVersionField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasEntityVersionModelObject.SEARCHABLE_METADATA.ENTITY_VERSION)
															   .asFieldMetaData();
				log.debug("\t-{}={}",entityVersionField.getIndexableFieldId(),entityVersion);
				_fields.add(IndexDocumentFieldValue.forMetaData(entityVersionField)
											   	   .andValue(entityVersion));
			}
		}
		// numeric id
		if (modelObj instanceof HasNumericID) {
			long numericId = modelObj.getNumericId();
			if (numericId >= 0) {
				FieldMetaData numericIdField = typeMetaData.findFieldByIdOrThrow(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.NUMERIC_ID)
														   .asFieldMetaData();
				log.debug("\t-{}={}",numericIdField.getIndexableFieldId(),numericId);
				_fields.add(IndexDocumentFieldValue.forMetaData(numericIdField)
									       	   	   .andValue(numericId));
			}
		}
		// version & version independent oid (it's INSIDE the OID)
		if (modelObj instanceof Versionable) {
			Versionable versionable = (Versionable)modelObj;
			VersionIndependentOID versionIndependentOid = versionable.getVersionIndependentOid();
			VersionOID version = versionable.getVersionOid();
			if (version != null) {
				FieldMetaData versionField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasVersionInfoModelObject.SEARCHABLE_METADATA.VERSION)
														 .asFieldMetaData();
				log.debug("\t-{}={}",versionField.getIndexableFieldId(),version);
				_fields.add(IndexDocumentFieldValue.forMetaData(versionField)
									           	   .andValue(version));
			}
			if (versionIndependentOid != null) {
				FieldMetaData versionIndependentField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasVersionInfoModelObject.SEARCHABLE_METADATA.VERSION_INDEPENDENT_OID)
																	.asFieldMetaData();
				log.debug("\t-{}={}",versionIndependentField.getIndexableFieldId(),versionIndependentOid);
				_fields.add(IndexDocumentFieldValue.forMetaData(versionIndependentField)
									           	   .andValue(versionIndependentOid));
			}
		}
		// language
		if (modelObj.hasFacet(HasLanguage.class)) {
			HasLanguage hasLang = modelObj.asFacet(HasLanguage.class);
			Language lang = hasLang.getLanguage();
			if (lang != null) {
				FieldMetaData langField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasLanguageModelObject.SEARCHABLE_METADATA.LANGUAGE)
													  .asFieldMetaData();
				FieldMetaData langCodeField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasLanguageModelObject.SEARCHABLE_METADATA.LANGUAGE_CODE)
														  .asFieldMetaData();
				log.debug("\t-{}={}",langField.getIndexableFieldId(),lang);
				_fields.add(IndexDocumentFieldValue.forMetaData(langField)
											   	   .andValue(lang));
				_fields.add(IndexDocumentFieldValue.forMetaData(langCodeField)
											   	   .andValue(lang.getCode()));
			}
		}
		// id
		if (modelObj.hasFacet(HasID.class)) {
			HasID<?> hasId = modelObj.asFacet(HasID.class);
			OID id = hasId.getId();
			if (id != null) {
				FieldMetaData idField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasIDModelObject.SEARCHABLE_METADATA.ID)
													.asFieldMetaData();
				log.debug("\t-{}={}",idField.getIndexableFieldId(),id);
				_fields.add(IndexDocumentFieldValue.forMetaData(idField)
											   	   .andValue(id));
			}
		}
		// name
		if (modelObj.hasFacet(HasName.class)) {
			if (modelObj.hasFacet(HasLangDependentNamedFacet.class)) {
				HasLangDependentNamedFacet hasName = modelObj.asFacet(HasLangDependentNamedFacet.class);
				LanguageTexts nameByLang = hasName.getNameByLanguage();
				
				FieldMetaData nameByLangField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasLanguageDependentName.SEARCHABLE_METADATA.NAME_BY_LANGUAGE)
														 .asFieldMetaData();
				log.debug("\t-{}={}",nameByLangField.getIndexableFieldId(),nameByLang != null ? Strings.customized("name in {}",
																												   nameByLang.getDefinedLanguages())
																							  : "NO name");
				_fields.add(IndexDocumentFieldValue.forMetaData(nameByLangField)
											   	   .andValue(nameByLang));
			}
			else if (modelObj.hasFacet(HasLangInDependentNamedFacet.class)) {
				HasLangInDependentNamedFacet hasName = modelObj.asFacet(HasLangInDependentNamedFacet.class);
				String name = hasName.getName();	
				
				FieldMetaData nameField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasLanguageInDependentName.SEARCHABLE_METADATA.NAME)
														 .asFieldMetaData();
				log.debug("\t-{}={}",nameField.getIndexableFieldId(),nameField);
				_fields.add(IndexDocumentFieldValue.forMetaData(nameField)
											   	   .andValue(name));
			}
		}
		// summary
		if (modelObj.hasFacet(HasSummaryFacet.class)) {
			HasSummaryFacet summarizable = modelObj.asFacet(HasSummaryFacet.class);
			Summary summary = summarizable.asSummarizable()
										  .getSummary();
			if (_validSummary(summary)) {
				FieldMetaData summaryField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasSummaryModelObject.SEARCHABLE_METADATA.SUMMARY)
														 .asFieldMetaData();
				log.debug("\t-{}={}",summaryField.getIndexableFieldId(),summary);
				_fields.add(IndexDocumentFieldValue.forMetaData(summaryField)
											   	   .andValue(summary));
			} else {
				log.warn("The model obj {} with oid={} has {} BUT no summary was extracted",
						 modelObj.getClass(),
						 Facetables.asFacet(modelObj,HasOID.class)
						 		   .getOid(),
						 HasSummaryFacet.class);
			}
		}
		// ... full text summary 
		if (modelObj.hasFacet(HasFullTextSummaryFacet.class)) {
			HasFullTextSummaryFacet summarizable = modelObj.asFacet(HasFullTextSummaryFacet.class);
			Summary fullText = summarizable.asFullTextSummarizable()
										   .getFullTextSummary();
			if (_validSummary(fullText)) {
				if (!fullText.isFullTextSummary()) throw new IllegalArgumentException(Throwables.message("The returned summary for {} is NOT a FULL-TEXT summary",
																									    modelObj.getClass()));
				FieldMetaData summaryField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasFullTextSummaryModelObject.SEARCHABLE_METADATA.FULL_TEXT)
														 .asFieldMetaData();
				log.debug("\t-{}={}",summaryField.getIndexableFieldId(),fullText);
				_fields.add(IndexDocumentFieldValue.forMetaData(summaryField)
										       	   .andValue(fullText));
			} else {
				log.warn("The model obj {} with oid={} has {} BUT no full text summary was extracted",
						 modelObj.getClass(),
						 Facetables.asFacet(modelObj,HasOID.class)
						 		   .getOid(),
						 HasFullTextSummaryFacet.class);
			}
		}
		// [3] - Add the create / last update info 
		if (modelObj.hasFacet(HasTrackableFacet.class)) {
			if (reqOp.is(PersistenceRequestedOperation.CREATE)) {
				FieldMetaData createDateField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATE_DATE)
															.asFieldMetaData();
				_fields.add(IndexDocumentFieldValue.forMetaData(createDateField)
											   .andValue(new Date()));
				if (securityContext.getUserCode() != null) {
					FieldMetaData creatorField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATOR)
															 .asFieldMetaData();
					_fields.add(IndexDocumentFieldValue.forMetaData(creatorField)
											       	   .andValue(securityContext.getUserCode()));
				}
			} else if (reqOp.is(PersistenceRequestedOperation.UPDATE)) {
				FieldMetaData lastUpdateDateField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATE_DATE)
																.asFieldMetaData();
				_fields.add(IndexDocumentFieldValue.forMetaData(lastUpdateDateField)
											   	   .andValue(new Date()));
				if (securityContext.getUserCode() != null) {
					FieldMetaData lastUpdatorField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATOR)
																 .asFieldMetaData();
					_fields.add(IndexDocumentFieldValue.forMetaData(lastUpdatorField)
													   .andValue(securityContext.getUserCode()));
				}
			}
		}
	}
	private static boolean _validSummary(final Summary summary) {
		// ths summary must have data
		boolean validSummary = summary != null;
		if (validSummary 
		 && summary instanceof LangDependentSummary) {
			LangDependentSummary langDepSum = (LangDependentSummary)summary;
			validSummary = CollectionUtils.hasData(langDepSum.getAvailableLanguages());
		}
		return validSummary;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <T> T getFieldValue(final FieldID metaDataId) {
		IndexDocumentFieldValue<T> fieldValue = _fields.get(metaDataId);
		return fieldValue != null ? fieldValue.getValue()
								  : null;
	}


}
