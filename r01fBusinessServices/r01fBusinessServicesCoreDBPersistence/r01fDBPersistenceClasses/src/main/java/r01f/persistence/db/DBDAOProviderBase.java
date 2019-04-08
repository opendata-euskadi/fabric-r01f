package r01f.persistence.db;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.objectstreamer.Marshaller;

@RequiredArgsConstructor
public abstract class DBDAOProviderBase<D extends DBBase>
		   implements Provider<D> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final ServicesCoreBootstrapConfigWhenBeanExposed _coreConfig;
	protected final EntityManager _entityManager;
	protected final Marshaller _modelObjectsMarshaller;
}
