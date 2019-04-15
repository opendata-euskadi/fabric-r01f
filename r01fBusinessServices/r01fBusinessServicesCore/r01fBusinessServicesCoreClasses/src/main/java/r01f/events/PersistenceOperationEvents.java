package r01f.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.model.persistence.CRUDError;
import r01f.model.persistence.CRUDOK;
import r01f.model.persistence.HasPersistenceOperationResult;
import r01f.model.persistence.PersistenceOperationError;
import r01f.model.persistence.PersistenceOperationOK;
import r01f.model.persistence.PersistenceOperationResult;
import r01f.model.persistence.PersistenceOperationResults;
import r01f.persistence.callback.spec.HasPersistenceOperationCallbackSpec;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.securitycontext.HasSecurityContext;
import r01f.securitycontext.SecurityContext;

/**
 * CRUD operation events
 */
public class PersistenceOperationEvents {
/////////////////////////////////////////////////////////////////////////////////////////
//  Base type
/////////////////////////////////////////////////////////////////////////////////////////
	public interface PersistenceOperationEvent 
			extends HasSecurityContext,
			   	    HasPersistenceOperationResult,
			   	    HasPersistenceOperationCallbackSpec,
			   		Debuggable {
		public boolean isForPersistenceOperationOK();
		public boolean isForPersistenceOperationError();
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	static abstract class PersistenceOperationEventBase
			   implements PersistenceOperationEvent {
		/**
		 * The user context
		 */
		@Getter private final SecurityContext _securityContext;
		/**
		 * The operation result 
		 */
		@Getter protected final PersistenceOperationResult _persistenceOperationResult;
		/**
		 * The async callback
		 */
		@Getter protected final PersistenceOperationCallbackSpec _callbackSpec;
		
		@Override
		public boolean hasSucceeded() {
			return _persistenceOperationResult.hasSucceeded();
		}
		@Override
		public boolean hasFailed() {
			return _persistenceOperationResult.hasFailed();
		}
		@Override
		public CharSequence debugInfo() {
			return PersistenceOperationResults.debugInfoOf(_persistenceOperationResult);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Operation OK
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class PersistenceOperationOKEvent 
		        extends PersistenceOperationEventBase {
		public PersistenceOperationOKEvent(final SecurityContext securityContext,
										   final PersistenceOperationOK opOK,
										   final PersistenceOperationCallbackSpec callbackSpec) {
			super(securityContext,
				  opOK,
				  callbackSpec);
		}
		public PersistenceOperationOK getResultAsOperationOK() {
			return (PersistenceOperationOK)_persistenceOperationResult;
		}
		@SuppressWarnings("unchecked")
		public <M> CRUDOK<M> getResultAsCRUDOperationOK() {
			return (CRUDOK<M>)_persistenceOperationResult;
		}
		@SuppressWarnings({ "unchecked","unused" })
		public <M> CRUDOK<M> getResultAsCRUDOperationOKOn(final Class<M> modelObjectType) {
			return (CRUDOK<M>)_persistenceOperationResult;
		}
		@Override
		public boolean isForPersistenceOperationOK() {
			return true;
		}
		@Override
		public boolean isForPersistenceOperationError() {
			return false;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Operation NOK
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class PersistenceOperationErrorEvent 
		 		extends PersistenceOperationEventBase {
		public PersistenceOperationErrorEvent(final SecurityContext securityContext,
									 		  final PersistenceOperationError opNOK,
									 		  final PersistenceOperationCallbackSpec callbackSpec) {
			super(securityContext,
				  opNOK,
				  callbackSpec);
		}
		public PersistenceOperationError getResultAsOperationError() {
			return (PersistenceOperationError)_persistenceOperationResult;
		}
		@SuppressWarnings("unchecked")
		public <M> CRUDError<M> getResultAsCRUDOperationError() {
			return (CRUDError<M>)_persistenceOperationResult;
		}
		@Override
		public boolean isForPersistenceOperationOK() {
			return false;
		}
		@Override
		public boolean isForPersistenceOperationError() {
			return true;
		}
	}
}
