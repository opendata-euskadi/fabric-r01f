package r01f.services.delegates.persistence;

import com.google.common.eventbus.EventBus;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.services.delegates.ServicesDelegateBase;
import r01f.services.interfaces.ServiceInterface;

@Accessors(prefix="_")
public abstract class PersistenceServicesDelegateBase
			  extends ServicesDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceServicesDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										   final ServiceInterface serviceImpl,
										   final EventBus eventBus) {
		super(coreCfg,
			  serviceImpl,
			  eventBus);
	}
	public PersistenceServicesDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										   final ServiceInterface serviceImpl) {
		this(coreCfg,
			 serviceImpl,
			 null);			// no event bus
	}
	public PersistenceServicesDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										   final EventBus eventBus) {
		this(coreCfg,
			 null,			// no service impl
			 eventBus);		
	}
}
