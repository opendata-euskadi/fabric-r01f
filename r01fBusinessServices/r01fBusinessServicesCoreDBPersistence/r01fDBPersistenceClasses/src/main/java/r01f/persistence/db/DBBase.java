package r01f.persistence.db;


import javax.inject.Provider;
import javax.persistence.EntityManager;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.HasMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.config.DBSpec;


/**
 * Base type for every persistence layer type
 */
@Accessors(prefix="_")
public abstract class DBBase
	       implements HasEntityManager,
	       			  HasMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//  NOT INJECTED STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The entity manager obtained from the {@link EntityManager} {@link Provider}
	 */
	@Getter protected final EntityManager _entityManager;
	/**
	 * The db config
	 */
	@Getter protected final DBModuleConfig _dbConfig;
	/**
	 * The db spec
	 */
	@Getter protected final DBSpec _dbSpec;
	/**
	 * Marshaller
	 */
	@Getter protected final Marshaller _modelObjectsMarshaller;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBBase(final DBModuleConfig dbCfg,
				  final EntityManager entityManager,
				  final Marshaller marshaller) {
		_entityManager = entityManager;
		_dbConfig = dbCfg;
		_dbSpec = DBSpec.usedAt(_entityManager);	// maybe ca be get directly from dbconfig??
		_modelObjectsMarshaller = marshaller;
	}
}
