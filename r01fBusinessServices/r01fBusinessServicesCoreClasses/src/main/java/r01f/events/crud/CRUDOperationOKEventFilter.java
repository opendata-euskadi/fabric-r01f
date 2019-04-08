package r01f.events.crud;

import r01f.events.PersistenceOperationEvents.PersistenceOperationOKEvent;

public interface CRUDOperationOKEventFilter {
	public boolean hasTobeHandled(PersistenceOperationOKEvent opEvent);
}
