package r01f.persistence.search.db;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import r01f.guids.OID;
import r01f.guids.OIDs;
import r01f.guids.PersistableObjectOID;
import r01f.locale.Language;
import r01f.model.IndexableModelObject;
import r01f.model.ModelObject;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.TypeFieldMetaData;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.model.search.SearchFilterForModelObject;
import r01f.model.search.SearchResultItemContainsPersistableObject;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.patterns.Factory;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.persistence.search.PersistableObjectToSearchResultItem;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.SearchServicesForModelObject;
import r01f.util.types.collections.CollectionUtils;

/**
 * Usage: 
 * <pre class='brush:java'>
 * 		MyDBSearcherForX = new MyDBSearcherForX(MyXDBEntity.class,
 * 												entityManager,
 *			  									new FactoryFrom<F,DBSearchQueryForModelObject<DB>>() {
 *														@Override
 *														public DBSearchQueryForModelObject<DB> from(final F filter) {
 *															return DBSearchQueryForModelObjectBuilder.forDBEntityType(dbEntityType)
 *																 			 .using(entityManager)
 *																  			 .supportsFullText(isFullTextSupported)
 *																  			 .noUILanguage()
 *																  			 .withFilterType(filterType);
 *															}
 *												},
 *												transformsDBEntityToSearchResultItem);
 * </pre>
 * @param <F>
 * @param <I>
 * @param <DB>
 */
public abstract class DBSearcherForModelObjectBase<F extends SearchFilterForModelObject,I extends SearchResultItemForModelObject<? extends IndexableModelObject>,
									 			   DB extends DBEntity>
			  extends DBSearcherBase<F,I,
			  						 DB> 
		   implements SearchServicesForModelObject<F,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected <S extends DBSearchQuery<F,DB>> DBSearcherForModelObjectBase(final Class<DB> dbEntityType,
										   								   final EntityManager entityManager,
										   								   final Factory<S> searchQueryFactory,
										   								   final TransformsDBEntityToSearchResultItem<DB,I> transformsDBEntityToSearchResultItem) {
		super(entityManager,
			  searchQueryFactory,
			  transformsDBEntityToSearchResultItem);
	}
	@SuppressWarnings("unchecked")
	protected <M extends IndexableModelObject,J extends SearchResultItemForModelObject<M>,
			   S extends DBSearchQuery<F,DB>> DBSearcherForModelObjectBase(final Class<M> modelObjType,final Class<DB> dbEntityType,
							 			   								   final EntityManager entityManager,
							 			   								   final Factory<S> searchQueryFactory,
							 			   								   final TransformsDBEntityIntoModelObject<DB,M> dbEntityToModelObjectTransformer,
							 			   								   final Factory<I> searchResultItemsFactory) {
		this(dbEntityType,
			 entityManager,
			 searchQueryFactory,
			 new TransformsDBEntityToSearchResultItemBase<DB,I>() {
							@Override
							public I dbEntityToSearchResultItem(final SecurityContext securityContext,
																final DB dbEntity,
																final Language lang) {
								// [0] - Get the model object from the dbEntity
								M modelObj = dbEntityToModelObjectTransformer.dbEntityToModelObject(securityContext,
																									dbEntity);
								
								// [1] - Use the search result item factory to create an item
								//		 (with a bit of dirty type tricks)
								PersistableObjectToSearchResultItem<M,J> modelObjToSearchResultItem = new PersistableObjectToSearchResultItem<M,J>(TypeMetaDataInspector.singleton()
																																							.getTypeMetaDataFor(modelObjType),
																																				   (Factory<J>)searchResultItemsFactory);
								I item = (I)modelObjToSearchResultItem.objToSearchResultItem(securityContext,
																						  	 modelObj,lang);
								
								// [3] - Set the model object
								if (item instanceof SearchResultItemContainsPersistableObject) {
									SearchResultItemContainsPersistableObject<?,?> itemContainsPersistable = (SearchResultItemContainsPersistableObject<?,?>)item;
									itemContainsPersistable.unsafeSetModelObject(modelObj);
								}
								
								// [99] - Return
								return item;
							}
			 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns all record oid 
	 * @param securityContext
	 * @param filter
	 * @return
	 */
	@Override @SuppressWarnings("unchecked")
	public <O extends PersistableObjectOID> Collection<O> filterRecordsOids(final SecurityContext securityContext,
														   					final F filter) {
		// [0]: guess the model object type
		Class<? extends ModelObject> modelObjType = CollectionUtils.pickOneAndOnlyElement(filter.getFilteredModelObjectTypes(),
																						  "This type is only suitable for filters with a single model object type");
		final TypeMetaData<?> typeMetaData = TypeMetaDataInspector.singleton()
																  .getTypeMetaDataFor(modelObjType);
		
		// [1]: Build the query
		Query q = _searchQueryFactory.create()
								     .getOidsQuery(filter);
		
		// [2]: Run the query and transform results 
		Collection<String> oidsAsDB = q.getResultList();
		Collection<? extends OID> oids = CollectionUtils.hasData(oidsAsDB) 
												? Collections2.transform(oidsAsDB,
																		 new Function<String,OID>() {
																					@Override
																					public OID apply(final String pk) {
																						TypeFieldMetaData oidFieldMetaData = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID);
																						Class<? extends OID> oidType = (Class<? extends OID>)oidFieldMetaData.getRawFieldType();
																						return OIDs.createOIDFromString(oidType,pk);
																					}
																		 })
												: null;
		return (Collection<O>)oids;
	}
}
