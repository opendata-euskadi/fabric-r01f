package r01f.persistence.db;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.guids.VersionIndependentOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.DBEntityForVersionableModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForVersionableModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForVersionableModelObjectImpl;
import r01f.reflection.ReflectionUtils;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.CRUDServicesForVersionableModelObjectDelegateBase;
import r01f.util.types.Dates;
import r01f.util.types.collections.CollectionUtils;

/**
 * Base type for every persistence layer type
 * @param <O>
 * @param <M>
 * @param <PK>
 * @param <DB>
 */
@Accessors(prefix="_")
@Slf4j
public abstract class DBCRUDForVersionableModelObjectBase<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet,
							  		 					  PK extends DBPrimaryKeyForVersionableModelObject,DB extends DBEntity & DBEntityForVersionableModelObject<PK>>
			  extends DBCRUDForModelObjectBase<O,M,
			  				     			   PK,DB>
	       implements DBCRUDForVersionableModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	public DBCRUDForVersionableModelObjectBase(final DBModuleConfig dbCfg,
											   final Class<M> modelObjType,final Class<DB> dbEntityType,
											   final EntityManager entityManager,
											   final Marshaller marshaller) {
		super(dbCfg,
			  modelObjType,dbEntityType,
			  entityManager,
			  marshaller);
	}
	@Deprecated
	public DBCRUDForVersionableModelObjectBase(final DBModuleConfig dbCfg,
											   final Class<M> modelObjType,final Class<DB> dbEntityType,
											   final TransformsDBEntityIntoModelObject<DB,M> dbEntityIntoModelObjectTransformer,
											   final EntityManager entityManager,
											   final Marshaller marshaller) {
		super(dbCfg,
			  modelObjType,dbEntityType,
			  dbEntityIntoModelObjectTransformer,
			  entityManager,
			  marshaller);
	}
	public DBCRUDForVersionableModelObjectBase(final Class<M> modelObjType,final Class<DB> dbEntityType,
											   final DBModuleConfig dbCfg,
											   final EntityManager entityManager,
											   final Marshaller marshaller) {
		super(modelObjType,dbEntityType,
			  dbCfg,
			  entityManager,
			  marshaller);
	}
	public DBCRUDForVersionableModelObjectBase(final Class<M> modelObjType,final Class<DB> dbEntityType,
											   final TransformsDBEntityIntoModelObject<DB,M> dbEntityIntoModelObjectTransformer,
											   final DBModuleConfig dbCfg,
											   final EntityManager entityManager,
											   final Marshaller marshaller) {
		super(modelObjType,dbEntityType,
			  dbEntityIntoModelObjectTransformer,
			  dbCfg,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Some trickery to build the key used at the entity manager's finder method
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	protected PK dbEntityPrimaryKeyFor(final O oid) {
		return (PK)DBPrimaryKeyForVersionableModelObjectImpl.from(oid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<M> activate(final SecurityContext securityContext,
								  final M entityToBeActivated) {
		return this.activate(securityContext,
							 entityToBeActivated,
							 null);		// no async callback
	}
	@Override
	public CRUDResult<M> activate(final SecurityContext securityContext,
								  final M entityToBeActivated,
								  final PersistenceOperationCallbackSpec callbackSpec) {
		throw new IllegalStateException(Throwables.message("Implemented at service level (see {}",
														   CRUDServicesForVersionableModelObjectDelegateBase.class));
	}
	@Override
	public CRUDResult<M> loadActiveVersionAt(final SecurityContext securityContext,
						   			   		 final VersionIndependentOID oid,final Date date) {

		log.debug("> loading a {} entity with oid={} active at {}",_DBEntityType,oid,date);

		// [1] - Load the active version entity at the provided date
		String namedQuery = ReflectionUtils.classNameFromClassNameIncludingPackage(_DBEntityType.getName()) + "VersionActiveAt";
		Collection<DB> activeVersionEntities = this.getEntityManager().createNamedQuery(namedQuery)		// this named query MUST exist at the entity with this name
															 				.setParameter("theOid",oid.asString())
															 				.setParameter("theDate",Dates.asCalendar(date))
															 		  .getResultList();
		M activeVersion = null;
		if (CollectionUtils.hasData(activeVersionEntities)) {
			DB activeVersionDBEntity = CollectionUtils.of(activeVersionEntities)
													  .pickOneAndOnlyElement("The DB is NOT consistent: there's more than a single version of {} active at {}",
																			 oid,date);
			activeVersion = this.dbEntityToModelObject(securityContext,
												   	   activeVersionDBEntity);
		}
		// [2] - Compose the persistence operation result
		CRUDResult<M> outLoadResult = null;
		if (activeVersion != null) {
			outLoadResult = CRUDResultBuilder.using(securityContext)
											 .on(_modelObjectType)
											 .loaded()
												.entity(activeVersion);
		} else {
			outLoadResult = CRUDResultBuilder.using(securityContext)
											 .on(_modelObjectType)
											 .notLoaded()
												.becauseClientRequestedEntityWasNOTFound()
													.about(oid,date).build();
			log.warn(outLoadResult.getDetailedMessage());
		}
		return outLoadResult;
	}

	@Override
	public CRUDResult<M> loadWorkVersion(final SecurityContext securityContext,
							 			 final VersionIndependentOID oid) {
		log.debug("> loading a {} entity with oid={} work version",_DBEntityType,oid);

		// [1] - Load the work version entity
		String namedQuery = ReflectionUtils.classNameFromClassNameIncludingPackage(_DBEntityType.getName()) + "WorkVersion";
		Collection<DB> workVersionEntities = this.getEntityManager().createNamedQuery(namedQuery)		// this named query MUST exist at the entity with this name
														   				.setParameter("theOid",oid.asString())
														   			.getResultList();
		M activeVersion = null;
		if (CollectionUtils.hasData(workVersionEntities)) {
			DB activeVersionEntity = CollectionUtils.of(workVersionEntities)
												    .pickOneAndOnlyElement("The DB is NOT consistent: there's more than a single work version of {}",
																		   oid);
			activeVersion = this.dbEntityToModelObject(securityContext,
												   	   activeVersionEntity);
		}

		// [2] - Compose the RecordPersistenceOperationResult
		CRUDResult<M> outLoadResult = null;
		if (activeVersion != null) {
			outLoadResult = CRUDResultBuilder.using(securityContext)
											 .on(_modelObjectType)
											 .loaded()
												 .entity(activeVersion);
		} else {
			outLoadResult = CRUDResultBuilder.using(securityContext)
											 .on(_modelObjectType)
											 .notLoaded()
											 .becauseClientRequestedEntityWasNOTFound()
											 		.aboutWorkVersion(oid).build();
			log.warn(outLoadResult.getDetailedMessage());
		}
		return outLoadResult;
	}
	@Override
	public CRUDOnMultipleResult<M> deleteAllVersions(final SecurityContext securityContext,
													 final VersionIndependentOID oid) {
		log.debug("> deleting all versions for a {} entity with oid={}",_DBEntityType,oid);

		// [1] - Load all version entities
		String namedQuery = ReflectionUtils.classNameFromClassNameIncludingPackage(_DBEntityType.getName()) +
							"AllVersions";
		Collection<DB> allVersionEntities = this.getEntityManager().createNamedQuery(namedQuery)		// this named query MUST exist at the entity with this name
														  				.setParameter("theOid",oid.asString())
														  		   .getResultList();
		// [2] - Call delete for every version entity
		Collection<M> deletedEntities = null;
		if (CollectionUtils.hasData(allVersionEntities)) {
			for (DB dbEntity : allVersionEntities) {
				if (dbEntity != null) {
					this.getEntityManager()
						.remove(this.getEntityManager().merge(dbEntity));	// TODO revisar
					M deletedModelObj =  this.dbEntityToModelObject(securityContext,
														  	  		dbEntity);
					if (deletedEntities == null) deletedEntities = Lists.newArrayList();
					deletedEntities.add(deletedModelObj);
				}
			}
		} else {
			log.trace("No versions to delete for a {} entity with oid={}",_DBEntityType,oid);
		}

		// [3] - Compose the result and return
		CRUDOnMultipleResult<M> outDeleteResults = CRUDResultBuilder.using(securityContext)
																	.<M>onVersionable(_modelObjectType)
																	.allDBEntitiesDeleted(allVersionEntities)
																		.transformedToModelObjectUsing(_dbEntityIntoModelObjectTransformer);
		return outDeleteResults;
	}
	@Override
	public CRUDOnMultipleResult<M> deleteAllVersions(final SecurityContext securityContext,
													 final VersionIndependentOID oid,
													 final PersistenceOperationCallbackSpec callbackSpec) {
		throw new IllegalStateException(Throwables.message("Implemented at service level (see {}",
														   CRUDServicesForVersionableModelObjectDelegateBase.class));
	}
}
