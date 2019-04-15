package r01f.events;

import r01f.events.PersistenceOperationEvents.PersistenceOperationErrorEvent;
import r01f.events.PersistenceOperationEvents.PersistenceOperationOKEvent;



/**
 * Event listeners
 */
public class PersistenceOperationEventListeners {
/////////////////////////////////////////////////////////////////////////////////////////
//  Listens to OK PersistenceOperationOKEvent
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface PersistenceOperationOKEventListener {
		public void onPersistenceOperationOK(final PersistenceOperationOKEvent opOKEvent);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Listens to NOK PersistenceOperationErrorEvent
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface PersistenceOperationErrorEventListener {
		public void onPersistenceOperationError(final PersistenceOperationErrorEvent opErrorEvent);
	}	
}
