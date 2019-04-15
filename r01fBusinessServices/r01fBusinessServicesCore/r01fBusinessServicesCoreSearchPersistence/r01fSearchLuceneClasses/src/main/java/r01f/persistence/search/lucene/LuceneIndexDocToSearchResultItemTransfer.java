package r01f.persistence.search.lucene;

import lombok.extern.slf4j.Slf4j;
import r01f.annotations.Immutable;
import r01f.facets.HasID;
import r01f.facets.HasLanguage;
import r01f.facets.HasName;
import r01f.facets.HasOID;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.IndexableModelObject;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.HasMetaDataForHasEntityVersionModelObject;
import r01f.model.metadata.HasMetaDataForHasIDModelObject;
import r01f.model.metadata.HasMetaDataForHasLanguageDependentName;
import r01f.model.metadata.HasMetaDataForHasLanguageInDependentName;
import r01f.model.metadata.HasMetaDataForHasLanguageModelObject;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.HasMetaDataForHasSummaryModelObject;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataForModelObjectBase;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.persistence.lucene.LuceneSearchResultDocument;
import r01f.reflection.ReflectionUtils;
import r01f.types.summary.Summary;

@Slf4j
class LuceneIndexDocToSearchResultItemTransfer {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets the item common fields 
	 * @param item
	 * @param doc
	 * @param filter
	 */
	public static <I extends SearchResultItemForModelObject<? extends IndexableModelObject>> void setResultItemFieldsFromIndexedDoc(final LuceneSearchResultDocument<? extends IndexableModelObject> doc,
																														    		final I item) {
		TypeMetaData<? extends IndexableModelObject> typeMetaData = doc.getModelObjectMetaData();
		
		// Model object type
		item.unsafeSetModelObjectType((Class<? extends IndexableModelObject>)typeMetaData.getRawType());
		item.setModelObjectTypeCode(typeMetaData.getTypeMetaData().modelObjTypeCode());
		
		// OID
		if (typeMetaData.hasFacet(HasOID.class)
		 && item instanceof HasOID) {
			FieldID fieldId = FieldID.from(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID);
			OID oid = doc.<OID>getFieldValueOrThrow(fieldId);
			HasOID<?> itemHasOid = (HasOID<?>)item;
			itemHasOid.unsafeSetOid(oid);
		}
		// ID
		if (typeMetaData.hasFacet(HasID.class)
		 && item instanceof HasID) {
			FieldID fieldId = FieldID.from(HasMetaDataForHasIDModelObject.SEARCHABLE_METADATA.ID);
			OID id = doc.<OID>getFieldValueOrThrow(fieldId);
			HasID<?> itemHasId = (HasID<?>)item;
			itemHasId.unsafeSetId(id);			
		}
		// numeric id
		if (typeMetaData.hasFacet(HasNumericID.class)) {
			FieldID fieldId = FieldID.from(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.NUMERIC_ID);
			Long numericId = doc.getFieldValueOrThrow(fieldId);
			if (numericId != null && numericId > 0) item.setNumericId(numericId);
		}
		// EntityVersion 
		if (typeMetaData.hasFacet(HasEntityVersion.class)) {
			FieldID fieldId = FieldID.from(HasMetaDataForHasEntityVersionModelObject.SEARCHABLE_METADATA.ENTITY_VERSION);
			long entityVersion = doc.<Long>getFieldValueOrThrow(fieldId);
			item.setEntityVersion(entityVersion);
		}
		// Name
		if (typeMetaData.hasFacet(HasName.class)
		 && item instanceof HasName) {
			if (typeMetaData.hasFacet(HasLangDependentNamedFacet.class)
			&& item instanceof HasLangDependentNamedFacet) {
				// Lang dependent name
				FieldID fieldId = FieldID.from(HasMetaDataForHasLanguageDependentName.SEARCHABLE_METADATA.NAME_BY_LANGUAGE);
				LanguageTexts nameByLang = doc.getFieldValue(fieldId);
				if (nameByLang != null) {
					HasLangDependentNamedFacet itemHasName = (HasLangDependentNamedFacet)item;
					itemHasName.setNameByLanguage(nameByLang);
				}
			} else if (typeMetaData.hasFacet(HasLangInDependentNamedFacet.class)
					&& item instanceof HasLangInDependentNamedFacet) {
				// Lang in-dependent name
				FieldID fieldId = FieldID.from(HasMetaDataForHasLanguageInDependentName.SEARCHABLE_METADATA.NAME);
				String name = doc.getFieldValue(fieldId);
				if (name != null) {
					HasLangInDependentNamedFacet itemHasName = (HasLangInDependentNamedFacet)item;
					itemHasName.setName(name);
				}
			}
		}
		// Summary
		// - Language dependent summary (stored as summary.{} -a field for every lang-)
		// - Language independent summary stored in a single lucene field
		if (typeMetaData.hasFacet(HasSummaryFacet.class)
		 && item instanceof HasSummaryFacet) {			
			FieldID fieldId = FieldID.from(HasMetaDataForHasSummaryModelObject.SEARCHABLE_METADATA.SUMMARY);
			Summary summary = doc.getFieldValue(fieldId);
			if (summary != null) {
				HasSummaryFacet itemHasSumm = (HasSummaryFacet)item;
				Summarizable summ = itemHasSumm.asSummarizable();
				if (summ != null) {
					boolean isImmutableSum = ReflectionUtils.typeOrSuperTypeHasAnnotation(summ.getClass(),Immutable.class);
					if (!isImmutableSum) summ.setSummary(summary);
				}
			} else {
				log.warn("There're no summary fields stored in a lucene document for a result of type {}",
						 typeMetaData.getRawType(),typeMetaData.getRawType(),ModelObjectData.class);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCH RESULT ITEM - CONTAINED OBJECT
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void setContainedObjectFieldValuesFromSearchResultDoc(final LuceneSearchResultDocument<? extends IndexableModelObject> doc,
																		final IndexableModelObject modelObject) {
		// oid
		if (modelObject.hasFacet(HasOID.class)) {
			FieldID fieldId = FieldID.from(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID);
			OID oid = doc.getFieldValue(fieldId);
			if (oid != null) ((HasOID<?>)modelObject).unsafeSetOid(oid);
		}
		// id
		if (modelObject.hasFacet(HasID.class)) {
			FieldID fieldId = FieldID.from(HasMetaDataForHasIDModelObject.SEARCHABLE_METADATA.ID);
			OID id = doc.getFieldValue(fieldId);
			if (id != null) ((HasID<?>)modelObject).unsafeSetId(id);
		}
		// language
		if (modelObject.hasFacet(HasLanguage.class)) {
			FieldID fieldId = FieldID.from(HasMetaDataForHasLanguageModelObject.SEARCHABLE_METADATA.LANGUAGE);
			Language lang = doc.getFieldValue(fieldId);
			if (lang != null) ((HasLanguage)modelObject).setLanguage(lang);
		}
		// name
		if (modelObject.hasFacet(HasName.class)) {
			if (modelObject.hasFacet(HasLangDependentNamedFacet.class)) {
				FieldID fieldId = FieldID.from(HasMetaDataForHasLanguageDependentName.SEARCHABLE_METADATA.NAME_BY_LANGUAGE);
				LanguageTexts nameByLang = doc.getFieldValue(fieldId);
				if (nameByLang != null) ((HasLangDependentNamedFacet)modelObject).setNameByLanguage(nameByLang);
			} else if (modelObject.hasFacet(HasLangInDependentNamedFacet.class)) {
				FieldID fieldId = FieldID.from(HasMetaDataForHasLanguageInDependentName.SEARCHABLE_METADATA.NAME);
				String name = doc.getFieldValue(fieldId);
				if (name != null) ((HasLangInDependentNamedFacet)modelObject).setName(name);
			}
		}
		// summary
		if (modelObject.hasFacet(HasSummaryFacet.class)) {
			FieldID fieldId = FieldID.from(HasMetaDataForHasSummaryModelObject.SEARCHABLE_METADATA.SUMMARY);
			Summary summary = doc.getFieldValue(fieldId);
			if (summary != null) {
				HasSummaryFacet objHasSummary = (HasSummaryFacet)modelObject;
				Summarizable summ = objHasSummary.asSummarizable();
				if (summ != null) {
					boolean isImmutableSum = ReflectionUtils.typeOrSuperTypeHasAnnotation(summ.getClass(),Immutable.class);
					if (!isImmutableSum) summ.setSummary(summary);
				}
			}
		}
	}
	/**
	 * Sets the model object's common fields from the search results
	 * @param item
	 * @param modelObject
	 * @param doc
	 */
	public static <I extends SearchResultItemForModelObject<? extends IndexableModelObject>> void copyFieldValuesFomSearchResultItemToContainedObject(final I item,
																																					  final IndexableModelObject modelObject) {
		// oid
		if (modelObject.hasFacet(HasOID.class)
		 && item instanceof HasOID) {
			HasOID<?> itemHasOid = (HasOID<?>)item;
			HasOID<?> objHasOid = (HasOID<?>)modelObject;
			
			if (itemHasOid.getOid() != null && objHasOid.getOid() == null) objHasOid.unsafeSetOid(itemHasOid.getOid());
		}
		// id
		if (modelObject.hasFacet(HasID.class)
		 && item instanceof HasID) {
			HasID<?> itemHasOid = (HasID<?>)item;
			HasID<?> objHasOid = (HasID<?>)modelObject;
			
			if (itemHasOid.getId() != null && objHasOid.getId() == null) objHasOid.unsafeSetId(itemHasOid.getId());
		}
		// NumericId
		if (modelObject.hasFacet(HasNumericID.class)) {
			long numericId = item.getNumericId();
			((HasNumericID)modelObject).setNumericId(numericId);
		}
		// Name
		if (modelObject.hasFacet(HasName.class)
		 && item instanceof HasName) {
			if (modelObject.hasFacet(HasLangDependentNamedFacet.class)
			 && item instanceof HasLangDependentNamedFacet) {
				// Lang dependent name
				HasLangDependentNamedFacet modelHasName = (HasLangDependentNamedFacet)modelObject;
				HasLangDependentNamedFacet itemHasName = (HasLangDependentNamedFacet)item;
				
				if (itemHasName.getName() != null && modelHasName.getName() == null) modelHasName.setNameByLanguage(itemHasName.getNameByLanguage());
			} else {
				// Lang independent name
				HasLangInDependentNamedFacet modelHasName = (HasLangInDependentNamedFacet)modelObject;
				HasLangInDependentNamedFacet itemHasName = (HasLangInDependentNamedFacet)item;
				
				if (itemHasName.getName() != null && modelHasName.getName() == null) modelHasName.setName(itemHasName.getName());
			}
		}
		// Summary
		if (modelObject.hasFacet(HasSummaryFacet.class)
		 && item instanceof HasSummaryFacet) {
			HasSummaryFacet modelObjHasSummary = (HasSummaryFacet)modelObject;
			HasSummaryFacet itemHasSummary = (HasSummaryFacet)item;
			Summarizable modelObjSumm = modelObjHasSummary.asSummarizable();
			Summarizable itemSumm = itemHasSummary.asSummarizable();
			if (itemSumm != null && itemSumm.getSummary() != null
			 && modelObjSumm != null && modelObjSumm.getSummary() == null) {
				boolean isImmutableSum = ReflectionUtils.typeOrSuperTypeHasAnnotation(modelObjSumm.getClass(),Immutable.class);
				if (!isImmutableSum) modelObjSumm.setSummary(itemSumm.getSummary());
			}
		}
	}
}
