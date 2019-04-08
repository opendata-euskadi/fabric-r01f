package r01f.persistence.callback;

import r01f.model.persistence.HasPersistenceOperationResult;
import r01f.model.persistence.PersistenceOperationError;
import r01f.model.persistence.PersistenceOperationOK;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence operation callback interface
 * How callbacks works
 * ===================
 * The callback mechanishm works as depicted in the following picture:
 * <pre>
 *     +--------+   +------+       +----+ +-----------+ +--------+ +------------+
 *     |Callback|   |Client|       |CORE| |Persistence| |EventBus| |EventHandler|
 *     |handler |   |      |       |    | |(ie: DB)   | |        | |(async)     |
 *     +---+----+   +--+---+       +--+-+ +-----+-----+ +----+---+ +-----+------+
 *         |           |              |         |            |           |
 *         |           |              |         |            |           |
 *         |           +------------> |         |            |           |
 *         |           |              |         |            |           |
 *         |           |              +-------> |            |           |
 *         |           |              +--------------------> |           |
 *         |           | <------------+         |            |           |
 *         |           |              |         |            +---------> |
 *         |                                                 |           |
 *         | <-----------------------------------------------------------+
 *         |                                                 |           |
 * </pre>
 * [1] - The [client] initiates a [persistence operation] calling a [core] function
 * [2] - The [core] executes it's works usually storing some data in the [DB]
 * [3] - If some [background] (async) work must be done in the [core], an [event]
 * 		 is sent to the [EventBus] to be executed later
 * [4] - The [persistence operation result] is returned to the client
 * 		 BEWARE that this result ONLY tells the client that PART OF THE WORK was done
 * 				(the DB persist)
 * [5] - An [Event Handler] (usually asynchronously) captures the [Event] and 
 * 		 executes some other work (usually heavy work)
 * [6] - The [client] can be informed back about the work performed by the [EventHandler]
 * 		 using the [callback handler]
 * 
 * BEWARE!!!
 * In order to disacouple the [client] logic from the [core], the client only sends 
 * an spec of how to call the [Callback handler]
 * 
 * [client] code:
 * <pre class='brush:java'>
 *		public static class MyPersistenceOperationBeanCallback
 *			 extends PersistenceOperationCallbackBase {
 *			@Override
 *			public void onPersistenceOperationOK(final PersistenceOperationOK opOK) {
 *				System.out.println("CALLBACK OK!!");
 *			}
 *			@Override
 *			public void onPersistenceOperationError(final PersistenceOperationError opError) {
 *				System.out.println("CALLBACK ERROR!!!");
 *			}
 *		}
 * </pre>
 * 
 * [core] code (usually after the [event handler] finishes it's work)
 * <pre class='brush:java'>
 *	private class MyOperationOKEventListener
 *		  extends CRUDOperationOKEventListenerBase {
 *		
 *		public MyOperationOKEventListener(final Class<M> modelObjType) {
 *			super(modelObjType,
 *				  new CRUDOperationOKEventFilter() {
 *							@Override
 *							public boolean hasTobeHandled(final PersistenceOperationOKEvent opEvent) {
 *								PersistenceOperationOK opResult = opEvent.getResultAsOperationOK();
 *								return (opResult instanceof CRUDOK) 
 *								    && (opResult.as(CRUDOK.class).hasBeenCreated() || opResult.as(CRUDOK.class).hasBeenUpdated() || opResult.as(CRUDOK.class).hasBeenDeleted())
 *								    && (opResult.as(CRUDOK.class).getObjectType().isAssignableFrom(modelObjType));
 *							}
 *				  });
 *		}
 *		@Subscribe	// subscribes this event listener at the EventBus
 *		@Override
 *		public void onPersistenceOperationOK(final PersistenceOperationOKEvent opOKEvent) {
 *			if (_crudOperationOKEventFilter.hasTobeHandled(opOKEvent)) {
 *				// DO some HARD WORK
 *				// ...
 *				// ... and tell the [client] about the result
 *				this.sendCallbackFor(opOKEvent);	<--- this is where 
 *			}
 *		}
 *	}
 * </pre>
 * 
 */
public interface PersistenceOperationCallback
		 extends HasPersistenceOperationResult {
/////////////////////////////////////////////////////////////////////////////////////////
// 	CALLBACK METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Callback method called when the persistence operation succeeds
	 * @param securityContext
	 * @param opOK
	 */
	public void onPersistenceOperationOK(final SecurityContext securityContext,
										 final PersistenceOperationOK opOK);	
	/**
	 * Callback method called when the persistence operation fails
	 * @param securityContext
	 * @param opNOK
	 */
	public void onPersistenceOperationError(final SecurityContext securityContext,
											final PersistenceOperationError opError);
}
