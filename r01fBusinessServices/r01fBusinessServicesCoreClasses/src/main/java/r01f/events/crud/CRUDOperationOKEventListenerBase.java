package r01f.events.crud;

import org.slf4j.Logger;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.events.PersistenceOperationEventListeners.PersistenceOperationOKEventListener;
import r01f.events.PersistenceOperationEvents.PersistenceOperationEvent;
import r01f.events.PersistenceOperationEvents.PersistenceOperationOKEvent;
import r01f.model.ModelObject;
import r01f.model.persistence.CRUDOK;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceOperationError;
import r01f.model.persistence.PersistenceOperationOK;
import r01f.persistence.callback.PersistenceOperationCallback;
import r01f.persistence.callback.spec.PersistenceOperationBeanCallbackSpec;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.reflection.ReflectionUtils;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;

/**
 * Listener to {@link PersistenceOperationOKEvent}s thrown by the persistence layer through the {@link EventBus}
 * @param <M>
 */
@Slf4j
@Accessors(prefix="_")
public abstract class CRUDOperationOKEventListenerBase 
           implements PersistenceOperationOKEventListener {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The type is needed because guava's event bus does NOT supports generic event types
	 * 	- {@link CRUDOperationEvent} is a generic type parameterized with the persistable model object type, 
	 *  - Subscriptions to the event bus are done by event type, that's by {@link CRUDOperationEvent} type
	 *  - BUT since guava {@link EventBus} does NOT supports generics, the subscriptions are done to the raw {@link CRUDOperationEvent}
	 *  - ... so ALL listeners will be attached to the SAME event type: {@link CRUDOperationEvent}
	 *  - ... and ALL listeners will receive {@link CRUDOperationEvent} events
	 *  - ... but ONLY one should handle it.
	 * In order for the event handler (listener) to discriminate events to handle, the model object's type
	 * is used (see {@link #_hasToBeHandled(CRUDOperationEvent)} method)
	 */
	protected final Class<? extends ModelObject> _type;
	/**
	 * Filters the events using the afected model object
	 */
	protected final transient CRUDOperationOKEventFilter _crudOperationOKEventFilter;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDOperationOKEventListenerBase(final Class<? extends ModelObject> type) {
		_type = type;
		_crudOperationOKEventFilter = new CRUDOperationOKEventFilter() {
												@Override @SuppressWarnings("unchecked")
												public boolean hasTobeHandled(final PersistenceOperationOKEvent opEvent) {
													CRUDResult<? extends ModelObject> opResult = opEvent.getPersistenceOperationResult()
																				    					.as(CRUDResult.class);
													// the event refers to the same model object type THIS event handler handles;
													return ReflectionUtils.isSubClassOf(opResult.as(CRUDOK.class).getObjectType(),
																						_type);
												}
									  };
	}
	public CRUDOperationOKEventListenerBase(final Class<? extends ModelObject> type,
											final CRUDOperationOKEventFilter crudOperationOKEventFilter) {
		_type = type;
		_crudOperationOKEventFilter = crudOperationOKEventFilter;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEFAULT DEAD EVENT LISTENER
/////////////////////////////////////////////////////////////////////////////////////////	
	@Subscribe
	public void handleDeadEvent(final DeadEvent deadEvent) {
		log.warn("> Not handled event:  {}",
				 deadEvent.getEvent());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if:
	 * 	1.- the event refers to an object of the type handled by this listener
	 *  2.- the event refers to a successful create or update operation 
	 * @param opEvent
	 * @return
	 */
	protected boolean _isEventForSuccessfulCreateUpdateOrDelete(final PersistenceOperationOKEvent opEvent) {	
		PersistenceOperationOK opResult = opEvent.getResultAsOperationOK();
		boolean handle = _crudOperationOKEventFilter.hasTobeHandled(opEvent);
		if (!handle) return false;
		
		return ((opResult.isCRUDOK()) 
			 && (opResult.as(CRUDOK.class).hasBeenCreated() || opResult.as(CRUDOK.class).hasBeenUpdated() || opResult.as(CRUDOK.class).hasBeenDeleted()));	// it's a create, update or delete event
	}
	/**
	 * Returns true if:
	 * 	1.- the event refers to an object of the type handled by this listener
	 *  2.- the event refers to a successful create or update operation 
	 * @param opEvent
	 * @return
	 */
	protected boolean _isEventForSuccessfulCreateOrUpdate(final PersistenceOperationOKEvent opEvent) {	
		PersistenceOperationOK opResult = opEvent.getResultAsOperationOK();
		boolean handle = _crudOperationOKEventFilter.hasTobeHandled(opEvent);
		if (!handle) return false;
		
		return ((opResult.isCRUDOK()) 
			 && (opResult.as(CRUDOK.class).hasBeenCreated() || opResult.as(CRUDOK.class).hasBeenUpdated()));	// it's a create or update event
	}
	/**
	 * Returns true if:
	 * 	1.- the event refers to an object of the type handled by this listener
	 *  2.- the event refers to a successful create 
	 * @param opEvent
	 * @return
	 */
	protected boolean _isEventForSuccessfulCreate(final PersistenceOperationOKEvent opEvent) {
		PersistenceOperationOK opResult = opEvent.getResultAsOperationOK();
		boolean handle = _crudOperationOKEventFilter.hasTobeHandled(opEvent);
		if (!handle) return false;
	
		return ((opResult.isCRUDOK()) 
			 && (opResult.as(CRUDOK.class).hasBeenCreated()));												// it's a create event
	}
	/**
	 * Returns true if:
	 * 	1.- the event refers to an object of the type handled by this listener
	 *  2.- the event refers to a successful update operation 
	 * @param opEvent
	 * @return
	 */
	protected boolean _isEventForSuccessfulUpdate(final PersistenceOperationOKEvent opEvent) {
		PersistenceOperationOK opResult = opEvent.getResultAsOperationOK();
		boolean handle = _crudOperationOKEventFilter.hasTobeHandled(opEvent);
		if (!handle) return false;
		
		return ((opResult.isCRUDOK()) 
			 && (opResult.as(CRUDOK.class).hasBeenUpdated()));								// it's an update event
	}
	/**
	 * Returns true if:
	 * 	1.- the event refers to an object of the type handled by this listener
	 *  2.- the event refers to a successful delete operation 
	 * @param opEvent
	 * @return
	 */
	protected boolean _isEventForSuccessfulDelete(final PersistenceOperationOKEvent opEvent) {
		PersistenceOperationOK opResult = opEvent.getResultAsOperationOK();
		boolean handle = _crudOperationOKEventFilter.hasTobeHandled(opEvent);
		if (!handle) return false;
		
		return ((opResult.isCRUDOK()) 
			 && (opResult.as(CRUDOK.class).hasBeenDeleted()));								// it's a delete event
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	CALLBACK SEND
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sends a callback 
	 * @param securityContext
	 * @param event
	 */
	public void sendCallbackFor(final SecurityContext securityContext,
								final PersistenceOperationEvent event) {
		if (event.getCallbackSpec() != null) {			
			PersistenceOperationCallback callback = _createCallbackInstance(event.getCallbackSpec());
			if (event.isForPersistenceOperationOK()) {
				callback.onPersistenceOperationOK(securityContext,
												  event.getPersistenceOperationResult()
													   .as(PersistenceOperationOK.class));
			} 
			else if (event.isForPersistenceOperationError()) {
				callback.onPersistenceOperationError(securityContext,
													 event.getPersistenceOperationResult()
														  .as(PersistenceOperationError.class));
			}
		} else {
			System.out.println("NO CALLBACK!!!!!!");
		}
	}
	private PersistenceOperationCallback _createCallbackInstance(final PersistenceOperationCallbackSpec callbackSpec) {
		PersistenceOperationCallback callback = null;
		if (callbackSpec instanceof PersistenceOperationBeanCallbackSpec) {
			PersistenceOperationBeanCallbackSpec beanCallbackSpec = (PersistenceOperationBeanCallbackSpec)callbackSpec;
			Class<? extends PersistenceOperationCallback> callbackType = beanCallbackSpec.getImplType();
			callback = ReflectionUtils.createInstanceOf(callbackType);
		} 
		else {
			throw new UnsupportedOperationException();
		}
		return callback;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	protected void _debugEvent(final Logger log,
						  	   final PersistenceOperationOKEvent opOKEvent,final boolean hasToBeHandled) {
		if (log.isTraceEnabled()) {
			log.trace(_debugEvent(opOKEvent,
						  		  hasToBeHandled));
		} else if (log.isDebugEnabled() && hasToBeHandled) {
			log.debug(_debugEvent(opOKEvent,
						  		  hasToBeHandled));
		}
	}
	protected String _debugEvent(final PersistenceOperationOKEvent opOKEvent,
							   	 final boolean hasToBeHandled) {
		return Strings.customized("EventListener registered for events of [{}] entities\n" + 
						  		  "{}\n" + 
						  		  "Handle the event: {}",
					  			  _type,
							  	  opOKEvent.debugInfo(),
							  	  hasToBeHandled);
	}
}
