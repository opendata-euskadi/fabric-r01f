package r01f.patterns.eventbus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NoArgsConstructor;
import r01f.patterns.Command;

/**
 * Holds an index of the {@link EventHandler} by source and by {@link EventType}
 * The additions and removals of 
 */
@NoArgsConstructor
class EventHandlerIndex {
	/**
	 * {@link EventHandler}s by source indexed by {@link EventType}
	 */
	private final Map<EventType<?>,Map<Object,List<?>>> _handlerIndex = new HashMap<EventType<?>,Map<Object,List<?>>>();
	/**
	 * Add and remove operations received during dispatch (those operations are queued)
	 */
	private List<Command> _deferredAdditionsOrRemovals;
/////////////////////////////////////////////////////////////////////////////////////////
//  GET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a handler for a source
	 * @param type
	 * @param source
	 * @return
	 */
	public <H> List<H> getHandlersForSource(final EventType<H> type, 
											final Object source) {
		// [1] - Get a Map of Handlers by source for the provided type
		Map<Object, List<?>> handlersOfType = _handlerIndex.get(type);
		if (handlersOfType == null) return Collections.emptyList();
		// [2] - Safe return
		@SuppressWarnings("unchecked")
		List<H> handlersBySource = (List<H>)handlersOfType.get(source);
		if (handlersBySource == null) return Collections.emptyList();
		return handlersBySource;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ADD
/////////////////////////////////////////////////////////////////////////////////////////
	public <H> void addHandlerForSourceNow(final EventType<H> type, 
							   			   final Object source,final H handler) {
		Map<Object,List<?>> handlersBySourceForType = _handlerIndex.get(type);
		if (handlersBySourceForType == null) {
			// Create space for handlers of type
			handlersBySourceForType = new HashMap<Object,List<?>>();
			_handlerIndex.put(type,new HashMap<Object,List<?>>());
		}
		// put the handler
		@SuppressWarnings("unchecked")
		List<H> handlers = (List<H>)handlersBySourceForType.get(source);
		if (handlers == null) {
			// Create space for handlers by source
			handlers = new ArrayList<H>();
			handlersBySourceForType.put(source,handlers);	// put the handler indexed by source object
		}
		handlers.add(handler);
	}
	public <H> void addHandlerForSourceLater(final EventType<H> type,
								 			 final Object source,final H handler) {
		_defer(new Command() {
						@Override
						public void execute() {
							EventHandlerIndex.this.addHandlerForSourceNow(type,
																		  source,handler);
						}
			   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	public <H> void removeHandlerForSourceNow(final EventType<H> type, 
								  			  final Object source,final H handler) {
		List<H> handlersForSource = this.getHandlersForSource(type,source);
		boolean removed = handlersForSource.remove(handler);
		if (removed && handlersForSource.isEmpty()) {
			// remove from index
			Map<Object,List<?>> sourceMap = _handlerIndex.get(type);
			List<?> pruned = sourceMap.remove(source);
	
			assert pruned != null : "Can't prune what wasn't there";
			assert pruned.isEmpty() : "Pruned unempty list!";
	
			if (sourceMap.isEmpty()) _handlerIndex.remove(type);
		}
	}
	public <H> void removeHandlerForSourceLater(final EventType<H> type,
								    		    final Object source,final H handler) {
		_defer(new Command() {
						@Override
						public void execute() {
							EventHandlerIndex.this.removeHandlerForSourceNow(type,
													   						 source,handler);
						}
			   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void handleQueuedAddsAndRemoves() {
		if (_deferredAdditionsOrRemovals != null) {
			try {
				for (Command c : _deferredAdditionsOrRemovals) {
					c.execute();
				}
			} finally {
				_deferredAdditionsOrRemovals = null;	// no pending additions or removals
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private void _defer(final Command command) {
		if (_deferredAdditionsOrRemovals == null) _deferredAdditionsOrRemovals = new ArrayList<Command>();
		_deferredAdditionsOrRemovals.add(command);
	}
}
