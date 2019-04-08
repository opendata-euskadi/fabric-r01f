package r01f.patterns.eventbus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.eventbus.EventBus;

import lombok.NoArgsConstructor;
import r01f.exceptions.MultiException;

/**
 * A simple event bus inspired by GWT implementation
 * (The guava {@link EventBus} implementation is NOT used since it cannot be used in GWT client-side code)
 * 
 * GWT's SimpleEventBus is NOT used to maintain this library free of the GWT dependency
 */
@NoArgsConstructor
public class SimpleEventBus {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * {@link EventHandler}s by source indexed by {@link EventType}
	 */
	private final EventHandlerIndex _handlerIndex = new EventHandlerIndex();
	/**
	 * While firing events, all handler registrations (additions) or removals are DEFERRED (enqueued)
	 * to be processed later
	 * This variable counts the number of fired events being dispatched.
	 * Only when it's value is 0 (no events being dispatched) the handler additions are done at the moment,
	 * when it's value is > 0 (events being dispatched) the handler additions are done later when every 
	 * event has been dispatched
	 */
	private int _firingDepth = 0;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public <H> HandlerRegistration addHandler(final EventType<H> type,
											  final H handler) {
		return _addHandlerForSource(type, 
					  				null,handler);		// null source = global handler
	}
	public <H> HandlerRegistration addHandlerToSource(final EventType<H> type,
													  final Object source,
													  final H handler) {
		if (source == null) throw new NullPointerException("Cannot add a handler with a null source");
		return _addHandlerForSource(type,
					  				source,handler);
	}
	private <H> HandlerRegistration _addHandlerForSource(final EventType<H> type,
										   				 final Object source,final H handler) {
		if (type == null) throw new NullPointerException("Cannot add a handler with a null type");
		if (handler == null) throw new NullPointerException("Cannot add a null handler");

		if (_firingDepth > 0) {
			_handlerIndex.addHandlerForSourceLater(type, 
												   source,handler);
		} else {
			_handlerIndex.addHandlerForSourceNow(type, 
					  							 source,handler);
		}
		return new HandlerRegistration() {
						@Override
						public void removeHandler() {
						    if (_firingDepth > 0) {
						    	_handlerIndex.removeHandlerForSourceLater(type, 
						    				   				   			  source,handler);
						    } else {
						    	_handlerIndex.removeHandlerForSourceNow(type,
						    				 			   				source,handler);
						    }
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void fireEvent(final EventBase<?> event) {
		_doFire(event, null);
	}
	public void fireEventFromSource(final EventBase<?> event, 
									final Object source) {
		if (source == null) throw new NullPointerException("Cannot fire from a null source");
		_doFire(event, source);
	}
	private <H extends EventHandler> void _doFire(final EventBase<H> event, 
												  final Object source) {
		if (event == null) throw new NullPointerException("Cannot fire null event");
		try {
			_firingDepth++;
			if (source != null) event.setSource(source);

			// [1] - Get the handlers registered for the source
			//		 (note that there can be events pending to be added or removed from the index)
			List<H> handlers = _getDispatchList(event.getAssociatedType(),
											    source);
			// [2] - Dispatch the events
			Set<Throwable> errorCauses = null;
			for (H handler : handlers) {
				try {
					event.dispatch(handler);
				} catch (Throwable e) {
					if (errorCauses == null) errorCauses = new HashSet<Throwable>();
					errorCauses.add(e);
				}
			}
			// Throw an exception for all the dispatching errors (if there're some)
			if (errorCauses != null) throw new MultiException(errorCauses);
		} finally {
			_firingDepth--;
			// If all events have been fired, it's time to process the pending index additions and removals
			if (_firingDepth == 0) _handlerIndex.handleQueuedAddsAndRemoves();
		}
	}
	private <H> List<H> _getDispatchList(final EventType<H> type, 
										 final Object source) {
		List<H> directHandlers = _handlerIndex.getHandlersForSource(type,
																	source);
		if (source == null) return directHandlers;

		List<H> globalHandlers = _handlerIndex.getHandlersForSource(type, 	
																   	null);	// source = null --> global handler
		List<H> rtn = new ArrayList<H>(directHandlers);
		rtn.addAll(globalHandlers);
		return rtn;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////

}
