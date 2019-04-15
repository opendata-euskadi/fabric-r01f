package r01f.events.crud;

import lombok.extern.slf4j.Slf4j;
import r01f.events.PersistenceOperationEventListeners.PersistenceOperationErrorEventListener;
import r01f.events.PersistenceOperationEvents.PersistenceOperationErrorEvent;
import r01f.model.persistence.PersistenceOperationError;

import com.google.common.eventbus.Subscribe;

/**
 * Default {@link PersistenceOperationErrorEvent}s listener that simply logs the op NOK events
 * @param <R>
 */
@Slf4j
public class CRUDOperationErrorEventListener
  implements PersistenceOperationErrorEventListener {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Subscribe	// subscribes this event listener at the EventBus
	@Override
	public void onPersistenceOperationError(final PersistenceOperationErrorEvent opErrorEvent) {
		// Persistence CRUD operation error
		PersistenceOperationError opError = opErrorEvent.getResultAsOperationError();
		if (opError.wasBecauseAClientError()) {
			// do not polute log with client errors ;-)
			log.info("Client Error: {}",opError.getDetailedMessage());
		} else {
			log.error("======================= [{}] OperationNOK event=====================\n{}",
					  CRUDOperationErrorEventListener.class,opError.getDetailedMessage(),opError.getPersistenceException());
		}
	}
}
