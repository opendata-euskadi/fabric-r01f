package r01f.persistence.db;

import javax.persistence.EntityManager;

import lombok.experimental.Accessors;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.FindResult;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.DBEntityForVersionableModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForVersionableModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.FindServicesForVersionableModelObject;

/**
 * Base type for every persistence layer type
 * @param <O>
 * @param <M>
 * @param <PK>
 * @param <DB>
 */
@Accessors(prefix="_")
public abstract class DBFindForVersionableModelObjectBase<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet,
							     						  PK extends DBPrimaryKeyForVersionableModelObject,DB extends DBEntity & DBEntityForVersionableModelObject<PK>>
			  extends DBFindForModelObjectBase<O,M,
			  				 				   PK,DB>
	       implements FindServicesForVersionableModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	public DBFindForVersionableModelObjectBase(final DBModuleConfig dbCfg,
											   final Class<M> modelObjectType,final Class<DB> dbEntityType,
											   final EntityManager entityManager,
											   final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  entityManager,
			  marshaller);
	}
	public DBFindForVersionableModelObjectBase(final Class<M> modelObjectType,final Class<DB> dbEntityType,
											   final DBModuleConfig dbCfg,
											   final EntityManager entityManager,
											   final Marshaller marshaller) {
		super(modelObjectType,dbEntityType,
			  dbCfg,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<M> findAllVersions(final SecurityContext securityContext) {
		throw new UnsupportedOperationException("NOT implemented!");
	}
}
