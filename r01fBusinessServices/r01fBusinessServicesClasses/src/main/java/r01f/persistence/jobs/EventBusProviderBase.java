package r01f.persistence.jobs;

import javax.inject.Provider;

import com.google.common.eventbus.EventBus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.types.ExecutionMode;

/**
 * Provides event buses
 * (a provider is used since it's injected and can be used while guice bootstraping)
 */
@Slf4j
@RequiredArgsConstructor
abstract class EventBusProviderBase
    implements Provider<EventBus> {
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Execution mode
	 */
	protected final ExecutionMode _execMode;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  NOT INJECTED FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected EventBus _eventBusInstance = null;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected abstract EventBus _createEventBusInstance();
	@Override
	public EventBus get() {
		if (_eventBusInstance != null) return _eventBusInstance;
		
		log.warn("Creating a {} event bus",_execMode); 
		_eventBusInstance = _createEventBusInstance();
		return _eventBusInstance;
	}
}