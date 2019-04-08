package r01f.persistence.search;

import com.google.common.base.Function;

import lombok.RequiredArgsConstructor;
import r01f.facets.HasID;
import r01f.facets.HasName;
import r01f.facets.HasOID;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.IndexableModelObject;
import r01f.model.PersistableModelObject;
import r01f.model.TrackableModelObject.HasTrackableFacet;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;
import r01f.model.metadata.TypeMetaData;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.patterns.Factory;
import r01f.securitycontext.SecurityContext;

/**
 * Transforms a {@link PersistableModelObject} to a {@link SearchResultItem}
 * @param <M>
 * @param <I>
 */
@RequiredArgsConstructor
public class PersistableObjectToSearchResultItem<M extends IndexableModelObject,
												 I extends SearchResultItemForModelObject<M>>
  implements TransformsPersistableObjectToSearchResultItem<M,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final TypeMetaData<?> _modelObjectMetadata;
	private final Factory<I> _searchResultItemFactory;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns this transformer as a {@link Function}
	 * @param securityContext
	 * @return
	 */
	public Function<M,I> asTransformFuncion(final SecurityContext securityContext,
											final Language lang) {
		return new Function<M,I>() {			
						@Override
						public I apply(final M modelObj) {
							return PersistableObjectToSearchResultItem.this.objToSearchResultItem(securityContext,
																		  						  modelObj,
																		  						  lang);
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings({ "unchecked","cast" })
	public I objToSearchResultItem(final SecurityContext securityContext,
								   final M modelObj,
								   final Language lang) {
		// create the item
		I item = _searchResultItemFactory.create();
		
		// Model object type
		item.unsafeSetModelObjectType((Class<? extends IndexableModelObject>)modelObj.getClass());
		item.setModelObjectTypeCode(_modelObjectMetadata.getTypeMetaData()
													    .modelObjTypeCode());
		
		// Tracking info
		if (modelObj instanceof HasTrackableFacet) {
			HasTrackableFacet trackableObj = (HasTrackableFacet)modelObj;
			item.setTrackingInfo(trackableObj.getTrackingInfo());
		}
		// numeric id
		if (modelObj instanceof HasNumericID) {
			item.setNumericId(modelObj.getNumericId());
		}
		// EntityVersion 
		if (modelObj instanceof HasEntityVersion) {
			item.setEntityVersion(modelObj.getEntityVersion());
		}
		// OID
		if (modelObj instanceof HasOID
		 && item instanceof HasOID) {
			HasOID<? extends OID> hasOidObj = (HasOID<? extends OID>)modelObj;
			OID oid = hasOidObj.getOid();
			
			HasOID<? extends OID> itemHasOid = (HasOID<? extends OID>)item;
			itemHasOid.unsafeSetOid(oid);
		}	
		// ID
		if (modelObj instanceof HasID
		 && item instanceof HasID) {
			HasID<? extends OID> hasIdObj = (HasID<? extends OID>)modelObj;
			OID id = hasIdObj.getId();
			
			HasID<? extends OID> itemHasId = (HasID<? extends OID>)item;
			itemHasId.unsafeSetId(id);
		}
		// Name
		if (modelObj instanceof HasName
		 && item instanceof HasName) {
			if (item instanceof HasLangDependentNamedFacet
			 && modelObj instanceof HasLangDependentNamedFacet) {
				HasLangDependentNamedFacet itemHasName = (HasLangDependentNamedFacet)item;
				HasLangDependentNamedFacet objHasName = (HasLangDependentNamedFacet)modelObj;
				
				LanguageTexts nameByLang = objHasName.getNameByLanguage();
				itemHasName.setNameByLanguage(nameByLang);
			} else if (item instanceof HasLangInDependentNamedFacet
				    && modelObj instanceof HasLangDependentNamedFacet) {
				HasLangInDependentNamedFacet itemHasName = (HasLangInDependentNamedFacet)item;
				HasLangInDependentNamedFacet objHasName = (HasLangInDependentNamedFacet)modelObj;
				
				String name = objHasName.getName();
				itemHasName.setName(name);
			} else {
				throw new IllegalStateException("Both search result item and model object are instances of " + HasName.class.getSimpleName() + " BUT " + 
											    "search result item is a " + item.getClass() + " instance and " +
											    "model object is a " + modelObj.getClass() + " instance!");
			}
		}
		return item;
	}
}
